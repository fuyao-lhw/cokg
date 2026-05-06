<!-- 结构,主体 -->
<template>
  <div class="admin-container">
    <el-container>
      <el-aside width="200px">
        <el-menu default-active="2" class="menu">
          <el-sub-menu index="/baseManage">
            <template #title>
              <span>基础管理</span>
            </template>
            <el-menu-item index="/user/manageUser">
              <template #title>
                <RouterLink to="/admin/user/manageUser" class="link"
                  >用户管理</RouterLink
                >
              </template>
            </el-menu-item>
            <el-menu-item index="/dept/manageDept">
              <template #title>
                <RouterLink to="/admin/dept/manageDept" class="link"
                  >管理部门</RouterLink
                >
              </template>
            </el-menu-item>
            <el-menu-item index="/permission/distribution">
              <template #title>
                <RouterLink to="/admin/permission/distribution" class="link">
                  权限管理
                </RouterLink>
              </template>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="/knowledgeManage">
            <template #title>
              <span>知识图谱管理</span>
            </template>
            <el-menu-item index="/knowledge/manageKG">
              <template #title>
                <RouterLink to="/admin/knowledge/manageKG" class="link"
                  >知识图谱管理</RouterLink
                >
              </template>
            </el-menu-item>
            <el-menu-item index="/knowledge/manageEntity">
              <template #title>
                <RouterLink to="/admin/knowledge/manageNode" class="link"
                  >节点（实体）管理</RouterLink
                >
              </template>
            </el-menu-item>
            <el-menu-item index="/knowledge/manageRelation">
              <template #title>
                <RouterLink to="/admin/knowledge/manageRelation" class="link"
                  >节点（实体）关系管理</RouterLink
                >
              </template>
            </el-menu-item>
            
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header"> 管理员界面 </el-header>
        <el-main class="main">
          <RouterView></RouterView>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<!-- 交互,脚本语言 -->
<script setup name="adminConsole" lang="ts">
</script>

<!-- 样式 -->
<style scoped>
.header {
  text-align: center;
  font-size: 30px;
}
</style>