<!-- 结构,主体 -->
<template>
<GraphComponent :graphId="graphId"></GraphComponent>
</template>

<!-- 交互,脚本语言 -->
<script setup lang="ts" name="GraphDetail">
import { useRoute } from 'vue-router';
import GraphComponent from '@/component/graph/GraphComponent.vue';

const route = useRoute();
const graphId = route.params.graphId;
console.log("graphId", graphId);
</script>

<!-- 样式 -->
<style scoped>

</style>