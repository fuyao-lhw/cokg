<!-- 结构,主体 -->
<template>
  <div class="admin-container">
    <!-- 表格上方的添加按钮 -->
    <div class="table-header">
      <el-button 
        type="primary" 
        @click="openAddUserDialog"
        :icon="Plus"
        class="add-button"
      >
        添加用户
      </el-button>
    </div>

    <el-table
      :data="userList"
      style="width: 100%"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      class="admin-table"
    >
      <el-table-column
        prop="userId"
        label="ID"
        width="80"
        sortable
      ></el-table-column>
      <el-table-column prop="name" label="姓名" width="120"></el-table-column>
      <el-table-column prop="code" label="工号" width="120"></el-table-column>
      <el-table-column
        prop="deptId"
        label="部门ID"
        width="120"
      ></el-table-column>
      <el-table-column prop="role" label="角色" width="100"></el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button 
            type="danger" 
            size="small" 
            @click="deleteUser(row.userId)"
            :icon="Delete"
            class="action-button delete-btn"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog 
      v-model="addUserVisible" 
      title="添加用户" 
      width="800"
      class="user-dialog"
    >
      <el-form :model="addUserForm" ref="userFormRef">
        <el-form-item label="姓名:">
          <el-input v-model="addUserForm.name"></el-input>
        </el-form-item>
        <!-- <el-form-item label="工号:">
          <el-input v-model="addUserForm.code"></el-input>
        </el-form-item> -->
        <el-form-item label="部门ID:">
          <el-select v-model="addUserForm.deptId" placeholder="请选择部门">
            <el-option
              v-for="item in deptOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="角色:">
          <el-radio-group v-model="addUserForm.role">
            <el-radio v-for="item in userRoleGroup" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
          <!-- <el-input v-model="addUserForm.role"></el-input> -->
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDeptDialog" class="dialog-btn cancel-btn">取消</el-button>
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
import { getUserList, deleteUser, addUser } from "@/utils/userUtil";
import { getDeptList } from "@/utils/deptUtil";
import { type DeptItem, type DeptOption } from "@/types/dept";
import { onMounted, ref } from "vue";
import { Plus, Delete, Check } from '@element-plus/icons-vue';
import type { FormInstance } from "element-plus";

const userList = ref([]);
const userRoleGroup = ref([
  { label: "管理员", value: "0" },
  { label: "普通用户", value: "1" },
])

const deptList = ref([]);

const deptOptions = ref<DeptOption[]>([]); // 明确指定类型

const addUserVisible = ref(false);

const addUserForm = ref({
  // code: "",
  name: "",
  deptId: "",
  role: 0,
});

const userFormRef = ref<FormInstance>();

function openAddUserDialog() {
  addUserVisible.value = true;
}

function closeDeptDialog() {
  addUserVisible.value = false;
}

async function handleSaveDept() {
  addUserVisible.value = false;
  // 添加用户
  await addUser(addUserForm.value)

  // 刷新用户列表
  const userListData = await getUserList();
  userList.value = userListData.data;
}

onMounted(async () => {
  try {
    // 获取用户列表
    // 如果getUserList是异步函数
    const userListData = await getUserList();
    userList.value = userListData.data;
    console.log("用户列表:", userListData.data);

    // 处理部门数据
    const deptListData = await getDeptList();
    deptList.value = deptListData.data;
    console.log("部门列表:", deptListData.data);
    // 安全地遍历部门列表并构建选项
    deptList.value.forEach((dept: DeptItem) => {
      if (dept && dept.deptId != null && dept.name != null) {
        deptOptions.value.push({
          value: dept.deptId,
          label: dept.name,
        });
      }
    });
    console.log("部门选项:", deptOptions.value);
  } catch (error) {
    console.error("获取用户列表失败:", error);
  }
});
</script>

<!-- 样式 -->
<style scoped>
/* 用户管理页面特有的样式 */
.user-dialog {
  /* 如果有用户页面特有的对话框样式，可以在这里添加 */
}
</style>