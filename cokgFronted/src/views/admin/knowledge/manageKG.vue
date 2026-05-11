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
      <el-button
      type="primary"
      @click="openBatchDialog"
      :icon="Upload">
        批量导入
      </el-button>
    </div>

    <BatchAddFileDialog 
    v-model:visible="batchDialogVisible" 
    title="批量导入知识图谱"
    @submit="(files) => handleSubmitFile(files, 'kg')"
    />

    <el-table
      :data="kgList"
      style="width: 100%"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      class="admin-table"
    >
      <el-table-column
        prop="kgMetaId"
        label="ID"
        width="80"
        sortable
      >
    </el-table-column>
      <el-table-column prop="kgName" label="名称" width="150"></el-table-column>
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
      <el-table-column label="创建人" prop="createUserId"></el-table-column>
      <el-table-column label="图谱类型" prop="kgType">
        <template #default="{ row }">
          {{ row.kgType === 0 ? "个人" : "部门" }}
        </template>
      </el-table-column>
      <el-table-column label="所属部门" prop="deptId"></el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            type="danger"
            size="small"
            @click="handleDeleteKG(row.kgMetaId)"
            :icon="Delete"
            class="action-button delete-btn"
          >
            删除
          </el-button>
          <el-button
            type="primary"
            @click="openEditKGDialog(row)"
            :icon="Edit"
            class="action-button edit-btn"
          >
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="addKGVisible"
      :title="addOrUpdate === 'add' ? '添加知识图谱' : '编辑知识图谱'"
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
        <el-form-item v-if="addOrUpdate === 'update'" label="所属部门">
          <el-select placeholder="请选择部门" v-model="addKGForm.deptId">
            <el-option
              v-for="item in deptOptions"
              :key="item.value"
              :value="item.value"
              :label="item.label"
            ></el-option>
          </el-select>
        </el-form-item>
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
import { getKGList, deleteKG, addKG, updateKG} from "@/utils/kgUtil";
import { getDeptList } from "@/utils/deptUtil";
import { onMounted, ref } from "vue";
import { Plus, Delete, Check, Edit, Upload } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import {
  type KGItem,
  type submitKGForm,
  type updateKGForm,
} from "@/interface/knowledgeInter";
import { type options } from "@/interface/optionsInter";
import type { DeptItem } from "@/interface/deptInter";
import BatchAddFileDialog from "@/component/dialog/BatchAddFileDialog.vue";
import { handleSubmitFile } from "@/utils/fileUtil";

const kgList = ref<KGItem[]>([]);

const addKGVisible = ref(false);
const addOrUpdate = ref("add");
const batchDialogVisible = ref(false);

const updateKGForm = ref<updateKGForm>({
  kgMetaId: null,
  kgName: "",
  kgType: null,
  deptId: null,

});

const addKGForm = ref<submitKGForm>({
  kgName: "",
  kgType: 1,
  deptId: -1,
  createUserCode: localStorage.getItem("code"),
});

const deptOptions = ref<options[]>([]);


function openBatchDialog() {
  batchDialogVisible.value = true;
}
function openAddKGDialog() {
  addOrUpdate.value = "add";
  addKGVisible.value = true;
}

async function openEditKGDialog(row: KGItem) {
  addOrUpdate.value = "update";
  addKGVisible.value = true;

  console.log("当前值:", row);

  await loadDeptOptions();

  // 设置当前值
  addKGForm.value = {
    kgName: row.kgName,
    kgType: row.kgType,
    deptId: row.deptId,
    createUserCode: localStorage.getItem("code"),
  };
  updateKGForm.value.kgMetaId = row.kgMetaId;
}

async function loadDeptOptions() {
  try {
    const deptListData = await getDeptList();
    deptOptions.value = deptListData.data.map((dept: DeptItem) => ({
      value: dept.deptId,
      label: `${dept.deptId}(${dept.name})`,
    }));
  } catch (error) {
    console.error("加载部门选项失败:", error);
  }
}

function closeKGDialog() {
  addKGVisible.value = false;
  addKGForm.value = {
    kgName: "",
    kgType: 1,
    deptId: 1,
    createUserCode: localStorage.getItem("code"),
  };
}

async function handleSaveKG() {
  try {
    addKGVisible.value = false;

    updateKGForm.value.kgName = addKGForm.value.kgName;
    updateKGForm.value.kgType = addKGForm.value.kgType;
    updateKGForm.value.deptId = addKGForm.value.deptId;

    // 添加知识图谱
    if (addOrUpdate.value === "add") {
      await addKG(addKGForm.value);
    } else if (addOrUpdate.value === "update") {
      // 更新知识图谱
      await updateKG(updateKGForm.value);
    }

    // 刷新知识图谱列表
    const kgListData = await getKGList();
    kgList.value = kgListData.data;
  } catch (error) {
    console.error("保存知识图谱失败:", error);
  }
}

async function handleDeleteKG(kgMetaId: number) {
  try {
    console.log("删除知识图谱:", kgMetaId, typeof kgMetaId);
    const res = await deleteKG(kgMetaId);

    if (res.code !== 200) {
      ElMessageBox.alert(res.message, "错误", {
        confirmButtonText: "确定",
        type: "error",
      });
    }


    // 刷新列表
    const kgListData = await getKGList();
    kgList.value = kgListData;
  } catch (error) {
    console.error("删除知识图谱失败:", error);
  }
}

// async function handleSubmitFile(file: File) {
//   const formData = new FormData();

//   formData.append("file", file);
//   formData.append("funcType", "kg");
//   formData.append("userCode", localStorage.getItem("code") || "10001");

//   await ElMessageBox.alert("上传成功", "提示", {
//     confirmButtonText: "确定",
//     type: "success",
//   });
//   await batchAdd(formData);

//   window.location.reload();
  
// }

onMounted(async () => {
  try {
    // 获取知识图谱列表
    const kgListData = await getKGList();
    kgList.value = kgListData.data;
    console.log("知识图谱列表:", kgListData);
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