<template>
  <div class="admin-container">
    <!-- 表格上方的添加按钮 -->
    <div class="table-header">
      <el-button
        type="primary"
        @click="openAddNodeDialog"
        :icon="Plus"
        class="add-button"
      >
        添加实体
      </el-button>
      <el-button type="primary" @click="openBatchDialog" :icon="Upload">
        批量导入
      </el-button>
      <el-select
        v-model="selectedKgId"
        placeholder="下拉筛选对应数据"
        filterable
        clearable
        style="width: 200px; margin-left: 10px;"
        @change="handleKgFilterChange"
      >
        <el-option
          v-for="item in kgOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>

    <BatchAddFileDialog
    v-model:visible="batchDialogVisible"
    title="批量导入节点"
    @submit="(files) => handleSubmitFile(files, 'kgNode')"
    />

    <el-table
      :data="nodeList"
      style="width: 100%"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      class="admin-table"
    >
      <el-table-column
        prop="kgNodeId"
        label="ID"
        width="80"
        sortable
      ></el-table-column>
      <el-table-column
        prop="nodeName"
        label="节点（实体）名称"
        width="150"
      ></el-table-column>
      <el-table-column
        prop="nodeType"
        label="节点（实体）类型"
        width="150"
      ></el-table-column>
      <el-table-column
        prop="kgMetaId"
        label="知识图谱ID"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="nodeAttr"
        label="节点属性"
        width="200"
      ></el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            type="danger"
            size="small"
            @click="handleDeleteNode(row.kgNodeId)"
            :icon="Delete"
            class="action-button delete-btn"
          >
            删除
          </el-button>
          <el-button
            type="primary"
            size="small"
            @click="openUpdateNodeDialog(row)"
            :icon="Edit"
            class="action-button update-btn"
          >
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="addNodeVisible"
      :title="addOrUpdate === 'add' ? '添加节点' : '编辑节点'"
      width="800"
      class="Node-dialog"
    >
      <el-form :model="addNodeForm" ref="NodeFormRef">
        <el-form-item label="名称:">
          <el-input v-model="addNodeForm.nodeName"></el-input>
        </el-form-item>
        <el-form-item label="类型:">
          <el-input
            v-model="addNodeForm.nodeType"
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
          <el-select
            v-model="addNodeForm.kgMetaId"
            placeholder="请选择知识图谱"
          >
            <el-option
              v-for="item in kgOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
              clearable
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="节点属性:">
          <el-input v-model="addNodeForm.nodeAttr">
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
          <el-button @click="closeNodeDialog" class="dialog-btn cancel-btn"
            >取消</el-button
          >
          <el-button
            type="primary"
            @click="handleSaveNode"
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
import { getNodeList, deleteNode, addNode, updateNode } from "@/utils/kgUtil";
import { getKGList } from "@/utils/kgUtil";
import {
  type NodeItem,
  type addNodeForm,
  type KGItem,
  type KGOptions,
  type updateNodeForm,
} from "@/interface/knowledgeInter";
import { onMounted, ref } from "vue";
import { Plus, Delete, Check, ArrowDown, Edit, Upload } from "@element-plus/icons-vue";
import { ElMessage, FormInstance, ElMessageBox } from "element-plus";
import { handleSubmitFile } from "@/utils/fileUtil";
import BatchAddFileDialog from "@/component/dialog/BatchAddFileDialog.vue";

const nodeList = ref<NodeItem[]>([]);

const kgOptions = ref<KGOptions[]>([]);

const addNodeVisible = ref(false);

const addNodeForm = ref(<addNodeForm>{
  nodeName: "",
  nodeType: "",
  kgMetaId: -1,
  nodeAttr: "",
  createUserCode: localStorage.getItem("code"),
});

const NodeFormRef = ref<FormInstance>();

const addOrUpdate = ref("add");

const updateNodeForm = ref(<updateNodeForm>{
  kgNodeId: -1,
  nodeName: "",
  nodeType: "",
  kgMetaId: -1,
  nodeAttr: "",
});

const batchDialogVisible = ref(false);
function openBatchDialog() {
  console.log("openBatchDialog");
  batchDialogVisible.value = true;
}

// ... 其他 ref 定义
const selectedKgId = ref<number>(-1); // 用于存储选中的知识图谱ID，1表示全部

// 处理知识图谱筛选变化
async function handleKgFilterChange() {
  await loadNodeList();
}

async function loadNodeList() {
      // 获取实体列表
    const nodeListData = await getNodeList(selectedKgId.value);
    nodeList.value = nodeListData.data;
    console.log("实体列表:", nodeList.value);
    nodeList.value.forEach((node: NodeItem) => {
      node.nodeAttr = node.nodeAttr.replace("\\", "");
    });
}

function openAddNodeDialog() {
  addOrUpdate.value = "add";
  addNodeVisible.value = true;
}

function closeNodeDialog() {
  addNodeVisible.value = false;
}

// 处理类型选择
function handleTypeSelect(command: string) {
  addNodeForm.value.nodeType = command;
  ElMessage.success(`已选择类型: ${command}`);
}

async function handleSaveNode() {
  try {
    addNodeVisible.value = false;
    let res = null;

    if (addOrUpdate.value === "add") {
      // 新增
      res = await addNode(addNodeForm.value);
    } else {
      // 修改
      // 填充修改表单数据
      updateNodeForm.value.kgMetaId = addNodeForm.value.kgMetaId;
      updateNodeForm.value.nodeAttr = addNodeForm.value.nodeAttr;
      updateNodeForm.value.nodeName = addNodeForm.value.nodeName;
      updateNodeForm.value.nodeType = addNodeForm.value.nodeType;
      res = await updateNode(updateNodeForm.value);
    }

    if (res.code !== 200) {
      ElMessage.success("保存成功");
    }

    // 刷新实体列表
    const nodeListData = await getNodeList(-1);
    nodeList.value = nodeListData.data;
  } catch (error) {
    console.error("保存节点失败:", error);
  }
}

async function handleDeleteNode(kgNodeId: number) {
  try {
    // 确认删除
    await ElMessageBox.confirm("确定要删除这个实体吗？", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 执行删除
    const res = await deleteNode(kgNodeId);

    if (res.code === 200) {
      ElMessage.success("删除成功");
    } else {
      ElMessage.error("删除失败");
    }
  } catch (error) {
    console.error("删除实体失败:", error);
  }

  // 刷新实体列表
  const nodeListData = await getNodeList(-1);
  nodeList.value = nodeListData.data;
}
function openUpdateNodeDialog(row: NodeItem) {
  addOrUpdate.value = "update";
  // 显示添加实体的对话框
  addNodeVisible.value = true;

  console.log("row:", row);

  updateNodeForm.value.kgNodeId = row.kgNodeId;

  // 填充表单数据
  addNodeForm.value = {
    nodeName: row.nodeName,
    nodeType: row.nodeType,
    nodeAttr: row.nodeAttr,
    kgMetaId: row.kgMetaId,
    createUserCode: localStorage.getItem("code") || "",
  };
}

onMounted(async () => {
  try {
    // // 获取实体列表
    // const nodeListData = await getNodeList(-1);
    // nodeList.value = nodeListData.data;
    // console.log("实体列表:", nodeList.value);
    // nodeList.value.forEach((node: NodeItem) => {
    //   node.nodeAttr = node.nodeAttr.replace("\\", "");
    // });
    await loadNodeList();

    // 获取知识图谱列表作为选项
    const kgListData = await getKGList();
    const kgList = kgListData.data;
    console.log("知识图谱列表:", kgList);

    // 构建知识图谱选项
    kgList.forEach((kg: KGItem) => {
      if (kg && kg.kgMetaId != null && kg.kgName != null) {
        kgOptions.value.push({
          value: kg.kgMetaId,
          label: `${kg.kgMetaId}(${kg.kgName})`,
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
.Node-dialog {
  /* 如果有实体页面特有的对话框样式，可以在这里添加 */
}
</style>