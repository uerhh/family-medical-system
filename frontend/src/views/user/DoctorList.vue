<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>选择医生</span>
          <el-input v-model="keyword" placeholder="搜索医生姓名" style="width:250px" clearable @clear="loadData" @keyup.enter="loadData">
            <template #append><el-button @click="loadData">搜索</el-button></template>
          </el-input>
        </div>
      </template>

      <el-row :gutter="16">
        <el-col :span="8" v-for="doc in doctors" :key="doc.id" style="margin-bottom:16px">
          <el-card shadow="hover" class="doctor-card" @click="openDetail(doc)">
            <div class="doctor-info">
              <el-avatar :size="64" :src="doc.avatar" style="background:#409eff">
                {{ doc.realName ? doc.realName.charAt(0) : '医' }}
              </el-avatar>
              <div class="doctor-meta">
                <div class="doctor-name">{{ doc.realName }}
                  <el-tag size="small" type="success" v-if="doc.title">{{ doc.title }}</el-tag>
                </div>
                <div class="doctor-dept" v-if="doc.department">
                  <el-icon><OfficeBuilding /></el-icon> {{ doc.department }}
                </div>
                <div class="doctor-years" v-if="doc.workYears">
                  <el-icon><Timer /></el-icon> 从业{{ doc.workYears }}年
                </div>
                <div class="doctor-fee" v-if="doc.consultFee">
                  问诊费 <span class="fee">¥{{ doc.consultFee }}</span>
                </div>
              </div>
            </div>
            <div class="doctor-specialty" v-if="doc.specialty">
              擅长：{{ doc.specialty }}
            </div>
            <div style="margin-top:12px;text-align:right">
              <el-button type="primary" size="small" @click.stop="openAppointment(doc)">立即预约</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="doctors.length === 0 && !loading" description="暂无医生数据" />
    </el-card>

    <!-- 医生详情对话框 -->
    <el-dialog v-model="detailVisible" title="医生详情" width="500px">
      <el-descriptions :column="2" border v-if="selectedDoctor">
        <el-descriptions-item label="姓名" :span="2">{{ selectedDoctor.realName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ selectedDoctor.department || '-' }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ selectedDoctor.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作年限">{{ selectedDoctor.workYears ? selectedDoctor.workYears + '年' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="问诊费">{{ selectedDoctor.consultFee ? '¥' + selectedDoctor.consultFee : '-' }}</el-descriptions-item>
        <el-descriptions-item label="擅长领域" :span="2">{{ selectedDoctor.specialty || '-' }}</el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="2">{{ selectedDoctor.introduction || '暂无简介' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="detailVisible = false; openAppointment(selectedDoctor)">预约该医生</el-button>
      </template>
    </el-dialog>

    <!-- 预约对话框 -->
    <el-dialog v-model="appointmentVisible" title="预约挂号" width="500px">
      <div v-if="appointmentDoctor" style="margin-bottom:16px;padding:12px;background:#f0f9eb;border-radius:8px">
        预约医生：<strong>{{ appointmentDoctor.realName }}</strong>
        <el-tag size="small" type="success" style="margin-left:8px">{{ appointmentDoctor.title }}</el-tag>
        <span style="margin-left:8px;color:#909399">{{ appointmentDoctor.department }}</span>
      </div>
      <el-form :model="appointmentForm" label-width="80px">
        <el-form-item label="预约日期">
          <el-date-picker v-model="appointmentForm.appointmentDate" type="date" value-format="YYYY-MM-DD"
            :disabled-date="d => d < new Date(new Date().setHours(0,0,0,0))" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="时间段">
          <el-radio-group v-model="appointmentForm.timeSlot">
            <el-radio-button value="上午">上午</el-radio-button>
            <el-radio-button value="下午">下午</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="症状描述">
          <el-input v-model="appointmentForm.symptoms" type="textarea" :rows="4" placeholder="请详细描述您的症状，便于医生提前了解" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="appointmentVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppointment" :loading="submitting">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDoctorList } from '../../api/doctor'
import { createAppointment } from '../../api/appointment'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, Timer } from '@element-plus/icons-vue'

const doctors = ref([])
const loading = ref(false)
const keyword = ref('')
const detailVisible = ref(false)
const selectedDoctor = ref(null)
const appointmentVisible = ref(false)
const appointmentDoctor = ref(null)
const submitting = ref(false)

const appointmentForm = ref({ appointmentDate: '', timeSlot: '上午', symptoms: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDoctorList({ page: 1, size: 100, keyword: keyword.value })
    doctors.value = res.data.records
  } finally {
    loading.value = false
  }
}

const openDetail = (doc) => {
  selectedDoctor.value = doc
  detailVisible.value = true
}

const openAppointment = (doc) => {
  appointmentDoctor.value = doc
  appointmentForm.value = { appointmentDate: '', timeSlot: '上午', symptoms: '' }
  appointmentVisible.value = true
}

const submitAppointment = async () => {
  if (!appointmentForm.value.appointmentDate) {
    ElMessage.warning('请选择预约日期')
    return
  }
  submitting.value = true
  try {
    await createAppointment({
      doctorId: appointmentDoctor.value.id,
      ...appointmentForm.value
    })
    ElMessage.success('预约提交成功，请等待医生确认')
    appointmentVisible.value = false
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.doctor-card { cursor: pointer; transition: transform 0.2s; }
.doctor-card:hover { transform: translateY(-2px); }
.doctor-info { display: flex; gap: 16px; }
.doctor-meta { flex: 1; }
.doctor-name { font-size: 16px; font-weight: bold; color: #303133; display: flex; align-items: center; gap: 8px; }
.doctor-dept, .doctor-years { font-size: 13px; color: #606266; margin-top: 4px; display: flex; align-items: center; gap: 4px; }
.doctor-fee { font-size: 13px; color: #909399; margin-top: 4px; }
.fee { color: #f56c6c; font-weight: bold; font-size: 16px; }
.doctor-specialty { font-size: 13px; color: #909399; margin-top: 12px; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
</style>
