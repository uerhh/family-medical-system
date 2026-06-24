<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>我的预约</span>
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:150px" @change="loadData">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="患者" width="150">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <el-avatar :size="28" :src="row.patientAvatar">{{ row.patientName ? row.patientName.charAt(0) : '?' }}</el-avatar>
              <span>{{ row.patientName || row.patientId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentDate" label="预约日期" />
        <el-table-column prop="timeSlot" label="时间段" />
        <el-table-column prop="symptoms" label="症状描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="['warning', 'primary', 'success', 'info'][row.status]">
              {{ ['待确认', '已确认', '已完成', '已取消'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" text size="small" @click="handleConfirm(row.id)">确认</el-button>
              <el-button type="danger" text size="small" @click="handleCancel(row.id)">拒绝</el-button>
            </template>
            <el-button v-if="row.status === 1" type="primary" text size="small" @click="handleComplete(row.id)">完成</el-button>
            <el-button v-if="row.status === 2" type="primary" text size="small" @click="handleDiagnosis(row)">诊断</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAppointmentList, updateAppointmentStatus } from '../../api/appointment'
import { useUserStore } from '../../store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const tableData = ref([])
const statusFilter = ref(null)

const loadData = async () => {
  const params = { page: 1, size: 100, doctorId: userStore.user?.id }
  if (statusFilter.value !== null) params.status = statusFilter.value
  const res = await getAppointmentList(params)
  tableData.value = res.data.records
}

const handleConfirm = async (id) => {
  await updateAppointmentStatus(id, 1)
  ElMessage.success('已确认')
  loadData()
}

const handleCancel = async (id) => {
  await updateAppointmentStatus(id, 3)
  ElMessage.success('已拒绝')
  loadData()
}

const handleComplete = async (id) => {
  await updateAppointmentStatus(id, 2)
  ElMessage.success('已完成')
  loadData()
}

const handleDiagnosis = (row) => {
  router.push({ path: '/doctor/diagnosis', query: { appointmentId: row.id, patientId: row.patientId } })
}

onMounted(loadData)
</script>
