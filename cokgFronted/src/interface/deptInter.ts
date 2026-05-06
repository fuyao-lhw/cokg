interface DeptItem {
    deptId: string | number;
    name: string;
}

interface DeptOption {
    value: string | number;
    label: string;
}

export type { DeptItem, DeptOption }