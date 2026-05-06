import axios from "axios";
import { ElMessage } from "element-plus";
import  router from "@/router";

async function submitLogin(code: string, password: string) {
    const res = await axios.post("/api/user/login", {
        code: code,
        password: password,
    });

    console.log(res.data);

    // 处理登录
    if (res.data.code !== 200) {
        ElMessage.error(res.data.msg);
    }
    ElMessage.success({
        message: `${res.data.msg}，欢迎您，${res.data.data.name}`,
        duration: 3000,
    });
    localStorage.setItem("token", res.data.data.token);
    localStorage.setItem("name", res.data.data.name);
    localStorage.setItem("code", res.data.data.code);
    router.push("/index");
}

export { submitLogin }