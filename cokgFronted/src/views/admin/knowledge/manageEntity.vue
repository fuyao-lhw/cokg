<template>
  <div class="admin-container">
    <!-- 表格上方的添加按钮 -->
    <div class="table-header">
      <el-button
        type="primary"
        @click="openAddEntityDialog"
        :icon="Plus"
        class="add-button"
      >
        添加实体
      </el-button>
    </div>

    <el-table
      :data="entityList"
      style="width: 100%"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      class="admin-table"
    >
      <el-table-column
        prop="entityId"
        label="ID"
        width="80"
        sortable
      ></el-table-column>
      <el-table-column prop="name" label="名称" width="150"></el-table-column>
      <el-table-column prop="type" label="类型" width="120"></el-table-column>
      <el-table-column
        prop="kgId"
        label="知识图谱ID"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="node_attr"
        label="节点属性"
        width="200"
      ></el-table-column>
      <el-table-column
        prop="createTime"
        label="创建时间"
        width="150"
      ></el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            type="danger"
            size="small"
            @click="deleteEntity(row.entityId)"
            :icon="Delete"
            class="action-button delete-btn"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="addEntityVisible"
      title="添加实体"
      width="800"
      class="entity-dialog"
    >
      <el-form :model="addEntityForm" ref="entityFormRef">
        <el-form-item label="名称:">
          <el-input v-model="addEntityForm.name"></el-input>
        </el-form-item>
        <el-form-item label="类型:">
          <el-input
            v-model="addEntityForm.type"
            placeholder="自定义或选取常用数据"
          >
            <template #append>
              <el-dropdown @command="handleTypeSelect">
                <el-button :icon="ArrowDown">点击选择</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="Employee"
                      >Employee</el-dropdown-item
                    >
                    <el-dropdown-item command="Department"
                      >Department</el-dropdown-item
                    >
                    <el-dropdown-item command="Document"
                      >Document</el-dropdown-item
                    >
                    <el-dropdown-item command="Project"
                      >Project</el-dropdown-item
                    >
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="知识图谱ID:">
          <el-select v-model="addEntityForm.kgId" placeholder="请选择知识图谱">
            <el-option
              v-for="item in kgOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="节点属性:">
          <el-input v-model="addEntityForm.node_attr">
            <template #append>
              <el-tooltip
                class="tooltip-form-nodeAttr"
                content='节点属性应是JSON结构，假设我选择Employee作为实体类型，则我的节点属性为：{"工号":"10001", "职位":"经理"}，这属于对实体的解释'
                placement="top"
              >
                <el-text>填写提示</el-text>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEntityDialog" class="dialog-btn cancel-btn"
            >取消</el-button
          >
          <el-button
            type="primary"
            @click="handleSaveEntity"
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
import { getEntityList, deleteEntity, addEntity } from "@/utils/entityUtil";
import { getKGList } from "@/utils/kgUtil";
import {
  type KGItem,
  type KGOptions,
  type EntityItem,
} from "@/interface/knowledgeInter";
import { onMounted, ref } from "vue";
import { Plus, Delete, Check, ArrowDown } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox, FormInstance } from "element-plus";

const entityList = ref<EntityItem[]>([]);

const kgOptions = ref<KGOptions[]>([]);

const addEntityVisible = ref(false);

const addEntityForm = ref({
  name: "",
  type: "",
  kgId: "",
  node_attr: "",
});

const entityFormRef = ref<FormInstance>();

function openAddEntityDialog() {
  addEntityVisible.value = true;
}

function closeEntityDialog() {
  addEntityVisible.value = false;
}

// 处理类型选择
function handleTypeSelect(command: string) {
  addEntityForm.value.type = command;
  ElMessage.success(`已选择类型: ${command}`);
}

async function handleSaveEntity() {
  try {
    addEntityVisible.value = false;
    // 添加实体
    await addEntity(addEntityForm.value);

    // 刷新实体列表
    const entityListData = await getEntityList();
    entityList.value = entityListData.data;
  } catch (error) {
    console.error("保存实体失败:", error);
  }
}

async function deleteEntity(entityId: string) {
  try {
    // 确认删除
    await ElMessageBox.confirm("确定要删除这个实体吗？", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 执行删除
    await deleteEntity(entityId);

    // 刷新列表
    const entityListData = await getEntityList();
    entityList.value = entityListData.data;
  } catch (error) {
    console.error("删除实体失败:", error);
  }
}

onMounted(async () => {
  try {
    // 获取实体列表
    const entityListData = await getEntityList();
    entityList.value = entityListData.data;
    console.log("实体列表:", entityListData.data);

    // 获取知识图谱列表作为选项
    const kgListData = await getKGList();
    const kgList = kgListData.data;
    console.log("知识图谱列表:", kgList);

    // 构建知识图谱选项
    kgList.forEach((kg: KGItem) => {
      if (kg && kg.kgId != null && kg.name != null) {
        kgOptions.value.push({
          value: kg.kgId,
          label: kg.name,
        });
      }
    });
    console.log("知识图谱选项:", kgOptions.value);
  } catch (error) {
    console.error("获取实体列表失败:", error);
  }
});
</script>

<style scoped>
/* 实体管理页面特有的样式 */
.entity-dialog {
  /* 如果有实体页面特有的对话框样式，可以在这里添加 */
}
</style>