import {getByAuth, postByAuth} from "./requestUtil"


async function getDeptList() {
    // const res = await axios.get("/api/dept/list", {
    //     headers: { Authorization: localStorage.getItem("token") }
    // });

    const res = await getByAuth("/api/dept/list", localStorage.getItem("token"));

    console.log("获取部门列表成功", res.data);

    return res.data;
}

async function addDept(data) {
    const res = await postByAuth("/api/dept/add", localStorage.getItem("token"), data);

    console.log("添加部门成功", res.data);

    return res.data.data;
}

export { getDeptList, addDept };