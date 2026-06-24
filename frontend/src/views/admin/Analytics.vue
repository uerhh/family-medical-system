<template>
  <div>
    <h2 class="page-title">数据分析</h2>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;">
          <template #header>用户增长趋势</template>
          <div ref="userGrowthRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;">
          <template #header>预约趋势</template>
          <div ref="appointmentTrendRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;">
          <template #header>科室分布</template>
          <div ref="deptDistRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;">
          <template #header>医生工作量排名</template>
          <div ref="doctorWorkloadRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getUserGrowthTrend, getAppointmentTrend, getDepartmentDistribution, getDoctorWorkload } from '../../api/analytics'

const userGrowthRef = ref(null)
const appointmentTrendRef = ref(null)
const deptDistRef = ref(null)
const doctorWorkloadRef = ref(null)

const charts = []

const initChart = async (refEl, option) => {
  await nextTick()
  if (!refEl.value) return null
  const chart = echarts.init(refEl.value)
  chart.setOption(option)
  charts.push(chart)
  return chart
}

const loadData = async () => {
  try {
    const [growthRes, trendRes, deptRes, workloadRes] = await Promise.all([
      getUserGrowthTrend(),
      getAppointmentTrend(),
      getDepartmentDistribution(),
      getDoctorWorkload()
    ])

    const growthData = growthRes.data || []
    initChart(userGrowthRef, {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: growthData.map(d => d.month) },
      yAxis: { type: 'value' },
      series: [{
        data: growthData.map(d => d.count),
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#409eff' }
      }]
    })

    const trendData = trendRes.data || []
    initChart(appointmentTrendRef, {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trendData.map(d => d.month) },
      yAxis: { type: 'value' },
      series: [{
        data: trendData.map(d => d.count),
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#67c23a' }
      }]
    })

    const deptData = deptRes.data || []
    initChart(deptDistRef, {
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        type: 'pie',
        radius: '50%',
        data: deptData,
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } }
      }]
    })

    const workloadData = workloadRes.data || []
    initChart(doctorWorkloadRef, {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: workloadData.map(d => d.name).reverse() },
      series: [{
        type: 'bar',
        data: workloadData.map(d => d.count).reverse(),
        itemStyle: { color: '#e6a23c' }
      }]
    })
  } catch (e) {
    console.error(e)
  }
}

const handleResize = () => charts.forEach(c => c.resize())

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c.dispose())
})
</script>

<style scoped>
.page-title { margin: 0 0 20px; font-size: 20px; }
</style>
