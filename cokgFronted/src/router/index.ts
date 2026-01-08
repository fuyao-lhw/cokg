import Login from "@/views/user/login.vue";
import Index from "@/views/index.vue";
import { createRouter, createWebHistory } from "vue-router";
import Admin from "@/views/admin/admin.vue";
import ManageUser from "@/views/admin/user/manageUser.vue";
import Test from "@/views/test.vue";
import ManageDept from "@/views/admin/dept/manageDept.vue";
import ManageKG from "@/views/admin/knowledge/manageKG.vue";
import ManageEntity from "@/views/admin/knowledge/manageEntity.vue";



const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            "path": "/",
            "redirect": "/index"
        },
        {
            path: "/index",
            component: Index,
            children: [
                {
                    path: "/admin",
                    component: Admin,
                    children: [
                        {
                            path: "/admin/user/manageUser",
                            component: ManageUser,
                        },
                        {
                            path: "/admin/dept/manageDept",
                            component: ManageDept,
                        },
                        {
                            path: "/admin/knowledge/manageKG",
                            component: ManageKG,
                        },
                        {
                            path: "/admin/knowledge/manageEntity",
                            component: ManageEntity,
                        },
                    ]
                },
            ]
        },
        {
            path: "/login",
            component: Login,
        },
        {
            path: "/test",
            component: Test,
        },
        // {
        //     path: "/admin",
        //     component: Admin,
        // },
    ],
});

export default router;