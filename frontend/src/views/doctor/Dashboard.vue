<template>
  <div>
    <div class="welcome-banner">
      <div style="display:flex;align-items:center;gap:20px">
        <el-avatar :size="64" :src="userStore.user?.avatar" style="background:rgba(255,255,255,0.3);flex-shrink:0">
          {{ userStore.user?.realName ? userStore.user.realName.charAt(0) : 'D' }}
        </el-avatar>
        <div>
          <h2>{{ userStore.user?.realName || '医生' }}，您好</h2>
          <p>今天是 {{ today }} {{ weekday }}，祝您工作顺利</p>
        </div>
      </div>
    </div>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background:#409eff"><el-icon :size="28"><Calendar /></el-icon></div>
          <div class="stat-value">{{ stats.todayCount || 0 }}</div>
          <div class="stat-label">今日预约</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background:#e6a23c"><el-icon :size="28"><Bell /></el-icon></div>
          <div class="stat-value">{{ stats.pendingCount || 0 }}</div>
          <div class="stat-label">待确认</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background:#409eff"><el-icon :size="28"><CircleCheck /></el-icon></div>
          <div class="stat-value">{{ stats.confirmedCount || 0 }}</div>
          <div class="stat-label">已确认</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background:#67c23a"><el-icon :size="28"><Select /></el-icon></div>
          <div class="stat-value">{{ stats.completedCount || 0 }}</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="16">
        <el-card>
          <template #header>今日预约</template>
          <el-table :data="todayAppointments" stripe>
            <el-table-column label="患者" width="150">
              <template #default="{ row }">
                <div style="display:flex;align-items:center;gap:8px">
                  <el-avatar :size="28" :src="row.patientAvatar">{{ row.patientName ? row.patientName.charAt(0) : '?' }}</el-avatar>
                  <span>{{ row.patientName || row.patientId }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="timeSlot" label="时间段" width="100" />
            <el-table-column prop="symptoms" label="症状描述" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="['warning', 'primary', 'success', 'info'][row.status]">
                  {{ ['待确认', '已确认', '已完成', '已取消'][row.status] }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="todayAppointments.length === 0" description="今日暂无预约" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>快捷操作</template>
          <div style="display:flex;flex-direction:column;gap:12px">
            <el-button type="primary" size="large" @click="$router.push('/doctor/appointments')">
              <el-icon><Calendar /></el-icon> 查看所有预约
            </el-button>
            <el-button type="success" size="large" @click="$router.push('/doctor/diagnosis')">
              <el-icon><Document /></el-icon> 诊断记录
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../store/user'
import { getDoctorStats } from '../../api/stats'
import { getAppointmentList } from '../../api/appointment'
import { Calendar, Bell, CircleCheck, Select, Document } from '@element-plus/icons-vue'

const userStore = useUserStore()
const stats = ref({})
const todayAppointments = ref([])

const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
const weekday = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][new Date().getDay()]

onMounted(async () => {
  const doctorId = userStore.user?.id
  const [statsRes, appointmentsRes] = await Promise.all([
    getDoctorStats(doctorId),
    getAppointmentList({ page: 1, size: 5, doctorId })
  ])
  stats.value = statsRes.data
  todayAppointments.value = appointmentsRes.data.records
})
</script>

<style scoped>
.welcome-banner {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  border-radius: 12px;
  padding: 30px 40px;
  color: #fff;
}
.welcome-banner h2 { font-size: 24px; margin-bottom: 8px; }
.welcome-banner p { font-size: 14px; opacity: 0.85; }
.stat-card { text-align: center; }
.stat-icon { width: 56px; height: 56px; border-radius: 14px; display: flex; align-items: center; justify-content: center; margin: 0 auto 12px; color: #fff; }
.stat-value { font-size: 32px; font-weight: bold; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
</style>
