<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background: #304156">
      <div class="logo">医生工作台</div>
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/doctor/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/doctor/appointments">
          <el-icon><Calendar /></el-icon>
          <span>预约管理</span>
        </el-menu-item>
        <el-menu-item index="/doctor/diagnosis">
          <el-icon><Document /></el-icon>
          <span>诊断记录</span>
        </el-menu-item>
        <el-menu-item index="/doctor/consultations">
          <el-icon><ChatDotRound /></el-icon>
          <span>在线问诊</span>
        </el-menu-item>
        <el-menu-item index="/doctor/chat">
          <el-icon><ChatLineRound /></el-icon>
          <span>即时通讯</span>
        </el-menu-item>
        <el-menu-item index="/doctor/analytics">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据分析</span>
        </el-menu-item>
        <el-menu-item index="/doctor/profile">
          <el-icon><Setting /></el-icon>
          <span>个人设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:flex-end;background:#fff;box-shadow:0 1px 4px rgba(0,0,0,0.08)">
        <el-dropdown>
          <span style="cursor:pointer;display:flex;align-items:center;gap:8px">
            <el-avatar :size="32" :src="userStore.user?.avatar" style="background:#67c23a">{{ userStore.user?.realName ? userStore.user.realName.charAt(0) : 'D' }}</el-avatar>
            {{ userStore.user?.realName || '医生' }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/doctor/profile')">个人设置</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main style="background:#f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { HomeFilled, Calendar, Document, Setting, ArrowDown, ChatDotRound, ChatLineRound, DataAnalysis } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  letter-spacing: 2px;
}
</style>
