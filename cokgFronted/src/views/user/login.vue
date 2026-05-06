<!-- 结构,主体 -->
<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>协同办公知识图谱系统</h2>
        <p>欢迎登录</p>
      </div>
      
      <el-form 
        :model="loginForm" 
        :rules="rules" 
        ref="loginFormRef" 
        class="login-form"
      >
        <el-form-item prop="code">
          <el-input 
            v-model="loginForm.code" 
            placeholder="请输入工号" 
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="Lock"
            show-password
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-button" 
            size="large" 
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<!-- 交互,脚本语言 -->
<script setup lang="ts">
import { ref, reactive } from "vue";
import type { FormInstance, FormRules } from 'element-plus';
import { submitLogin } from "@/utils/identityUtil";
import { ElMessage } from 'element-plus';

// 定义表单数据
const loginForm = reactive({
  code: "",
  password: ""
});

// 定义表单引用
const loginFormRef = ref<FormInstance>();

// 加载状态
const loading = ref(false);

// 表单验证规则
const rules = reactive<FormRules>({
  code: [
    { required: true, message: '请输入工号', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ]
});

// 处理登录逻辑
const handleLogin = async () => {
  if (!loginFormRef.value) return;
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // 假设 submitLogin 返回 Promise
        const res = await submitLogin(loginForm.code, loginForm.password);
        
        // 根据实际接口返回结构调整判断逻辑
        if (res) {
          ElMessage.success('登录成功');
          // TODO: 跳转首页或存储 Token
          // router.push('/');
        } else {
          ElMessage.error('登录失败，请检查工号和密码');
        }
      } catch (error) {
        console.error(error);
        ElMessage.error('网络异常，请稍后重试');
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.warning('请填写完整信息');
      return false;
    }
  });
};
</script>

<!-- 样式 -->
<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  /* 背景渐变，可根据项目主题色调整 */
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-size: cover;
}

.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px); /* 毛玻璃效果 */
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #333;
  font-size: 24px;
  margin-bottom: 10px;
  font-weight: 600;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
  font-weight: 500;
  letter-spacing: 2px;
}

/* 自定义 Element Plus 输入框样式微调 */
:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #667eea inset;
}
</style>