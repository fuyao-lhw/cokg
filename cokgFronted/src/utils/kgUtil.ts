import { getByAuth, postByAuth, uploadFile } from "./requestUtil"
import {
    type addNodeForm, type submitKGForm, type addRelationForm, type updateKGForm, type updateNodeForm,
    type updateRelationForm

} from "@/interface/knowledgeInter";
import { ElMessageBox } from "element-plus";

// 知识图谱
async function getKGList() {
    const res = await getByAuth('/api/knowledge/list', localStorage.getItem("token"));
    return res?.data
}

async function batchGetKGList(kgMetaIdList: number[]) {
    const res = await postByAuth('/api/knowledge/list/batch', localStorage.getItem("token"), kgMetaIdList);
    return res.data;
}


async function deleteKG(kgMetaId: number) {
    await ElMessageBox.confirm("确定要删除这个知识图谱吗？", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
    });
    const res = await postByAuth('/api/knowledge/delete', localStorage.getItem("token"), kgMetaId);
    return res.data;
 }


async function addKG(data: submitKGForm) {
    console.log("添加知识图谱", data);
    const res = await postByAuth('/api/knowledge/add', localStorage.getItem("token"), data);
    console.log("添加知识图谱成功", res.data);
    return res.data.data;
}

async function updateKG(data: updateKGForm) {
    const res = await postByAuth('/api/knowledge/update', localStorage.getItem("token"), data);
    return res.data;
}


// 节点
async function getNodeList(kgMetaId: number) {
    const res = await getByAuth(`/api/knowledge/nodeList`, localStorage.getItem("token"), {
        kgMetaId: kgMetaId
    })
    return res.data;
}

async function batchGetNodeList(kgNodeIdList: number[]) {
    const res = await postByAuth(`/api/knowledge/nodeList/batch`, localStorage.getItem("token"), kgNodeIdList)
    return res.data;
}

async function deleteNode(kgNodeId: number) {
    const res = await postByAuth('/api/knowledge/deleteNode', localStorage.getItem("token"), kgNodeId)
    return res.data;
}

async function addNode(addNodeForm: addNodeForm) {
    const res = await postByAuth(`/api/knowledge/addNode`, localStorage.getItem("token"), addNodeForm)
    return res.data;
}

async function updateNode(updateNodeForm: updateNodeForm) {
    const res = await postByAuth(`/api/knowledge/updateNode`, localStorage.getItem("token"), updateNodeForm)
    return res.data;
}


// 节点关系

async function addRelation(addRelationForm: addRelationForm) {
    const res = await postByAuth(`/api/knowledge/addRelation`, localStorage.getItem("token"), addRelationForm)
    return res.data;
}

async function deleteRelation(kgRelationId: number) {
    const res = await postByAuth(`/api/knowledge/deleteRelation`, localStorage.getItem("token"), { kgRelationId: kgRelationId })
    return res.data;

}

async function updateRelation(updateRelationForm: updateRelationForm) {
    const res = await postByAuth(`/api/knowledge/updateRelation`, localStorage.getItem("token"), updateRelationForm)
    return res.data;
}

async function getRelationList(kgMetaId: number) {
    return await getByAuth(`/api/knowledge/relationList`, localStorage.getItem("token"), {
        kgMetaId: kgMetaId
    });


}

async function batchAddKg(formData: FormData) {
    return await uploadFile(`/api/file/upload`,
        localStorage.getItem("token"),
        formData
    )
}

export {
    getKGList, deleteKG, addKG, addRelation, deleteRelation, updateRelation, getRelationList, getNodeList, deleteNode, addNode,
    updateKG, updateNode, batchGetKGList, batchGetNodeList, batchAddKg
}