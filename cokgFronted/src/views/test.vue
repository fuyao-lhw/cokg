<!-- 结构，主体 -->
<template>
  <el-button type="primary" @click="openDialog">click</el-button>

  <Dialog v-model:visible="dialogVisible"></Dialog>
</template>

<!-- 交互，脚本语言 -->
<script setup lang="ts">
// import { openDialog } from "@/utils/dialogUtil";
import Dialog from "@/component/dialog/DilaogComponent.vue";
import { ref } from "vue";

const dialogVisible = ref(false);

function openDialog() {
  dialogVisible.value = true;
}



</script>

<!-- 样式 -->
<style scoped>
</style>