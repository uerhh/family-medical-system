<template>
  <div>
    <div class="page-header">
      <h2>在线问诊</h2>
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="loadData" style="width: 150px;">
        <el-option label="全部" :value="null" />
        <el-option label="待回复" :value="0" />
        <el-option label="已回复" :value="1" />
        <el-option label="已关闭" :value="2" />
      </el-select>
    </div>

    <el-table :data="list" v-loading="loading" stripe @row-click="goDetail">
      <el-table-column prop="patientName" label="患者" width="120" />
      <el-table-column prop="title" label="咨询标题" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getConsultationList } from '../../api/consultation'

const router = useRouter()
const list = ref([])
const loading = ref(false)
const statusFilter = ref(null)

const statusText = (s) => ['待回复', '已回复', '已关闭'][s] || '未知'
const statusType = (s) => ['warning', 'success', 'info'][s] || 'info'

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 100 }
    if (statusFilter.value !== null) params.status = statusFilter.value
    const res = await getConsultationList(params)
    list.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

const goDetail = (row) => {
  router.push('/doctor/consultation/' + row.id)
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 { margin: 0; font-size: 20px; }
</style>
