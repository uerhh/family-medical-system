<template>
  <div>
    <h2 class="page-title">数据分析</h2>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;">
          <template #header>个人预约趋势</template>
          <div ref="personalTrendRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;">
          <template #header>患者年龄分布</template>
          <div ref="ageDistRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;">
          <template #header>患者性别分布</template>
          <div ref="genderDistRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;">
          <template #header>诊断分类统计</template>
          <div ref="diagCategoryRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDoctorPersonalTrend, getPatientDemographics, getDiagnosisCategories } from '../../api/analytics'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const doctorId = userStore.user?.id

const personalTrendRef = ref(null)
const ageDistRef = ref(null)
const genderDistRef = ref(null)
const diagCategoryRef = ref(null)

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
    const [trendRes, demoRes, diagRes] = await Promise.all([
      getDoctorPersonalTrend(doctorId),
      getPatientDemographics(doctorId),
      getDiagnosisCategories(doctorId)
    ])

    const trendData = trendRes.data || []
    initChart(personalTrendRef, {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trendData.map(d => d.month) },
      yAxis: { type: 'value' },
      series: [{
        data: trendData.map(d => d.count),
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#409eff' }
      }]
    })

    const demoData = demoRes.data || []
    const ageData = demoData.find(d => d.type === 'age')?.data || []
    const genderData = demoData.find(d => d.type === 'gender')?.data || []

    initChart(ageDistRef, {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ageData.map(d => d.name) },
      yAxis: { type: 'value' },
      series: [{
        type: 'bar',
        data: ageData.map(d => d.value),
        itemStyle: { color: '#67c23a' }
      }]
    })

    initChart(genderDistRef, {
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '50%',
        data: genderData,
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } }
      }]
    })

    const diagData = diagRes.data || []
    initChart(diagCategoryRef, {
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        type: 'pie',
        radius: ['30%', '60%'],
        data: diagData,
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } }
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
