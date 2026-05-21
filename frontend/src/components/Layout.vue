<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <el-icon :size="24"><DataBoard /></el-icon>
        <span v-show="!isCollapse" class="logo-text">CRM系统</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#409EFF"
        class="aside-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>

        <el-sub-menu index="crm">
          <template #title>
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </template>
          <el-menu-item index="/leads">线索管理</el-menu-item>
          <el-menu-item index="/customers">客户列表</el-menu-item>
          <el-menu-item index="/follow-ups">跟进记录</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="biz">
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>业务管理</span>
          </template>
          <el-menu-item index="/opportunities">商机看板</el-menu-item>
          <el-menu-item index="/deals">成单管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="ai">
          <template #title>
            <el-icon><MagicStick /></el-icon>
            <span>智能助手</span>
          </template>
          <el-menu-item index="/knowledge">知识库</el-menu-item>
          <el-menu-item index="/ai-search">AI查询</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/map">
          <el-icon><MapLocation /></el-icon>
          <template #title>客户地图</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            :size="20"
            @click="isCollapse = !isCollapse"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="3" class="notification-badge">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" style="background: #409EFF;">{{ userInitial }}</el-avatar>
              <span class="user-name">{{ userName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const userName = ref('')

const userInitial = computed(() => {
  return userName.value ? userName.value.charAt(0) : '?'
})

const loadUserInfo = () => {
  try {
    const userStr = localStorage.getItem('crm_user')
    if (userStr) {
      const user = JSON.parse(userStr)
      userName.value = user.realName || user.username || '用户'
    }
  } catch (error) {
    console.error('Failed to load user info:', error)
    userName.value = '用户'
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    localStorage.removeItem('crm_token')
    localStorage.removeItem('crm_user')
    router.push('/login')
  }).catch(() => {
    // 用户取消退出
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #001529;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  border-bottom: 1px solid #ffffff1a;
}

.logo-text {
  white-space: nowrap;
}

.aside-menu {
  border-right: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.layout-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  z-index: 10;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: #333;
  transition: color 0.3s;
}
.collapse-btn:hover {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-badge {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name {
  font-size: 14px;
  color: #333;
}

.layout-main {
  background: #f0f2f5;
  overflow-y: auto;
}
</style>
