<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>预约挂号</span>
          <el-button type="primary" @click="dialogVisible = true">新建预约</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="医生" width="150">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <el-avatar :size="28" :src="row.doctorAvatar">{{ row.doctorName ? row.doctorName.charAt(0) : '?' }}</el-avatar>
              <span>{{ row.doctorName || row.doctorId }}</span>
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
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="danger" text size="small" @click="handleCancel(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新建预约" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="医生ID">
          <el-input-number v-model="form.doctorId" :min="1" />
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker v-model="form.appointmentDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="时间段">
          <el-radio-group v-model="form.timeSlot">
            <el-radio value="上午">上午</el-radio>
            <el-radio value="下午">下午</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="症状描述">
          <el-input v-model="form.symptoms" type="textarea" :rows="3" placeholder="请描述您的症状" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { createAppointment, getAppointmentList, updateAppointmentStatus } from '../../api/appointment'
import { useUserStore } from '../../store/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const tableData = ref([])
const dialogVisible = ref(false)
const form = ref({ doctorId: null, appointmentDate: '', timeSlot: '上午', symptoms: '' })

const loadData = async () => {
  const res = await getAppointmentList({ page: 1, size: 100, patientId: userStore.user?.id })
  tableData.value = res.data.records
}

const handleSubmit = async () => {
  await createAppointment(form.value)
  ElMessage.success('预约提交成功')
  dialogVisible.value = false
  loadData()
}

const handleCancel = async (id) => {
  await ElMessageBox.confirm('确认取消预约？', '提示', { type: 'warning' })
  await updateAppointmentStatus(id, 3)
  ElMessage.success('已取消')
  loadData()
}

onMounted(loadData)
</script>
