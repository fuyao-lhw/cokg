import Login from "@/views/user/login.vue";
import Index from "@/views/index.vue";
import { createRouter, createWebHistory } from "vue-router";
import Admin from "@/views/admin/admin.vue";
import ManageUser from "@/views/admin/user/manageUser.vue";
import Test from "@/views/test.vue";
import ManageDept from "@/views/admin/dept/manageDept.vue";
import ManageKG from "@/views/admin/knowledge/manageKG.vue";
import ManageRelation from "@/views/admin/knowledge/manageRelation.vue";
import ManageNode from "@/views/admin/knowledge/manageNode.vue";
import GraphList from "@/views/graph/List.vue";
import GraphDetail from "@/views/graph/Detail.vue";
import ManagePermission from "@/views/admin/user/managePermission.vue";
import aiChat from "@/views/ai/chat.vue"
import getEntity from "@/views/ai/getEntity.vue";



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
                            path: "/admin/permission/distribution",
                            component: ManagePermission,
                        },
                        {
                            path: "/admin/knowledge/manageKG",
                            component: ManageKG,
                        },
                        {
                            path: "/admin/knowledge/manageNode",
                            component: ManageNode,
                        },
                        {
                            path: "/admin/knowledge/manageRelation",
                            component: ManageRelation,
                        },

                    ]
                },
                {
                    path: "/graph",
                    redirect: "/graph/list",
                    children: [
                        {
                            path: "/graph/list",
                            component: GraphList,
                        },
                        {
                            path: "/graph/:graphId",
                            component: GraphDetail,
                            props: true
                        },
                    ]
                },
                {
                    path: "/ai",
                    redirect: "/ai/chat",
                    children: [
                        {
                            path: "/ai/chat",
                            component: aiChat,
                        },
                        {
                            path: "/ai/getEntity",
                            component: getEntity,
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

    ],
});

export default router;