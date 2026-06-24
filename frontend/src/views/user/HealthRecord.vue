<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>健康档案</span>
          <el-button type="primary" @click="handleEdit">{{ isEdit ? '保存' : '编辑' }}</el-button>
        </div>
      </template>
      <el-form :model="form" label-width="100px" :disabled="!isEdit">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血型">
              <el-select v-model="form.bloodType" placeholder="请选择">
                <el-option label="A型" value="A" />
                <el-option label="B型" value="B" />
                <el-option label="AB型" value="AB" />
                <el-option label="O型" value="O" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身高(cm)">
              <el-input-number v-model="form.height" :min="50" :max="250" :precision="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)">
              <el-input-number v-model="form.weight" :min="20" :max="300" :precision="1" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="过敏史">
              <el-input v-model="form.allergy" type="textarea" :rows="2" placeholder="请填写过敏史" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="既往病史">
              <el-input v-model="form.medicalHistory" type="textarea" :rows="3" placeholder="请填写既往病史" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="家族病史">
              <el-input v-model="form.familyHistory" type="textarea" :rows="3" placeholder="请填写家族病史" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyRecord, saveRecord } from '../../api/healthRecord'
import { ElMessage } from 'element-plus'

const isEdit = ref(false)
const form = ref({
  bloodType: '',
  height: null,
  weight: null,
  allergy: '',
  medicalHistory: '',
  familyHistory: ''
})

const loadData = async () => {
  const res = await getMyRecord()
  if (res.data) {
    form.value = { ...form.value, ...res.data }
  }
}

const handleEdit = async () => {
  if (isEdit.value) {
    await saveRecord(form.value)
    ElMessage.success('保存成功')
  }
  isEdit.value = !isEdit.value
}

onMounted(loadData)
</script>
