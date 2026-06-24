<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>预约管理</span>
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:150px" @change="loadData">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="患者" width="150">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <el-avatar :size="28" :src="row.patientAvatar">{{ row.patientName ? row.patientName.charAt(0) : '?' }}</el-avatar>
              <span>{{ row.patientName || row.patientId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="医生" width="150">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <el-avatar :size="28" :src="row.doctorAvatar">{{ row.doctorName ? row.doctorName.charAt(0) : '?' }}</el-avatar>
              <span>{{ row.doctorName || row.doctorId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="80" />
        <el-table-column prop="symptoms" label="症状描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="['warning', 'primary', 'success', 'info'][row.status]">
              {{ ['待确认', '已确认', '已完成', '已取消'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" background layout="total, prev, pager, next"
        :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAppointmentList } from '../../api/appointment'

const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const statusFilter = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (statusFilter.value !== null) params.status = statusFilter.value
    const res = await getAppointmentList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
