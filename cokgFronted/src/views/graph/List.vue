<!-- 结构,主体 -->
<template>
  <div class="graph-list-container">
    <el-table :data="tableData" style="width: 100%" border>
      <el-table-column prop="graphId" label="图谱 ID" width="180" />
      <el-table-column prop="graphName" label="图谱名" width="200" />
      <el-table-column prop="permission" label="权限" width="120">
        <template #default="{ row }">
          <el-tag :type="getPermissionType(row.permission)">
            {{ getPermissionText(row.permission) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="200" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleView(row)">查看</el-button>
          <el-button size="small" type="primary" @click="handleEdit(row)"
            >编辑</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<!-- 交互,脚本语言 -->
<script setup lang="ts" name="GraphList">
import { useRouter } from "vue-router";
import { ref, onMounted } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";
import { type GraphItem } from "@/interface/graphInter";
import { getGraphList } from "@/utils/graphUtil";

const tableData = ref<GraphItem[]>([]);
const router = useRouter();

// 获取权限类型（用于 el-tag 颜色）
function getPermissionType(permission: number): string {
  const typeMap: Record<number, string> = {
    0: "info",
    1: "success",
  };
  return typeMap[permission] || "info";
}

// 获取权限文本
function getPermissionText(permission: number): string {
  const textMap: Record<number, string> = {
    0: "只读",
    1: "编辑",
  };
  return textMap[permission] || "未知";
}

// 查看图谱
function handleView(row: GraphItem) {
  console.log("查看图谱:", row.graphId);
  // TODO: 跳转查看页面
  router.push(`/graph/${row.graphId}`);
}

// 编辑图谱
function handleEdit(row: GraphItem) {
  console.log("编辑图谱:", row.graphId);
  // TODO: 跳转编辑页面
}

onMounted(async () => {
  tableData.value = await getGraphList();
});
</script>

<!-- 样式 -->
<style scoped>
.graph-list-container {
  padding: 20px;
}
</style>