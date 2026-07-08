<!-- 结构,主体 -->
<template>
  <div class="chat-container">
    <!-- 左侧侧边栏：历史会话 -->
    <aside class="sidebar" :class="{ collapsed: isSidebarCollapsed }">
      <div class="sidebar-header">
        <button class="new-chat-btn" @click="startNewChat">
          <span class="icon">+</span>
          <span v-if="!isSidebarCollapsed">新建对话</span>
        </button>
        <button class="toggle-sidebar-btn" @click="toggleSidebar">
          {{ isSidebarCollapsed ? "▶" : "◀" }}
        </button>
      </div>

      <div class="history-list">
        <div
          v-for="(chat, index) in historyList"
          :key="chat.sessionId"
          class="history-item"
          :class="{ active: currentChatId === chat.sessionId }"
          @click="switchChat(chat.sessionId)"
        >
          <span class="chat-icon">💬</span>
          <span v-if="!isSidebarCollapsed" class="chat-title">{{
            chat.title
          }}</span>
        </div>
      </div>

      <div class="sidebar-footer" v-if="!isSidebarCollapsed">
        <div class="user-profile">
          <div class="avatar">U</div>
          <span>用户账号</span>
        </div>
      </div>
    </aside>

    <!-- 右侧主聊天区域 -->
    <main class="chat-main">
      <!-- 顶部标题栏 (可选) -->
      <header class="chat-header">
        <span class="model-selector">默认模型 ▼</span>
      </header>

      <!-- 消息列表区域 -->
      <div class="messages-wrapper" ref="messagesContainer">
        <div v-if="messages.length === 0" class="empty-state">
          <h2>有什么可以帮你的吗？</h2>
          <div class="suggestion-chips">
            <span class="chip" @click="fillInput('帮我写一个Python脚本')"
              >帮我写一个Python脚本</span
            >
            <span class="chip" @click="fillInput('解释一下量子纠缠')"
              >解释一下量子纠缠</span
            >
            <span class="chip" @click="fillInput('制定一周健身计划')"
              >制定一周健身计划</span
            >
          </div>
        </div>

        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="message-row"
          :class="msg.role"
        >
          <div class="avatar-area">
            <div class="avatar" :class="msg.role">
              {{ msg.role === "USER" ? "U" : "AI" }}
            </div>
          </div>
          <div class="message-content">
            <!--<div
              class="message-text"
              v-html="renderMarkdown(msg.content)"
            ></div> -->
            <div
              class="message-text"
              v-html="renderMarkdown(msg.content)"
            ></div>
          </div>
        </div>

        <!-- 加载中状态 -->
        <div v-if="isLoading" class="message-row ai">
          <div class="avatar-area">
            <div class="avatar ai">AI</div>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入框区域 -->
      <div class="input-area">
        <div class="input-box">
          <textarea
            v-model="inputContent"
            placeholder="输入消息..."
            @keydown.enter.prevent="sendMessage"
            rows="1"
            ref="textareaRef"
          ></textarea>
          <button
            class="send-btn"
            :disabled="!inputContent.trim() || isLoading"
            @click="sendMessage"
          >
            <span v-if="!isLoading">发送</span>
            <span v-else class="loading-spinner">⟳</span>
          </button>
        </div>
        <div class="input-footer">AI 生成内容可能不准确，请核实重要信息。</div>
      </div>
    </main>
  </div>
</template>

<!-- 交互,脚本语言 -->
<script lang="ts" setup>
import { ref, nextTick, onMounted } from "vue";
import MarkdownIt from 'markdown-it';
import DOMPurify from 'dompurify';
import { getHistoryList, getHistory } from "@/utils/aiChat";


const md = new MarkdownIt({
  breaks: true,      // 允许换行符转换为 <br>
  linkify: true,     // 自动识别链接并转换为 <a> 标签
  typographer: true, // 启用一些语言中立的替换+引号美化
  html: true         // 允许 HTML 标签（注意：后续有 DOMPurify 净化，所以是安全的）
});

// --- 类型定义 ---
interface Message {
  role: "USER" | "AI";
  content: string;
}

interface HistoryChat {
  sessionId: number;
  title: string;
}

// --- 响应式数据 ---
const isSidebarCollapsed = ref(false);
const currentChatId = ref<number>(Date.now());
const inputContent = ref("");
const isLoading = ref(false);
const messagesContainer = ref<HTMLDivElement | null>(null);
const textareaRef = ref<HTMLTextAreaElement | null>(null);

// 模拟历史数据
const historyList = ref<HistoryChat[]>([]);

// 当前聊天消息
const messages = ref<Message[]>([]);

// --- 方法 ---

// 切换侧边栏
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};

// 开始新对话
const startNewChat = () => {
  const newId = Date.now();
  historyList.value.unshift({ sessionId: newId, title: "新对话" });
  currentChatId.value = newId;
  messages.value = [];
};

// 切换历史对话
const switchChat = async (id: number) => {
  messages.value = [];
  currentChatId.value = id;
  // 实际项目中这里应通过 id 从后端或本地存储加载历史消息
  const data = await getHistory(id.toString());
  console.log("getHistory:", data);
  data.data.forEach((item) => {
    // console.log("item:", item);
    if (item.type === "USER") {
      messages.value.push({
        role: "USER",
        content: item.contents[0].text,
      });
    } else if (item.type === "AI") {
      messages.value.push({
        role: "AI",
        content: item.text,
      });
    }
  });
  console.log("switchChat:", messages.value);
  // messages.value = data.data;
};

// 填充输入框
const fillInput = (text: string) => {
  inputContent.value = text;
  if (textareaRef.value) {
    textareaRef.value.focus();
  }
};

// 使用 marked 和 dompurify 渲染 Markdown
const renderMarkdown = (text: string) => {
  if (!text) return "";

  const rawHtml = md.render(text) as string;
  return DOMPurify.sanitize(rawHtml);
};

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

// 发送消息
const sendMessage = async () => {
  if (!inputContent.value.trim() || isLoading.value) return;

  const userMsg = inputContent.value;
  // 1. 添加用户消息
  messages.value.push({ role: "USER", content: userMsg });
  inputContent.value = "";

  // 重置 textarea 高度
  if (textareaRef.value) {
    textareaRef.value.style.height = "auto";
  }

  // 2. 添加空的 AI 消息占位
  messages.value.push({ role: "AI", content: "" });
  isLoading.value = true;

  await scrollToBottom();

  // 3. 调用真实流式接口
  try {
    await fetchStreamResponse(userMsg);
  } catch (error) {
    console.error("AI 响应失败:", error);
    messages.value[messages.value.length - 1].content =
      "抱歉，连接 AI 服务时出现错误。";
  } finally {
    isLoading.value = false;
  }
};

// 核心：处理流式响应// 核心：处理流式响应（实现流式打印）
const fetchStreamResponse = async (prompt: string) => {
  const apiUrl = "/api/ai/chat/stream";

  try {
    const response = await fetch(apiUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: localStorage.getItem("token") || "",
      },
      body: JSON.stringify({
        memoryId: currentChatId.value,
        message: prompt,
      }),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    if (!response.body) {
      throw new Error("Response body is null");
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder("utf-8");

    // 【关键1】用于处理被截断的行
    let buffer = "";
    // 【关键2】获取当前正在生成的 AI 消息索引
    const aiMessageIndex = messages.value.length - 1;

    while (true) {
      const { done, value } = await reader.read();

      // 如果流结束，退出循环
      if (done) {
        break;
      }

      if (value) {
        // 1. 解码并拼接到缓冲区
        const chunk = decoder.decode(value, { stream: true });
        buffer += chunk;

        // 2. 按行分割
        const lines = buffer.split("\n");
        // 保留最后一行（可能不完整）
        buffer = lines.pop() || "";

        // 3. 遍历完整的行进行解析
        for (const line of lines) {
          const trimmedLine = line.trim();
          if (!trimmedLine) continue;

          // 假设后端返回格式为: data: 内容
          if (trimmedLine.startsWith("data:")) {
            const content = trimmedLine.slice(5); // 去掉 "data:"
            console.log("content: ", content);
            if (content === "[DONE]") {
              // 流结束，标记为非流式状态，触发最终完整渲染
              return; // 结束
            }

            // 【关键3】实时更新 Vue 响应式数据
            // 这会触发 Vue 重新渲染，实现“流式打印”视觉效果
            messages.value[aiMessageIndex].content += content;

            // 【关键4】每次更新后滚动到底部，保持视线跟随
            await scrollToBottom();
          }
        }
      }
    }
  } catch (error) {
    console.error("Stream fetch error:", error);
    messages.value[messages.value.length - 1].content += "\n[连接错误]";
  }
  switchChat(currentChatId.value);
};

// 自动调整 Textarea 高度
const adjustTextareaHeight = () => {
  if (textareaRef.value) {
    textareaRef.value.style.height = "auto";
    textareaRef.value.style.height = `${textareaRef.value.scrollHeight}px`;
  }
};

// 获取历史聊天
async function getHistoryChats() {
  const data = await getHistoryList();
  historyList.value = data.data;
}

onMounted(() => {
  if (textareaRef.value) {
    textareaRef.value.addEventListener("input", adjustTextareaHeight);
  }
  getHistoryChats();
});
</script>

<!-- 样式 -->
<style scoped>
/* 全局容器 */
.chat-container {
  display: flex;
  height: 100vh;
  width: 100%;
  background-color: #f7f7f8;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica,
    Arial, sans-serif;
  color: #333;
  overflow: hidden;
}

/* 左侧侧边栏 */
.sidebar {
  width: 260px;
  background-color: #f9f9fa;
  border-right: 1px solid #e5e5e5;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.new-chat-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  background-color: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  color: #333;
}

.new-chat-btn:hover {
  background-color: #f0f0f0;
}

.toggle-sidebar-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 12px;
  color: #666;
  padding: 5px;
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-item:hover {
  background-color: #ececec;
}

.history-item.active {
  background-color: #e5e5e5;
  font-weight: 500;
}

.chat-icon {
  font-size: 16px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #e5e5e5;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #555;
}

/* 右侧主区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  background-color: #fff;
}

.chat-header {
  height: 50px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.model-selector:hover {
  color: #333;
}

/* 消息列表 */
.messages-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
  scroll-behavior: smooth;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.empty-state h2 {
  margin-bottom: 20px;
  font-weight: 500;
}

.suggestion-chips {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 600px;
}

.chip {
  padding: 8px 16px;
  background-color: #f7f7f8;
  border: 1px solid #e5e5e5;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chip:hover {
  background-color: #ececec;
}

.message-row {
  display: flex;
  padding: 20px 0;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
  padding-left: 20px;
  padding-right: 20px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.ai {
  justify-content: flex-start;
}

.avatar-area {
  margin-right: 16px;
  flex-shrink: 0;
}

.message-row.user .avatar-area {
  order: 2;
  margin-right: 0;
  margin-left: 16px;
}

.message-row.user .avatar {
  background-color: #5b8def; /* 用户头像颜色 */
  color: white;
}

.message-row.ai .avatar {
  background-color: #10a37f; /* AI 头像颜色 */
  color: white;
}

.message-content {
  max-width: 80%;
  line-height: 1.6;
  font-size: 15px;
}

.message-row.user .message-content {
  background-color: #f7f7f8;
  padding: 12px 16px;
  border-radius: 12px;
  border-bottom-right-radius: 2px;
}

.message-row.ai .message-content {
  color: #333;
}

/* 打字机动画 */
.typing-indicator span {
  display: inline-block;
  width: 6px;
  height: 6px;
  background-color: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out both;
  margin: 0 2px;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}
.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 底部输入区 */
.input-area {
  padding: 20px;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.input-box {
  position: relative;
  width: 100%;
  max-width: 800px;
  background-color: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: flex-end;
  padding: 10px 14px;
  transition: border-color 0.2s;
}

.input-box:focus-within {
  border-color: #5b8def;
}

textarea {
  flex: 1;
  border: none;
  outline: none;
  resize: none;
  background: transparent;
  font-size: 15px;
  line-height: 1.5;
  max-height: 200px;
  padding: 4px 0;
  font-family: inherit;
}

.send-btn {
  background-color: #5b8def;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  margin-left: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.send-btn:hover:not(:disabled) {
  background-color: #4a7de0;
}

.loading-spinner {
  display: inline-block;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.input-footer {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
  text-align: center;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

::-webkit-scrollbar-track {
  background: transparent;
}
</style>