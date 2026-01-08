interface KGItem {
    kgId: string;
    name: string;
    description: string;
    createTime: string;
    updateTime: string;
}

interface EntityItem {
    entityId: string;
    name: string;
    type: string;
    kgId: string;
    description: string;
    createTime: string;
}

interface KGOptions {
    value: string;
    label: string;
}

interface submitKGForm {
    kgName: string;
    kgType: number;  // 0-个人；1-部门
    deptId: string;
    createUserCode: string | null;
}


export { type KGItem, type EntityItem, type KGOptions, type submitKGForm }