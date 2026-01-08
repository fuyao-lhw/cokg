<template>
  <div class="avatar-container">
    <!-- 登录状态：显示头像 -->
    <div v-if="isLoggedIn" class="avatar" @click="toggleMenu">
      <img :src="avatarUrl" alt="User Avatar" class="avatar-img" />
    </div>
    
    <!-- 未登录状态：显示登录链接 -->
    <router-link v-else to="/login" class="login-link">登录</router-link>
    
    <!-- 展开菜单（仅在登录状态下显示） -->
    <div v-if="showMenu && isLoggedIn" class="menu">
      <div class="menu-content">
        <h3>{{ name }}</h3>
        <div class="menu-item" @click="handleLogout">退出登录</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from "axios";
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

// 用户数据
const router = useRouter();
const name = ref(localStorage.getItem("name") || "");
const avatarUrl = ref("");
const showMenu = ref(false);

// 检查是否已登录
const isLoggedIn = computed(() => {
  return localStorage.getItem("token") !== null || name.value !== "";
});

// 随机获取动漫头像
const fetchRandomAvatar = () => {
  const res = axios.get("https://v2.xxapi.cn/api/head");
  res.then((res) => {
    avatarUrl.value = res.data.data;
  }).catch(error => {
    console.error("获取头像失败:", error);
    // 可以设置一个默认头像
    avatarUrl.value = "/default-avatar.png"; // 或者其他默认头像路径
  });
};

// 切换菜单显示/隐藏
const toggleMenu = () => {
  if (isLoggedIn.value) {
    showMenu.value = !showMenu.value;
  }
};

// 处理退出登录
const handleLogout = () => {
  // 清除本地存储的数据
  localStorage.removeItem("token");
  localStorage.removeItem("name");
//   localStorage.removeItem("userId");
  
  // 重置组件状态
  name.value = "";
  showMenu.value = false;
  
  // 提示用户
  ElMessage.info("已退出登录");
  
  // 可选：跳转到登录页面
  router.push('/login');
};

// 点击菜单外部区域关闭菜单
const handleClickOutside = (event: Event) => {
  const target = event.target as HTMLElement;
  if (!target.closest('.avatar-container')) {
    showMenu.value = false;
  }
};

// 组件挂载时获取随机头像
onMounted(() => {
  if (isLoggedIn.value) {
    fetchRandomAvatar();
  }
  
  // 添加全局点击事件监听器
  document.addEventListener('click', handleClickOutside);
});

// 组件卸载时移除事件监听器
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

// 为了使用 onUnmounted，需要导入
import { onUnmounted } from "vue";
</script>

<style scoped>
.avatar-container {
  position: relative;
  display: inline-block;
  margin-left: 20px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #409eff;
  transition: transform 0.2s;
  background-color: #f5f5f5;
}

.avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login-link {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: all 0.3s;
}

.login-link:hover {
  background-color: #ecf5ff;
  color: #409eff;
  border-color: #b3d8ff;
}

.menu {
  position: absolute;
  top: 50px;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 120px;
}

.menu-content {
  text-align: center;
  padding: 10px 0;
}

.menu h3 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  padding: 0 12px;
}

.menu-item {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: background-color 0.2s;
}

.menu-item:hover {
  background-color: #f5f7fa;
  color: #409eff;
}
</style>