<template>
  <div>
    <el-card>
      <template #header>诊断记录</template>
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
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip />
        <el-table-column prop="prescription" label="处方" show-overflow-tooltip />
        <el-table-column prop="advice" label="医嘱" show-overflow-tooltip />
        <el-table-column prop="followUpDate" label="复诊日期" />
        <el-table-column prop="createTime" label="诊断时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDiagnosisList } from '../../api/diagnosis'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const tableData = ref([])

const loadData = async () => {
  const res = await getDiagnosisList({ page: 1, size: 100, patientId: userStore.user?.id })
  tableData.value = res.data.records
}

onMounted(loadData)
</script>
