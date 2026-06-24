<template>
  <div>
    <el-card>
      <template #header>诊断记录</template>
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
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip />
        <el-table-column prop="prescription" label="处方" show-overflow-tooltip />
        <el-table-column prop="advice" label="医嘱" show-overflow-tooltip />
        <el-table-column prop="followUpDate" label="复诊日期" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="填写诊断" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="诊断结果">
          <el-input v-model="form.diagnosis" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="处方">
          <el-input v-model="form.prescription" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="医嘱">
          <el-input v-model="form.advice" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="复诊日期">
          <el-date-picker v-model="form.followUpDate" type="date" value-format="YYYY-MM-DD" />
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
import { useRoute } from 'vue-router'
import { createDiagnosis, getDiagnosisList } from '../../api/diagnosis'
import { useUserStore } from '../../store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const tableData = ref([])
const dialogVisible = ref(false)
const form = ref({ diagnosis: '', prescription: '', advice: '', followUpDate: '' })

const loadData = async () => {
  const res = await getDiagnosisList({ page: 1, size: 100, doctorId: userStore.user?.id })
  tableData.value = res.data.records
}

const handleSubmit = async () => {
  await createDiagnosis({
    ...form.value,
    appointmentId: route.query.appointmentId,
    patientId: route.query.patientId,
    doctorId: userStore.user?.id
  })
  ElMessage.success('诊断提交成功')
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
  if (route.query.appointmentId) {
    dialogVisible.value = true
  }
})
</script>
