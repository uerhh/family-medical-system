<template>
  <div>
    <div class="welcome-banner">
      <div style="display:flex;align-items:center;gap:20px">
        <el-avatar :size="64" :src="userStore.user?.avatar" style="background:rgba(255,255,255,0.3);flex-shrink:0">
          {{ userStore.user?.realName ? userStore.user.realName.charAt(0) : 'U' }}
        </el-avatar>
        <div class="welcome-text">
          <h2>您好，{{ userStore.user?.realName || '用户' }}</h2>
          <p>欢迎使用家庭医疗系统，祝您身体健康</p>
        </div>
      </div>
      <div class="welcome-date">
        <div class="date">{{ today }}</div>
        <div class="weekday">{{ weekday }}</div>
      </div>
    </div>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6" v-for="item in quickMenus" :key="item.path">
        <el-card shadow="hover" class="quick-card" @click="$router.push(item.path)">
          <div class="quick-icon" :style="{ background: item.color }">
            <el-icon :size="28"><component :is="item.icon" /></el-icon>
          </div>
          <div class="quick-title">{{ item.title }}</div>
          <div class="quick-desc">{{ item.desc }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="16">
        <el-card>
          <template #header>系统公告</template>
          <el-timeline>
            <el-timeline-item timestamp="2026-05-11" placement="top">
              <el-card shadow="never"><h4>系统上线</h4><p>家庭医疗系统正式上线，欢迎使用！</p></el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2026-05-11" placement="top">
              <el-card shadow="never"><h4>功能说明</h4><p>您可以通过"找医生"功能浏览医生并在线预约挂号。</p></el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>健康提示</template>
          <div class="health-tips">
            <div class="tip-item" v-for="(tip, i) in tips" :key="i">
              <el-icon style="color:#67c23a"><CircleCheck /></el-icon>
              <span>{{ tip }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '../../store/user'
import { Calendar, FirstAidKit, Document, Avatar, CircleCheck } from '@element-plus/icons-vue'

const userStore = useUserStore()

const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
const weekday = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][new Date().getDay()]

const quickMenus = [
  { path: '/user/doctors', icon: Avatar, title: '找医生', desc: '浏览医生列表', color: '#409eff' },
  { path: '/user/appointment', icon: Calendar, title: '预约挂号', desc: '在线预约', color: '#67c23a' },
  { path: '/user/health-record', icon: FirstAidKit, title: '健康档案', desc: '管理健康信息', color: '#e6a23c' },
  { path: '/user/diagnosis', icon: Document, title: '诊断记录', desc: '查看历史诊断', color: '#f56c6c' }
]

const tips = [
  '每天保持8小时充足睡眠',
  '均衡饮食，多吃蔬菜水果',
  '坚持适量运动，每周3-5次',
  '定期体检，关注健康指标',
  '保持良好心态，减少压力'
]
</script>

<style scoped>
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
}
.welcome-text h2 { font-size: 24px; margin-bottom: 8px; }
.welcome-text p { font-size: 14px; opacity: 0.85; }
.welcome-date { text-align: right; }
.date { font-size: 18px; font-weight: bold; }
.weekday { font-size: 14px; opacity: 0.85; margin-top: 4px; }
.quick-card { text-align: center; cursor: pointer; transition: transform 0.2s; padding: 20px 10px; }
.quick-card:hover { transform: translateY(-4px); }
.quick-icon { width: 56px; height: 56px; border-radius: 14px; display: flex; align-items: center; justify-content: center; margin: 0 auto 12px; color: #fff; }
.quick-title { font-size: 16px; font-weight: bold; color: #303133; }
.quick-desc { font-size: 12px; color: #909399; margin-top: 4px; }
.health-tips .tip-item { display: flex; align-items: center; gap: 8px; padding: 8px 0; font-size: 14px; color: #606266; }
</style>
