<template>
  <div class="admin-container">
    <!-- 表格上方的添加按钮 -->
    <div class="table-header">
      <el-button
        type="primary"
        @click="openAddKGDialog"
        :icon="Plus"
        class="add-button"
      >
        添加知识图谱
      </el-button>
    </div>

    <el-table
      :data="kgList"
      style="width: 100%"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      class="admin-table"
    >
      <el-table-column
        prop="kgId"
        label="ID"
        width="80"
        sortable
      ></el-table-column>
      <el-table-column prop="name" label="名称" width="150"></el-table-column>
      <!-- <el-table-column prop="description" label="描述" width="200"></el-table-column> -->
      <el-table-column
        prop="createTime"
        label="创建时间"
        width="150"
      ></el-table-column>
      <el-table-column
        prop="updateTime"
        label="更新时间"
        width="150"
      ></el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            type="danger"
            size="small"
            @click="deleteKG(row.kgId)"
            :icon="Delete"
            class="action-button delete-btn"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="addKGVisible"
      title="添加知识图谱"
      width="800"
      class="kg-dialog"
    >
      <el-form :model="addKGForm" ref="kgFormRef">
        <el-form-item label="图谱名称:">
          <el-input v-model="addKGForm.kgName"></el-input>
        </el-form-item>
        <el-form-item label="图谱类型：">
          <el-input
            v-model="addKGForm.kgType"
            type="number"
            placeholder="请输入0或1"
          >
            <template #append>
              <el-tooltip placement="top">
                <template #content>图谱所属：0代表个人；1代表部门</template>
                <el-text>填写提示</el-text>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        <!-- <el-form-item label="描述:">
          <el-input 
            v-model="addKGForm.description" 
            type="textarea"
            :rows="3"
          ></el-input>
        </el-form-item> -->
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeKGDialog" class="dialog-btn cancel-btn"
            >取消</el-button
          >
          <el-button
            type="primary"
            @click="handleSaveKG"
            :icon="Check"
            class="dialog-btn confirm-btn"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { getKGList, deleteKG, addKG } from "@/utils/kgUtil";
import { onMounted, ref } from "vue";
import { Plus, Delete, Check } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import { type KGItem, submitKGForm } from "@/interface/knowledgeInter";

const kgList = ref<KGItem[]>([]);

const addKGVisible = ref(false);

const addKGForm = ref<submitKGForm>({
  kgName: "",
  kgType: 1,
  deptId: "",
  createUserCode: localStorage.getItem("code"),
});

function openAddKGDialog() {
  addKGVisible.value = true;
}

function closeKGDialog() {
  addKGVisible.value = false;
  addKGForm.value = {
    kgName: "",
    kgType: 1,
    deptId: "",
    createUserCode: localStorage.getItem("code"),
  };
}

async function handleSaveKG() {
  try {
    addKGVisible.value = false;
    // 添加知识图谱
    await addKG(addKGForm.value);

    // 刷新知识图谱列表
    const kgListData = await getKGList();
    kgList.value = kgListData.data;
  } catch (error) {
    console.error("保存知识图谱失败:", error);
  }
}

async function deleteKG(kgId: string) {
  try {
    // 确认删除
    await ElMessageBox.confirm("确定要删除这个知识图谱吗？", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 执行删除
    await deleteKG(kgId);

    // 刷新列表
    const kgListData = await getKGList();
    kgList.value = kgListData.data;
  } catch (error) {
    console.error("删除知识图谱失败:", error);
  }
}

onMounted(async () => {
  try {
    // 获取知识图谱列表
    const kgListData = await getKGList();
    kgList.value = kgListData.data;
    console.log("知识图谱列表:", kgListData.data);
  } catch (error) {
    console.error("获取知识图谱列表失败:", error);
  }
});
</script>

<style scoped>
/* 知识图谱管理页面特有的样式 */
.kg-dialog {
  /* 如果有知识图谱页面特有的对话框样式，可以在这里添加 */
}
</style>