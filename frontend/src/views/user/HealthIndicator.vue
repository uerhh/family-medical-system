<template>
  <div>
    <div class="page-header">
      <h2>健康指标</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        添加记录
      </el-button>
    </div>

    <!-- 健康提醒 -->
    <el-card v-if="tips.length" class="tips-card" style="margin-bottom: 20px;">
      <template #header>
        <span>健康提醒</span>
      </template>
      <el-alert
        v-for="(tip, index) in tips"
        :key="index"
        :title="tip.message"
        :type="tip.level === 'danger' ? 'error' : tip.level"
        :closable="false"
        show-icon
        style="margin-bottom: 10px;"
      />
    </el-card>

    <!-- 图表区域 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="chart-header">
          <span>趋势图</span>
          <el-select v-model="selectedType" @change="loadChart" style="width: 200px;">
            <el-option label="收缩压" value="blood_pressure_systolic" />
            <el-option label="舒张压" value="blood_pressure_diastolic" />
            <el-option label="血糖" value="blood_sugar" />
            <el-option label="心率" value="heart_rate" />
            <el-option label="体重" value="weight" />
            <el-option label="身高" value="height" />
            <el-option label="体温" value="temperature" />
            <el-option label="血氧" value="blood_oxygen" />
          </el-select>
        </div>
      </template>
      <div ref="chartRef" style="height: 350px;"></div>
    </el-card>

    <!-- 记录列表 -->
    <el-card>
      <template #header>
        <span>测量记录</span>
      </template>
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="indicatorType" label="指标类型" width="140">
          <template #default="{ row }">
            {{ typeLabels[row.indicatorType] || row.indicatorType }}
          </template>
        </el-table-column>
        <el-table-column prop="indicatorValue" label="测量值" width="120" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="measureTime" label="测量时间" width="180" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <el-pagination
        v-if="total > 0"
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 添加记录弹窗 -->
    <el-dialog v-model="showAddDialog" title="添加健康指标" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="指标类型">
          <el-select v-model="form.indicatorType" @change="onTypeChange">
            <el-option label="收缩压" value="blood_pressure_systolic" />
            <el-option label="舒张压" value="blood_pressure_diastolic" />
            <el-option label="血糖" value="blood_sugar" />
            <el-option label="心率" value="heart_rate" />
            <el-option label="体重" value="weight" />
            <el-option label="身高" value="height" />
            <el-option label="体温" value="temperature" />
            <el-option label="血氧" value="blood_oxygen" />
          </el-select>
        </el-form-item>
        <el-form-item label="测量值">
          <el-input-number v-model="form.indicatorValue" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" disabled />
        </el-form-item>
        <el-form-item label="测量时间">
          <el-date-picker v-model="form.measureTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { addIndicator, getIndicatorList, getIndicatorChart, getHealthTips } from '../../api/healthIndicator'

const typeLabels = {
  blood_pressure_systolic: '收缩压',
  blood_pressure_diastolic: '舒张压',
  blood_sugar: '血糖',
  heart_rate: '心率',
  weight: '体重',
  height: '身高',
  temperature: '体温',
  blood_oxygen: '血氧'
}

const typeUnits = {
  blood_pressure_systolic: 'mmHg',
  blood_pressure_diastolic: 'mmHg',
  blood_sugar: 'mmol/L',
  heart_rate: 'bpm',
  weight: 'kg',
  height: 'cm',
  temperature: '℃',
  blood_oxygen: '%'
}

const selectedType = ref('weight')
const chartRef = ref(null)
let chartInstance = null
const tips = ref([])
const records = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const showAddDialog = ref(false)

const form = ref({
  indicatorType: 'weight',
  indicatorValue: null,
  unit: 'kg',
  measureTime: '',
  remark: ''
})

const onTypeChange = (type) => {
  form.value.unit = typeUnits[type] || ''
}

const loadChart = async () => {
  try {
    const res = await getIndicatorChart({ indicatorType: selectedType.value })
    const data = res.data || []
    if (chartInstance) {
      chartInstance.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: data.map(d => d.measureTime?.substring(0, 16) || ''),
          axisLabel: { rotate: 30 }
        },
        yAxis: { type: 'value' },
        series: [{
          data: data.map(d => d.indicatorValue),
          type: 'line',
          smooth: true,
          areaStyle: { opacity: 0.3 },
          itemStyle: { color: '#409eff' }
        }]
      })
    }
  } catch (e) {
    console.error(e)
  }
}

const loadTips = async () => {
  try {
    const res = await getHealthTips()
    tips.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadRecords = async () => {
  loading.value = true
  try {
    const res = await getIndicatorList({ page: page.value, size: pageSize })
    records.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (p) => {
  page.value = p
  loadRecords()
}

const handleAdd = async () => {
  if (!form.value.indicatorValue) {
    ElMessage.warning('请输入测量值')
    return
  }
  if (!form.value.measureTime) {
    ElMessage.warning('请选择测量时间')
    return
  }
  try {
    await addIndicator(form.value)
    ElMessage.success('添加成功')
    showAddDialog.value = false
    form.value.indicatorValue = null
    form.value.remark = ''
    loadRecords()
    loadChart()
    loadTips()
  } catch (e) {
    console.error(e)
  }
}

const handleResize = () => {
  chartInstance?.resize()
}

onMounted(async () => {
  await nextTick()
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value)
  }
  loadChart()
  loadTips()
  loadRecords()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
