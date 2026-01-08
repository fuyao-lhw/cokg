<!-- 结构,主体 -->
<template>
  <el-form class="login">
    <span>工号</span>
    <el-input v-model="code"></el-input>
    <span>密码</span>
    <el-input v-model="password"></el-input>
    <el-button @click="submitLogin(code, password)">登录</el-button>
  </el-form>
</template>

<!-- 交互,脚本语言 -->
<script setup name="Login" lang="ts">
import { ref } from "vue";
import {submitLogin} from "@/utils/identityUtil";

const code = ref("");
const password = ref("");

// async function submitLogin() {
//   const res = await axios.post("/api/user/login", {
//     username: username.value,
//     password: password.value,
//   });

//   console.log(res.data);

// }
</script>

<!-- 样式 -->
<style scoped>
</style>