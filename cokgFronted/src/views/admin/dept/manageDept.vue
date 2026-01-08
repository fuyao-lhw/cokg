<!-- 结构,主体 -->
<template>
  <div class="admin-container">
    <!-- 表格上方的添加按钮 -->
    <div class="table-header">
      <el-button 
        type="primary" 
        @click="openAddDeptDialog"
        :icon="Plus"
        class="add-button"
      >
        添加部门
      </el-button>
    </div>

    <el-table
      :data="deptList"
      style="width: 100%"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      class="admin-table"
    >
      <el-table-column
        prop="deptId"
        label="ID"
        width="80"
        sortable
      ></el-table-column>
      <el-table-column prop="name" label="部门名称" width="150"></el-table-column>
      <!-- <el-table-column prop="code" label="部门编码" width="150"></el-table-column> -->
      <!-- <el-table-column prop="description" label="描述" width="200"></el-table-column> -->
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            size="small" 
            @click="openEditDeptDialog(row)"
            :icon="Edit"
            class="action-button edit-btn"
          >
            编辑
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="deleteDept(row.deptId)"
            :icon="Delete"
            class="action-button delete-btn"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑部门对话框 -->
    <el-dialog 
      v-model="addDeptVisible" 
      :title="isEdit ? '编辑部门' : '添加部门'" 
      width="600"
      class="dept-dialog"
    >
      <el-form :model="deptForm" :rules="deptRules" ref="deptFormRef">
        <el-form-item label="部门名称:" prop="name">
          <el-input v-model="deptForm.name" placeholder="请输入部门名称"></el-input>
        </el-form-item>
        <!-- <el-form-item label="部门编码:" prop="code">
          <el-input v-model="deptForm.code" placeholder="请输入部门编码"></el-input>
        </el-form-item> -->
        <!-- <el-form-item label="描述:">
          <el-input 
            v-model="deptForm.description" 
            type="textarea" 
            placeholder="请输入部门描述"
            :rows="3"
          ></el-input>
        </el-form-item> -->
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button 
            @click="closeDeptDialog" 
            :icon="Close"
            class="dialog-btn cancel-btn"
          >
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="handleSaveDept" 
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

<!-- 交互,脚本语言 -->
<script setup lang="ts">
import { addDept, getDeptList } from "@/utils/deptUtil";
import { onMounted, ref } from "vue";
import type { DeptItem } from "@/types/dept";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage, ElMessageBox } from "element-plus";
import { Check, Close, Delete, Edit, Plus } from "@element-plus/icons-vue";

// 部门列表数据
const deptList = ref<DeptItem[]>([]);

// 对话框相关
const addDeptVisible = ref(false);
const isEdit = ref(false);
const currentDeptId = ref<string | number | null>(null);

// 部门表单数据
const deptForm = ref({
  name: "",
//   code: "",
//   description: "",
});

// 表单验证规则
const deptRules: FormRules = {
  name: [
    { required: true, message: "请输入部门名称", trigger: "blur" },
    { min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur" }
  ],
  code: [
    { required: true, message: "请输入部门编码", trigger: "blur" },
    { pattern: /^[A-Za-z0-9_-]+$/, message: "编码只能包含字母、数字、下划线和横线", trigger: "blur" }
  ]
};

// 表单引用
const deptFormRef = ref<FormInstance>();

// 打开添加部门对话框
function openAddDeptDialog() {
  isEdit.value = false;
  currentDeptId.value = null;
  deptForm.value = {
    name: "",
    // code: "",
    // description: "",
  };
  addDeptVisible.value = true;
}

// 打开编辑部门对话框
function openEditDeptDialog(dept: DeptItem) {
  isEdit.value = true;
  currentDeptId.value = dept.deptId;
  deptForm.value = {
    name: dept.name || "",
    // code: dept.code || "",
    // description: dept.description || "",
  };
  addDeptVisible.value = true;
}

// 保存部门（新增或编辑）
const handleSaveDept = async () => {
  if (!deptFormRef.value) return;
  
  try {
    await deptFormRef.value.validate();
    
    if (isEdit.value && currentDeptId.value) {
    //   编辑部门
    //   await updateDept(currentDeptId.value, deptForm.value);
      ElMessage.success("部门更新成功");
    } else {
      // 添加部门
      await addDept(deptForm.value);
      ElMessage.success("部门添加成功");
    }
    
    // 关闭对话框并刷新列表
    closeDeptDialog();
    await loadDeptList();
  } catch (error) {
    console.error("保存部门失败:", error);
    ElMessage.error("保存失败，请检查输入信息");
  }
};

// 关闭对话框
function closeDeptDialog() {
  addDeptVisible.value = false;
  if (deptFormRef.value) {
    deptFormRef.value.clearValidate();
  }
}

// 删除部门
const deleteDept = async (id: string | number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个部门吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await deleteDeptApi(id);
    ElMessage.success("部门删除成功");
    await loadDeptList();
  } catch (error) {
    console.error("删除部门失败:", error);
    if (error !== 'cancel') {
      ElMessage.error("删除失败");
    }
  }
};

// 加载部门列表
const loadDeptList = async () => {
  try {
    const deptListData = await getDeptList();
    deptList.value = deptListData.data;
    console.log("部门列表:", deptListData.data);
  } catch (error) {
    console.error("获取部门列表失败:", error);
    ElMessage.error("获取部门列表失败");
  }
};

// 初始化数据
onMounted(async () => {
  await loadDeptList();
});
</script>

<!-- 样式 -->
<style scoped>
/* 部门管理页面特有的样式 */
.dept-dialog {
  /* 如果有部门页面特有的对话框样式，可以在这里添加 */
}
</style>