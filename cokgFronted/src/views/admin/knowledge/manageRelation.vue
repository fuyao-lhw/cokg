<!-- 结构,主体 -->
<template>
  <div class="admin-container">
    <!-- 表格上方的添加按钮 -->
    <div class="table-header">
      <el-button
        type="primary"
        @click="openAddRelationDialog"
        :icon="Plus"
        class="add-button"
      >
        添加关系
      </el-button>
      <el-button type="primary" @click="openBatchDialog" :icon="Upload">
        批量导入
      </el-button>
      <el-select
        v-model="selectedKgId"
        placeholder="下拉筛选对应数据"
        filterable
        clearable
        style="width: 200px; margin-left: 10px"
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
      title="批量导入节点关系"
      @submit="(files) => handleSubmitFile(files, 'kgNodeRelation')"
    />

    <!-- 关系表格 -->
    <el-table
      :data="relationList"
      style="width: 100%;min-width: 1200px;"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      class="admin-table"
    >
      <el-table-column
        prop="kgRelationId"
        label="关系ID"
        width="100"
      ></el-table-column>
      <el-table-column prop="kgMetaId" label="知识图谱ID" width="120">
        <template #default="{ row }">
          {{ `${row.kgMetaId}(${kgIdNameMap.get(row.kgMetaId)})` }}
        </template>
      </el-table-column>
      <el-table-column prop="kgNodeSrcId" label="源节点ID" width="120">
        <template #default="{ row }">
          {{ `${row.kgNodeSrcId}(${nodeIdNameMap.get(row.kgNodeSrcId)})` }}
        </template>
      </el-table-column>
      <el-table-column prop="kgNodeTargetId" label="目标节点ID" width="120">
        <template #default="{ row }">
          {{
            `${row.kgNodeTargetId}(${nodeIdNameMap.get(row.kgNodeTargetId)})`
          }}
        </template>
      </el-table-column>
      <el-table-column
        prop="relationType"
        label="关系类型"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="properties"
        label="关系属性"
        width="200"
      ></el-table-column>
      <el-table-column prop="weight" label="权重" width="100"></el-table-column>
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
            @click="deleteRelation(row.kgRelationId)"
            :icon="Delete"
            class="action-button delete-btn"
          >
            删除
          </el-button>
          <el-button
            type="primary"
            size="small"
            @click="openUpdateRelationDialog(row)"
            :icon="Edit"
            >编辑</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加关系对话框 -->
    <el-dialog
      v-model="addRelationVisible"
      :title="addOrUpdate === 'add' ? '添加关系' : '更新关系'"
      width="800"
      class="relation-dialog"
    >
      <el-form :model="addRelationForm" ref="relationFormRef">
        <el-form-item label="知识图谱:">
          <el-select
            v-model="addRelationForm.kgMetaId"
            placeholder="请选择知识图谱"
            @change="onKGChange"
            clearable
          >
            <el-option
              v-for="item in kgOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="源节点:">
          <el-select
            v-model="addRelationForm.kgNodeSrcId"
            placeholder="请选择源节点"
            clearable
          >
            <el-option
              v-for="item in nodeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="目标节点:">
          <el-select
            placeholder="请选择目标节点"
            v-model="addRelationForm.kgNodeTargetId"
            clearable
          >
            <el-option
              v-for="item in nodeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关系类型:">
          <el-input v-model="addRelationForm.relationType">
            <template #append>
              <el-tooltip class="tooltip-relation-type" placement="top">
                <template #content
                  >关系类型应是文本，例如：创建、属于···</template
                >
                <el-text>填写提示</el-text>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="关系属性:">
          <el-input v-model="addRelationForm.properties">
            <template #append>
              <el-tooltip
                class="tooltip-form-properties"
                content='关系属性应是JSON结构，例如：{"描述":"合作关系", "强度":"高"}'
                placement="top"
              >
                <el-text>填写提示</el-text>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="权重:">
          <el-input
            v-model="addRelationForm.weight"
            placeholder="请输入权重（带小数部分）"
            type="number"
          >
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeRelationDialog" class="dialog-btn cancel-btn"
            >取消</el-button
          >
          <el-button
            type="primary"
            @click="handleSaveRelation"
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
import {
  getRelationList,
  deleteRelation,
  updateRelation,
  addRelation,
  batchGetNodeList,
} from "@/utils/kgUtil";
import { getKGList, getNodeList } from "@/utils/kgUtil";
import {
  type KGItem,
  type KGOptions,
  type NodeItem,
  type RelationItem,
  type NodeOptions,
  type addRelationForm,
  type updateRelationForm,
} from "@/interface/knowledgeInter";
import { onMounted, ref } from "vue";
import { Plus, Delete, Check, Edit, Upload } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import { handleSubmitFile } from "@/utils/fileUtil";
import BatchAddFileDialog from "@/component/dialog/BatchAddFileDialog.vue";

// 数据定义
const relationList = ref<RelationItem[]>([]);
const kgOptions = ref<KGOptions[]>([]);
const addRelationVisible = ref(false);

// 图谱id和name的映射
const kgIdNameMap: Map<number, string> = new Map();

// 节点id和name的映射
const nodeIdNameMap: Map<number, string> = new Map();

// 表单数据
const addRelationForm = ref(<addRelationForm>{
  kgMetaId: null,
  kgNodeSrcId: null,
  kgNodeTargetId: null,
  relationType: "",
  properties: "",
  weight: null,
  createUserCode: localStorage.getItem("code") || "",
});

const nodeOptions = ref<NodeOptions[]>([]); // 节点选项

const addOrUpdate = ref("add");

const updateRelationForm = ref(<updateRelationForm>{
  kgMetaId: null,
  kgNodeSrcId: null,
  kgNodeTargetId: null,
  relationType: "",
  properties: "",
  weight: null,
});

const batchDialogVisible = ref(false);

// ... 其他 ref 定义
const selectedKgId = ref<number>(-1); // 用于存储选中的知识图谱ID，1表示全部

// 处理知识图谱筛选变化
async function handleKgFilterChange() {
  await loadRelationList();
}

async function loadRelationList() {
  // 如果 selectedKgId 为 null 或 undefined，传 -1 或其他代表“全部”的值
  // 具体取决于你的后端接口定义，这里假设 null/undefined 时传 -1
  const kgIdParam = selectedKgId.value ?? -1;

  const relationListData = await getRelationList(kgIdParam);

  // 注意：根据你的原代码，relationListData.data.data 才是数组
  // 请根据实际接口返回结构调整，原代码中是 relationListData.data.data
  const relations = relationListData.data.data || [];

  relationList.value = relations;

    await loadNodeNamesForRelations(relationList.value);

  
}

function openBatchDialog() {
  batchDialogVisible.value = true;
}

// 方法定义
// 加载知识图谱选项
async function loadKGOptions() {
  try {
    const kgListData = await getKGList();
    const kgList = kgListData.data;

    console.log("知识图谱列表:", kgList);

    kgOptions.value = kgList.map((kg: KGItem) => ({
      value: kg.kgMetaId,
      label: kg.kgName,
    }));

    kgList.forEach((kg: KGItem) => {
      if (kg && kg.kgMetaId != null && kg.kgName != null) {
        kgIdNameMap.set(kg.kgMetaId, kg.kgName);
      }
    });
  } catch (error) {
    console.error("加载知识图谱失败:", error);
  }
}

async function onKGChange(kgId: number) {
  // 清空节点选择
  addRelationForm.value.kgNodeSrcId = null;
  addRelationForm.value.kgNodeTargetId = null;

  // 加载节点选项
  await loadNodeOptions(kgId);
}

// 加载节点选项（根据知识图谱ID）
async function loadNodeOptions(kgId: number) {
  try {
    const nodeListData = await getNodeList(kgId);
    console.log("节点列表:", nodeListData.data);
    nodeOptions.value = nodeListData.data.map((node: NodeItem) => ({
      value: node.kgNodeId,
      label: `${node.nodeName} （${node.nodeType}）`,
    }));

    console.log("节点选项:", nodeOptions.value);
  } catch (error) {
    console.error("加载节点失败:", error);
  }
}

async function openAddRelationDialog() {
  addRelationVisible.value = true;

  // 清空表单
  addRelationForm.value = {
    kgMetaId: null,
    kgNodeSrcId: null,
    kgNodeTargetId: null,
    relationType: "",
    properties: "",
    weight: null,
    createUserCode: localStorage.getItem("code") || "",
  };

  // 清空节点选项
  nodeOptions.value = [];

  await loadKGOptions();
}

function closeRelationDialog() {
  addRelationVisible.value = false;
}

async function handleSaveRelation() {
  try {
    addRelationVisible.value = false;

    let res = null;

    if (addOrUpdate.value === "add") {
      // 添加关系
      res = await addRelation(addRelationForm.value);
    } else if (addOrUpdate.value === "update") {
      updateRelationForm.value.kgMetaId = addRelationForm.value.kgMetaId;
      updateRelationForm.value.kgNodeSrcId = addRelationForm.value.kgNodeSrcId;
      updateRelationForm.value.kgNodeTargetId =
        addRelationForm.value.kgNodeTargetId;
      updateRelationForm.value.relationType =
        addRelationForm.value.relationType;
      updateRelationForm.value.properties = addRelationForm.value.properties;
      updateRelationForm.value.weight = addRelationForm.value.weight;

      // 修改关系
      res = await updateRelation(updateRelationForm.value);
    }

    if (res.code !== 200) {
      ElMessageBox.alert(res.msg);
    }

    // 刷新关系列表
    window.location.reload();
  } catch (error) {
    console.error("保存关系失败:", error);
  }
}

async function deleteRelation(relationId: number) {
  try {
    // 确认删除
    await ElMessageBox.confirm("确定要删除这个关系吗？", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 执行删除
    await deleteRelation(relationId);

    // 刷新列表
    const relationListData = await getRelationList(-1);
    relationList.value = relationListData.data;
  } catch (error) {
    console.error("删除关系失败:", error);
  }
}

async function openUpdateRelationDialog(row: RelationItem) {
  addOrUpdate.value = "update";
  addRelationVisible.value = true;

  updateRelationForm.value.kgRelationId = row.kgRelationId;

  addRelationForm.value = {
    kgMetaId: row.kgMetaId,
    kgNodeSrcId: row.kgNodeSrcId,
    kgNodeTargetId: row.kgNodeTargetId,
    relationType: row.relationType,
    properties: row.properties,
    weight: row.weight,
    createUserCode: localStorage.getItem("code") || "",
  };
}

// 新增：根据关系列表加载节点名称映射
async function loadNodeNamesForRelations(relations: RelationItem[]) {
  const nodeIdList: Set<number> = new Set();
  relations.forEach((relation: RelationItem) => {
    if (relation.kgNodeSrcId) nodeIdList.add(relation.kgNodeSrcId);
    if (relation.kgNodeTargetId) nodeIdList.add(relation.kgNodeTargetId);
  });

  if (nodeIdList.size > 0) {
    // 过滤掉已经存在于 Map 中的 ID，避免重复请求（可选优化）
    const newIds = [...nodeIdList].filter(id => !nodeIdNameMap.has(id));
    
    if (newIds.length > 0) {
      try {
        const nodeListData = await batchGetNodeList(newIds);
        nodeListData.data.forEach((node: NodeItem) => {
          if (node && node.kgNodeId != null && node.nodeName != null) {
            nodeIdNameMap.set(node.kgNodeId, node.nodeName);
          }
        });
      } catch (error) {
        console.error("加载节点名称失败:", error);
      }
    }
  }
}

onMounted(async () => {
  try {
    // 1. 先加载知识图谱选项，确保下拉框有数据
    await loadKGOptions();

    // 2. 加载初始的关系列表（默认全部）
    await loadRelationList();


    console.log("kgIdNameMap:", kgIdNameMap);
    console.log("nodeIdNameMap:", nodeIdNameMap);
  } catch (error) {
    console.error("初始化失败:", error);
  }
});
</script>


<!-- 样式 -->
<style scoped>
</style>