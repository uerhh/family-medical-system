<template>
  <div class="dashboard">
    <h2 class="page-title">系统概览</h2>
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card blue">
          <div class="stat-icon"><el-icon :size="40"><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.userCount || 0 }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card green">
          <div class="stat-icon"><el-icon :size="40"><Avatar /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.doctorCount || 0 }}</div>
            <div class="stat-label">医生总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card orange">
          <div class="stat-icon"><el-icon :size="40"><Calendar /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayAppointments || 0 }}</div>
            <div class="stat-label">今日预约</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card red">
          <div class="stat-icon"><el-icon :size="40"><Bell /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingAppointments || 0 }}</div>
            <div class="stat-label">待处理预约</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card>
          <template #header>预约统计</template>
          <div class="chart-placeholder">
            <div class="bar-chart">
              <div class="bar-item" v-for="item in chartData" :key="item.label">
                <div class="bar-label">{{ item.label }}</div>
                <div class="bar-track">
                  <div class="bar-fill" :style="{ width: item.percent + '%', background: item.color }"></div>
                </div>
                <div class="bar-value">{{ item.value }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>快捷操作</template>
          <div class="quick-actions">
            <el-button type="primary" size="large" @click="$router.push('/admin/users')">
              <el-icon><User /></el-icon> 用户管理
            </el-button>
            <el-button type="success" size="large" @click="$router.push('/admin/doctors')">
              <el-icon><Avatar /></el-icon> 医生管理
            </el-button>
            <el-button type="warning" size="large" @click="$router.push('/admin/appointments')">
              <el-icon><Calendar /></el-icon> 预约管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getOverview } from '../../api/stats'
import { User, Avatar, Calendar, Bell } from '@element-plus/icons-vue'

const stats = ref({})

const chartData = computed(() => {
  const total = stats.value.totalAppointments || 1
  return [
    { label: '待确认', value: stats.value.pendingAppointments || 0, percent: ((stats.value.pendingAppointments || 0) / total * 100), color: '#e6a23c' },
    { label: '已确认', value: (stats.value.totalAppointments || 0) - (stats.value.pendingAppointments || 0) - (stats.value.completedAppointments || 0), percent: (((stats.value.totalAppointments || 0) - (stats.value.pendingAppointments || 0) - (stats.value.completedAppointments || 0)) / total * 100), color: '#409eff' },
    { label: '已完成', value: stats.value.completedAppointments || 0, percent: ((stats.value.completedAppointments || 0) / total * 100), color: '#67c23a' }
  ]
})

onMounted(async () => {
  const res = await getOverview()
  stats.value = res.data
})
</script>

<style scoped>
.page-title { margin-bottom: 20px; font-size: 20px; color: #303133; }
.stat-card { display: flex; align-items: center; padding: 10px 0; }
.stat-card .stat-icon { width: 80px; display: flex; justify-content: center; }
.stat-card .stat-info { flex: 1; }
.stat-value { font-size: 32px; font-weight: bold; line-height: 1.2; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.stat-card.blue .stat-icon { color: #409eff; }
.stat-card.blue .stat-value { color: #409eff; }
.stat-card.green .stat-icon { color: #67c23a; }
.stat-card.green .stat-value { color: #67c23a; }
.stat-card.orange .stat-icon { color: #e6a23c; }
.stat-card.orange .stat-value { color: #e6a23c; }
.stat-card.red .stat-icon { color: #f56c6c; }
.stat-card.red .stat-value { color: #f56c6c; }
.bar-chart { padding: 10px 0; }
.bar-item { display: flex; align-items: center; margin-bottom: 20px; }
.bar-label { width: 60px; font-size: 14px; color: #606266; }
.bar-track { flex: 1; height: 24px; background: #f0f2f5; border-radius: 12px; overflow: hidden; margin: 0 12px; }
.bar-fill { height: 100%; border-radius: 12px; transition: width 0.6s ease; min-width: 8px; }
.bar-value { width: 40px; text-align: right; font-weight: bold; color: #303133; }
.quick-actions { display: flex; gap: 16px; padding: 20px 0; flex-wrap: wrap; }
</style>
