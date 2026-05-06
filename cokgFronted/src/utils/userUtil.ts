import axios from "axios";
import { getByAuth } from "@/utils/requestUtil";
import { type addUserItem } from "@/interface/userInter";

// 获取用户列表
async function getUserList() {
    const res = await axios.get("/api/user/list", {
        headers: { Authorization: localStorage.getItem("token") }
        }
    );

    console.log("获取用户列表成功", res.data);

    return res.data;

}

// 添加用户
async function addUser(data: addUserItem) {

    const res = await axios.post("/api/user/add", data);

    console.log("添加用户成功", res.data);

    return;

}

// 删除用户
async function deleteUser(id) {


    return;

}

async function getUserPermissions() {
    const res = await getByAuth('/api/user/permission/user_share', localStorage.getItem("token"))
    return res.data;
}

export { getUserList, deleteUser, addUser, getUserPermissions };