<template>
  <div class="entity-container">
    <!-- 顶部操作栏 -->
    <div class="action-bar">
      <!-- 上传文档按钮 -->
      <el-upload
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        :show-file-list="false"
        accept=".doc,.docx,.pdf,.txt"
      >
        <el-button type="primary" :icon="Upload">
          上传文档解析实体
        </el-button>
      </el-upload>

      <!-- 右侧功能按钮组 -->
      <div class="right-actions">
        <el-button
          type="success"
          :icon="Download"
          @click="handleExportExcel"
          :disabled="entityList.length === 0"
        >
          导出为 Excel
        </el-button>
        
        <el-button
          type="warning"
          :icon="Check"
          @click="handleSubmitToManage"
          :disabled="entityList.length === 0"
        >
          提交实体管理
        </el-button>
      </div>
    </div>

    <!-- 数据展示表格 -->
    <el-table
      :data="entityList"
      style="width: 100%; margin-top: 20px;"
      border
      stripe
      v-loading="loading"
    >
      <el-table-column prop="nodeName" label="节点名称" width="180"></el-table-column>
      <el-table-column prop="nodeType" label="节点类型" width="150"></el-table-column>
      <el-table-column prop="kgMetaId" label="知识图谱ID" width="120"></el-table-column>
      <el-table-column prop="nodeAttr" label="节点属性" min-width="200">
        <template #default="{ row }">
          <span class="attr-text">{{ formatAttr(row.nodeAttr) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ $index }">
          <el-button
            type="danger"
            link
            size="small"
            @click="handleDeleteRow($index)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空状态提示 -->
    <el-empty
      v-if="entityList.length === 0 && !loading"
      description="暂无解析数据，请上传文档"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Upload, Download, Check } from '@element-plus/icons-vue';
import * as XLSX from 'xlsx';
// 假设这里有一个工具函数或API接口用于发送文件到后端进行解析
// import { parseDocumentToEntities } from '@/utils/entityUtil'; 
// 假设这里有一个接口用于批量提交实体
// import { batchAddNodes } from '@/utils/kgUtil';

// --- 类型定义 (参考 manageNode.vue 中的 NodeItem) ---
interface EntityItem {
  nodeName: string;
  nodeType: string;
  kgMetaId: number | string; // 后端可能返回字符串或数字
  nodeAttr: string; // JSON字符串
}

// --- 状态管理 ---
const loading = ref(false);
const entityList = ref<EntityItem[]>([]);

// --- 方法实现 ---

/**
 * 1. 处理文件上传与解析
 */
const handleFileChange = async (file: any) => {
  if (!file.raw) return;
  
  loading.value = true;
  entityList.value = []; // 清空旧数据

  try {
    // 【模拟后端交互】
    // 实际开发中，你应该创建一个 FormData 并将 file.raw append 进去，然后调用后端接口
    // const formData = new FormData();
    // formData.append('file', file.raw);
    // const res = await parseDocumentToEntities(formData);
    // entityList.value = res.data;

    // --- 模拟数据开始 (仅用于演示效果，请替换为真实API调用) ---
    await new Promise(resolve => setTimeout(resolve, 1000)); // 模拟网络延迟
    const mockData: EntityItem[] = [
      {
        nodeName: "张三",
        nodeType: "Employee",
        kgMetaId: 1,
        nodeAttr: '{"工号": "1001", "部门": "技术部"}'
      },
      {
        nodeName: "李四",
        nodeType: "Employee",
        kgMetaId: 1,
        nodeAttr: '{"工号": "1002", "部门": "市场部"}'
      },
      {
        nodeName: "AI项目组",
        nodeType: "Project",
        kgMetaId: 1,
        nodeAttr: '{"状态": "进行中"}'
      }
    ];
    entityList.value = mockData;
    // --- 模拟数据结束 ---

    ElMessage.success(`成功解析出 ${entityList.value.length} 条实体数据`);

  } catch (error) {
    console.error(error);
    ElMessage.error("文档解析失败，请检查文件格式或网络连接");
  } finally {
    loading.value = false;
  }
};

/**
 * 2. 导出为 Excel
 */
const handleExportExcel = () => {
  if (entityList.value.length === 0) {
    ElMessage.warning("没有可导出的数据");
    return;
  }

  // 准备导出数据，格式化一下 nodeAttr 以便在 Excel 中更易读
  const exportData = entityList.value.map(item => ({
    "节点名称": item.nodeName,
    "节点类型": item.nodeType,
    "知识图谱ID": item.kgMetaId,
    "节点属性": item.nodeAttr // 保持JSON字符串，或者可以 JSON.parse 后展开
  }));

  // 创建 worksheet
  const ws = XLSX.utils.json_to_sheet(exportData);
  
  // 创建 workbook
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "实体数据");

  // 生成文件名并下载
  const fileName = `实体数据_${new Date().getTime()}.xlsx`;
  XLSX.writeFile(wb, fileName);
  
  ElMessage.success("导出成功");
};

/**
 * 3. 提交实体管理
 */
const handleSubmitToManage = async () => {
  if (entityList.value.length === 0) return;

  try {
    // 弹窗确认
    await ElMessageBox.confirm(
      `确定要将当前 ${entityList.value.length} 条实体数据提交到知识库管理吗？`,
      '确认提交',
      {
        confirmButtonText: '确认提交',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    loading.value = true;

    // 【模拟后端交互】
    // 实际开发中调用批量添加接口
    // const res = await batchAddNodes(entityList.value);
    
    // --- 模拟提交开始 ---
    await new Promise(resolve => setTimeout(resolve, 1000));
    console.log("提交给后端的数据:", entityList.value);
    // --- 模拟提交结束 ---

    ElMessage.success("提交成功！数据已发送至实体管理模块。");
    
    // 可选：提交成功后清空列表或跳转页面
    // entityList.value = []; 

  } catch (error) {
    if (error !== 'cancel') {
      console.error("提交失败:", error);
      ElMessage.error("提交失败，请稍后重试");
    }
  } finally {
    loading.value = false;
  }
};

/**
 * 辅助：删除单行数据
 */
const handleDeleteRow = (index: number) => {
  entityList.value.splice(index, 1);
  ElMessage.info("已删除该行数据");
};

/**
 * 辅助：格式化属性显示
 */
const formatAttr = (attr: string) => {
  if (!attr) return '';
  // 简单截断过长的JSON字符串，避免表格撑开太大
  return attr.length > 50 ? attr.substring(0, 50) + '...' : attr;
};
</script>

<style scoped>
.entity-container {
  padding: 20px;
  background-color: #fff;
  min-height: 100%;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.right-actions {
  display: flex;
  gap: 10px;
}

.attr-text {
  font-family: monospace;
  color: #606266;
  font-size: 12px;
}
</style>