<!-- 结构,主体 -->
<template>
  <div class="index-container">
    <el-container>
      <el-header class="header">
        <!-- i am index header -->
        <!-- <router-link to="/login">登录</router-link> -->
        <!-- <router-link to="/admin">管理员控制台</router-link> -->
        <router-link to="/index">
          <div class="toIndex">
            <el-icon><House /></el-icon>
            <span>去首页</span>
          </div>
        </router-link>
        <div class="header-right">
          <el-button @click="handleAdminClick">管理员控制台</el-button>
          <userShow />
        </div>
      </el-header>
      <el-main class="main">
        <!-- i am index main -->
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<!-- 交互,脚本语言 -->
<script setup name="index" lang="ts">
import UserShow from "./user/userShow.vue";
import axios from "axios";
import router from "@/router";
import { ElMessage } from "element-plus";

async function handleAdminClick() {
  console.log("校验权限");

  if (!localStorage.getItem("token")) {
    ElMessage.error("请先登录");
  }

  const res = await axios.post("/api/user/check_auth", {
    token: localStorage.getItem("token"),
  });

  if (res.data.code === 200) {
    console.log("有权限");
    // 跳转到管理员控制台
    router.push("/admin");
  } else if (res.data.code === 101) {
    console.log("token验证失败");
    // 提示用户无权限
    ElMessage.error("token验证失败，请重新登录");
    localStorage.removeItem("token");
    localStorage.removeItem("name");
    router.push("/login");
  } else {
    console.log("无权限");
    ElMessage.error("无管理员权限");
    // 跳转到登录页面
    router.push("/index");
  }
}
</script>

<!-- 样式 -->
<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 左右两端对齐 */
  gap: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toIndex {
  display: flex;
  align-items: center;
  gap: 6px; /* 图标和文字之间的间距 */
  padding: 8px 12px; /* 内边距使点击区域更舒适 */
  border-radius: 4px; /* 圆角 */
  text-decoration: none; /* 去除下划线 */
  color: inherit; /* 继承父元素颜色 */
  transition: background-color 0.3s; /* 添加过渡效果 */
}

.toIndex:hover {
  background-color: #f5f5f5; /* 悬停背景色 */
  color: #409eff; /* 悬停文字颜色 */
}

.toIndex .el-icon {
  font-size: 16px; /* 图标大小 */
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toIndex span {
  font-size: 17px; /* 文字大小 */
  white-space: nowrap; /* 防止文字换行 */
}
</style>