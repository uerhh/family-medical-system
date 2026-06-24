<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>个人信息</template>
          <div style="text-align:center;margin-bottom:20px">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
              accept="image/jpeg,image/png,image/webp"
            >
              <el-avatar :size="100" :src="avatarUrl" style="cursor:pointer">
                {{ userStore.user?.realName ? userStore.user.realName.charAt(0) : 'D' }}
              </el-avatar>
              <div style="margin-top:8px;font-size:12px;color:#909399">点击更换头像</div>
            </el-upload>
          </div>
          <el-form :model="userForm" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="userForm.realName" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="userForm.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input-number v-model="userForm.age" :min="1" :max="150" />
            </el-form-item>
            <el-form-item label="身份证号">
              <el-input v-model="userForm.idCard" />
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="userForm.address" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveInfo" :loading="saving">保存信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>修改密码</template>
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="90px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" @click="handleChangePwd" :loading="changingPwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top:20px">
          <template #header>账号信息</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户ID">{{ userStore.user?.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userStore.user?.username }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag type="success">医生</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ userStore.user?.createTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo, updateUserInfo, changePassword, uploadAvatar } from '../../api/user'
import { useUserStore } from '../../store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const avatarUrl = ref(userStore.user?.avatar || '')
const saving = ref(false)
const changingPwd = ref(false)
const pwdFormRef = ref()

const userForm = ref({
  username: '', realName: '', phone: '', email: '', gender: 0, age: null, idCard: '', address: ''
})

const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== pwdForm.value.newPassword) callback(new Error('两次密码不一致'))
      else callback()
    }, trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  const res = await getUserInfo()
  Object.assign(userForm.value, res.data)
}

const beforeAvatarUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) { ElMessage.error('仅支持 jpg、png、webp 格式'); return false }
  if (!isLt2M) { ElMessage.error('图片大小不能超过 2MB'); return false }
  return true
}

const handleAvatarUpload = async (options) => {
  try {
    const res = await uploadAvatar(options.file)
    const url = res.data
    userStore.updateAvatar(url)
    avatarUrl.value = url + '?t=' + Date.now()
    userForm.value.avatar = url
    ElMessage.success('头像更新成功')
    options.onSuccess(url)
  } catch (e) {
    ElMessage.error('头像上传失败：' + (e.message || '未知错误'))
    options.onError(e)
  }
}

const handleSaveInfo = async () => {
  saving.value = true
  try {
    await updateUserInfo(userForm.value)
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

const handleChangePwd = async () => {
  await pwdFormRef.value.validate()
  changingPwd.value = true
  try {
    await changePassword({ oldPassword: pwdForm.value.oldPassword, newPassword: pwdForm.value.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } finally {
    changingPwd.value = false
  }
}

onMounted(loadUserInfo)
</script>
