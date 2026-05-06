
interface KGItem {
    kgMetaId: number;
    kgName: string;
    kgType: number;
    deptId: number;
    createTime: string;
    updateTime: string;
}

interface NodeItem {
    kgNodeId: number;
    nodeName: string;
    nodeType: string;
    kgMetaId: number;
    nodeAttr: string;
}

interface KGOptions {
    value: number;
    label: string;
}

interface NodeOptions { 
    value: string;
    label: string;
}



interface RelationItem {
    kgRelationId: number;
    kgId: number;
    kgNodeSrcId: number;
    kgNodeTargetId: number;
    relationType: string;
    properties: string;
    weight: number;
    createUserId: number;
    createTime: string;
    updateTime: string;
    isDeleted: number;
    kgMetaId: number;
}

// params
interface submitKGForm {
    kgName: string;
    kgType: number | null;  // 0-个人；1-部门
    deptId: number | null;
    createUserCode: string | null;
}

interface updateKGForm {
    kgMetaId: number | null;
    kgName: string;
    kgType: number | null;
    deptId: number | null;
}

interface addNodeForm {
    nodeName: string,
    nodeType: string,
    kgMetaId: number,
    nodeAttr: string,
    createUserCode: string;
}

interface updateNodeForm {
    kgNodeId: number | null,
    nodeName: string,
    nodeType: string,
    kgMetaId: number | null,
    nodeAttr: string,
}

interface addRelationForm {
    kgMetaId: number | null,
    kgNodeSrcId: number | null,
    kgNodeTargetId: number | null,
    relationType: string,
    properties: string,
    weight: number | null,
    createUserCode: string;
}

interface updateRelationForm {
    kgRelationId: number | null,
    kgMetaId: number | null,
    kgNodeSrcId: number | null,
    kgNodeTargetId: number | null,
    relationType: string,
    properties: string,
    weight: number | null,
}


export {
    type KGItem, type NodeItem, type NodeOptions, type KGOptions, type submitKGForm, type RelationItem,
    type addNodeForm, type addRelationForm, type updateKGForm, type updateNodeForm, type updateRelationForm

  }