<template>
  <div v-loading="loading">
    <div class="page-header">
      <h2>问诊详情</h2>
      <el-button @click="router.back()">返回</el-button>
    </div>

    <el-card v-if="detail" style="margin-bottom: 20px;">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <span>{{ detail.title }}</span>
          <el-tag :type="statusType(detail.status)">{{ statusText(detail.status) }}</el-tag>
        </div>
      </template>
      <p><strong>患者：</strong>{{ detail.patientName }}</p>
      <p><strong>症状描述：</strong>{{ detail.symptoms }}</p>
      <div v-if="detail.images" class="consultation-images">
        <el-image v-for="(img, idx) in parseImages(detail.images)" :key="idx"
          :src="img" :preview-src-list="parseImages(detail.images)" :initial-index="idx"
          fit="cover" style="width: 100px; height: 100px; border-radius: 4px; margin-right: 8px; margin-bottom: 8px;" />
      </div>
      <p><strong>创建时间：</strong>{{ detail.createTime }}</p>
    </el-card>

    <el-card style="margin-bottom: 20px;">
      <template #header><span>对话记录</span></template>
      <div class="message-list">
        <div v-for="reply in detail?.replies" :key="reply.id"
             :class="['message-item', reply.senderId === userId ? 'self' : 'other']">
          <el-avatar :src="reply.senderAvatar" :size="36">
            {{ reply.senderName?.charAt(0) }}
          </el-avatar>
          <div class="message-content">
            <div class="message-sender">{{ reply.senderName }}
              <el-tag size="small" :type="reply.senderRole === 2 ? 'success' : 'primary'" style="margin-left:6px;">
                {{ reply.senderRole === 2 ? '医生' : '患者' }}
              </el-tag>
            </div>
            <div class="message-text">{{ reply.content }}</div>
            <div v-if="reply.images" class="reply-images">
              <el-image v-for="(img, idx) in parseImages(reply.images)" :key="idx"
                :src="img" :preview-src-list="parseImages(reply.images)" :initial-index="idx"
                fit="cover" style="width: 80px; height: 80px; border-radius: 4px; margin-right: 6px; margin-top: 6px;" />
            </div>
            <div class="message-time">{{ reply.createTime }}</div>
          </div>
        </div>
        <el-empty v-if="!detail?.replies?.length" description="暂无回复" />
      </div>
    </el-card>

    <el-card v-if="detail && detail.status !== 2">
      <template #header><span>医生回复</span></template>
      <el-input v-model="replyContent" type="textarea" :rows="3" placeholder="输入回复内容..." />
      <div style="margin-top: 8px;">
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
      </div>
      <div style="margin-top: 12px; text-align: right;">
        <el-button type="warning" @click="handleClose">关闭问诊</el-button>
        <el-button type="primary" @click="handleReply">发送回复</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getConsultationDetail, replyConsultation, closeConsultation } from '../../api/consultation'
import { uploadImage } from '../../api/file'
import { useUserStore } from '../../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userId = userStore.user?.id
const detail = ref(null)
const loading = ref(false)
const replyContent = ref('')
const imageFileList = ref([])
const uploadedImages = ref([])

const statusText = (s) => ['待回复', '已回复', '已关闭'][s] || '未知'
const statusType = (s) => ['warning', 'success', 'info'][s] || 'info'

const parseImages = (imagesStr) => {
  if (!imagesStr) return []
  try { return JSON.parse(imagesStr) } catch { return [] }
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getConsultationDetail(route.params.id)
    detail.value = res.data
  } finally {
    loading.value = false
  }
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

const handleReply = async () => {
  if (!replyContent.value.trim()) return ElMessage.warning('请输入回复内容')
  try {
    await replyConsultation(route.params.id, {
      content: replyContent.value,
      images: uploadedImages.value.length > 0 ? JSON.stringify(uploadedImages.value) : null
    })
    ElMessage.success('回复成功')
    replyContent.value = ''
    imageFileList.value = []
    uploadedImages.value = []
    loadDetail()
  } catch (e) {
    console.error(e)
  }
}

const handleClose = async () => {
  try {
    await closeConsultation(route.params.id)
    ElMessage.success('已关闭')
    loadDetail()
  } catch (e) {
    console.error(e)
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 { margin: 0; font-size: 20px; }
.message-list { max-height: 500px; overflow-y: auto; }
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.message-item.self { flex-direction: row-reverse; }
.message-item.self .message-content { text-align: right; }
.message-content { flex: 1; }
.message-sender { font-size: 13px; color: #606266; margin-bottom: 4px; }
.message-text {
  background: #f4f4f5;
  padding: 8px 12px;
  border-radius: 8px;
  display: inline-block;
  max-width: 80%;
}
.message-item.self .message-text { background: #ecf5ff; }
.message-time { font-size: 12px; color: #909399; margin-top: 4px; }
.consultation-images { margin: 8px 0; }
.reply-images { margin-top: 6px; }
</style>
