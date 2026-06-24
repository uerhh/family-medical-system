<template>
  <div>
    <div class="page-header">
      <h2>在线问诊</h2>
      <el-button type="primary" @click="showCreateDialog = true">发起问诊</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe @row-click="goDetail">
      <el-table-column prop="title" label="咨询标题" />
      <el-table-column label="医生" width="120">
        <template #default="{ row }">
          {{ row.doctorName || '待分配' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </el-table>

    <el-dialog v-model="showCreateDialog" title="发起问诊" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请简要描述您的问题" />
        </el-form-item>
        <el-form-item label="症状">
          <el-input v-model="form.symptoms" type="textarea" :rows="4" placeholder="请详细描述您的症状" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            :action="''"
            list-type="picture-card"
            :limit="5"
            :on-exceed="() => ElMessage.warning('最多上传5张图片')"
            :before-upload="beforeImageUpload"
            :http-request="handleImageUpload"
            :file-list="imageFileList"
            :on-remove="handleImageRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">支持 jpg/png/webp，单张不超过 5MB，最多 5 张</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createConsultation, getConsultationList } from '../../api/consultation'
import { uploadImage } from '../../api/file'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const list = ref([])
const loading = ref(false)
const showCreateDialog = ref(false)
const form = ref({ title: '', symptoms: '' })
const imageFileList = ref([])
const uploadedImages = ref([])

const statusText = (s) => ['待回复', '已回复', '已关闭'][s] || '未知'
const statusType = (s) => ['warning', 'success', 'info'][s] || 'info'

const loadData = async () => {
  loading.value = true
  try {
    const res = await getConsultationList({ page: 1, size: 100, patientId: userStore.user.id })
    list.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

const goDetail = (row) => {
  router.push('/user/consultation/' + row.id)
}

const beforeImageUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) ElMessage.error('仅支持 jpg/png/webp 格式')
  if (!isLt5M) ElMessage.error('图片大小不能超过 5MB')
  return isImage && isLt5M
}

const handleImageUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    uploadedImages.value.push(res.data)
    options.onSuccess(res.data)
  } catch (e) {
    options.onError(e)
    ElMessage.error('图片上传失败')
  }
}

const handleImageRemove = (file) => {
  const idx = imageFileList.value.indexOf(file)
  if (idx !== -1) uploadedImages.value.splice(idx, 1)
}

const handleCreate = async () => {
  if (!form.value.title) return ElMessage.warning('请输入标题')
  if (!form.value.symptoms) return ElMessage.warning('请描述症状')
  try {
    await createConsultation({
      ...form.value,
      images: uploadedImages.value.length > 0 ? JSON.stringify(uploadedImages.value) : null
    })
    ElMessage.success('提交成功')
    showCreateDialog.value = false
    form.value = { title: '', symptoms: '' }
    imageFileList.value = []
    uploadedImages.value = []
    loadData()
  } catch (e) {
    console.error(e)
  }
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
.upload-tip { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
