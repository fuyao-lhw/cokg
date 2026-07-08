<!-- 结构,主体 -->
<template>
  <div class="distribute-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>图谱权限分配</span>
          <span v-if="selectedRows.length > 0" style="margin-right: 10px; color: #409EFF;">
            已选中 {{ selectedRows.length }} 项
          </span>
          <el-button type="primary" @click="handleSaveAll" :disabled="selectedRows.length === 0">
            保存选中更改
          </el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        style="width: 100%"
        border
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        
        <el-table-column prop="userCode" label="员工工号" width="120" align="center" />
        <el-table-column prop="userName" label="姓名" width="120" align="center" />

        <!-- 可见图谱ID列 (修改为输入框+编辑按钮) -->
        <el-table-column label="可见图谱ID" min-width="300">
          <template #default="{ row }">
            <div style="display: flex; gap: 10px; align-items: center;">
              <el-input 
                v-model="row.kgDisplayText" 
                placeholder="未分配" 
                readonly 
                style="flex: 1;"
              />
              <el-button type="primary" link @click="openEditDialog(row)">
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑图谱权限对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="分配知识图谱权限"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="当前用户">
          <span>{{ currentEditRow?.userName }} ({{ currentEditRow?.userCode }})</span>
        </el-form-item>
        <el-form-item label="选择图谱">
          <el-select
            v-model="tempSelectedKgIds"
            multiple
            collapse-tags
            collapse-tags-tooltip
            placeholder="请选择图谱"
            style="width: 100%"
          >
            <el-option
              v-for="kg in kgOptions"
              :key="kg.kgMetaId"
              :label="kg.kgName"
              :value="kg.kgMetaId"
            >
              <span style="float: left">{{ kg.kgName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ kg.kgMetaId }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmEdit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<!-- 交互,脚本语言 -->
<script lang="ts" setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getUserPermissions } from "@/utils/userUtil";
import { getKGList } from "@/utils/kgUtil";
import { postByAuth } from "@/utils/requestUtil";
import { get } from "http";

// --- 类型定义 ---
interface KgOption {
  kgMetaId: string;
  kgName: string;
}

interface UserRow {
  userId: string;
  userCode: string;
  userName: string;
  selectedKgIds: string[]; 
  originalKgIds: string[]; 
  kgDisplayText: string; // 用于输入框显示的文本
}

// --- 状态数据 ---
const loading = ref(false);
const tableData = ref<UserRow[]>([]);
const kgOptions = ref<KgOption[]>([]);
const selectedRows = ref<UserRow[]>([]);

// 对话框相关状态
const dialogVisible = ref(false);
const currentEditRow = ref<UserRow | null>(null);
const tempSelectedKgIds = ref<string[]>([]); // 临时存储对话框中选中的ID

// --- 数据获取 ---
const fetchUsers = async () => {
  loading.value = true;
  try {
    const userRes = await getUserPermissions();
    console.log("用户列表:", userRes.data);
    tableData.value = []; 

    userRes.data.forEach((user) => {
      // 假设后端返回了该用户已有的图谱ID列表，这里暂定为空，实际需根据API调整
      const initialKgIds: string[] = user.originalKgIds; 
      
      tableData.value.push({
        userId: user.userId,
        userCode: user.userCode,
        userName: user.userName,
        selectedKgIds: [...initialKgIds],
        originalKgIds: [...initialKgIds],
        kgDisplayText: formatKgIds(initialKgIds), // 初始化显示文本
      });
    });
  } catch (error) {
    console.error(error);
    ElMessage.error("获取用户列表失败");
  } finally {
    loading.value = false;
  }
};

const fetchKgList = async () => {
  try {
    const kgRes = await getKGList();
    kgOptions.value = kgRes.data.map((kg: any) => ({
      kgMetaId: kg.kgMetaId,
      kgName: kg.kgName,
    }));
  } catch (error) {
    console.error(error);
    ElMessage.error("获取图谱列表失败");
  }
};

// --- 辅助函数 ---
// 将ID数组格式化为分号分隔的字符串用于显示
const formatKgIds = (ids: string[]) => {
  return ids.join(';');
};

// --- 事件处理 ---

const handleSelectionChange = (val: UserRow[]) => {
  selectedRows.value = val;
};

// 打开编辑对话框
const openEditDialog = (row: UserRow) => {
  currentEditRow.value = row;
  // 复制当前选中的ID到临时变量，避免直接修改原数据直到用户点击确定
  tempSelectedKgIds.value = [...row.selectedKgIds];
  dialogVisible.value = true;
};

// 确认编辑
const confirmEdit = () => {
  if (currentEditRow.value) {
    // 更新行数据
    currentEditRow.value.selectedKgIds = [...tempSelectedKgIds.value];
    // 更新显示文本
    currentEditRow.value.kgDisplayText = formatKgIds(tempSelectedKgIds.value);
    
    dialogVisible.value = false;
    ElMessage.success("修改已暂存，请点击右上角保存生效");
  }
};

// 保存所有更改
const handleSaveAll = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning("请至少选择一行数据进行保存");
    return;
  }

  const changes = selectedRows.value
    .filter(
      (row) =>
        JSON.stringify(row.selectedKgIds) !== JSON.stringify(row.originalKgIds)
    )
    .map((row) => ({
      userId: row.userId,
      kgMetaIds: row.selectedKgIds, // 提交数组
    }));

  if (changes.length === 0) {
    ElMessage.info("选中的数据未发生任何更改");
    return;
  }

  loading.value = true;
  try {
    await postByAuth("/api/user/permission/distribution", localStorage.getItem("token"), changes);

    // 更新原始数据状态
    changes.forEach(change => {
      const row = tableData.value.find(r => r.userId === change.userId);
      if (row) {
        row.originalKgIds = [...row.selectedKgIds];
      }
    });
    
    ElMessage.success(`成功保存 ${changes.length} 条数据`);
  } catch (error) {
    console.error(error);
    ElMessage.error("保存失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchUsers();
  fetchKgList();
});
</script>

<!-- 样式 -->
<style scoped>
.distribute-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>