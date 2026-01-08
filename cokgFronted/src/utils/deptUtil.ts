import axios from "axios";


async function getDeptList() {
    const res = await axios.get("/api/dept/list", {
        headers: { Authorization: localStorage.getItem("token") }
    });

    console.log("获取部门列表成功", res.data);

    return res.data;
}

async function addDept(data) {
    const res = await axios.post("/api/dept/add", data);

    console.log("添加部门成功", res.data);

    return res.data;
}

export { getDeptList, addDept };