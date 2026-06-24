<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>医生管理</span>
          <el-button type="primary" @click="openAddDialog">添加医生</el-button>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="姓名" width="150">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <el-avatar :size="28" :src="row.avatar">{{ row.realName ? row.realName.charAt(0) : '?' }}</el-avatar>
              <span>{{ row.realName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="department" label="科室" width="120" />
        <el-table-column prop="title" label="职称" width="120" />
        <el-table-column prop="specialty" label="擅长" show-overflow-tooltip />
        <el-table-column prop="workYears" label="工作年限" width="90" />
        <el-table-column prop="consultFee" label="问诊费" width="90">
          <template #default="{ row }">{{ row.consultFee ? '¥' + row.consultFee : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加医生对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加医生" width="600px" destroy-on-close>
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="addForm.username" placeholder="登录用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="addForm.password" type="password" placeholder="登录密码" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="addForm.realName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="addForm.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="addForm.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科室" prop="department">
              <el-select v-model="addForm.department" placeholder="选择科室">
                <el-option v-for="d in departments" :key="d" :label="d" :value="d" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称">
              <el-select v-model="addForm.title" placeholder="选择职称">
                <el-option label="主任医师" value="主任医师" />
                <el-option label="副主任医师" value="副主任医师" />
                <el-option label="主治医师" value="主治医师" />
                <el-option label="住院医师" value="住院医师" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作年限">
              <el-input-number v-model="addForm.workYears" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="问诊费(元)">
              <el-input-number v-model="addForm.consultFee" :min="0" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="擅长领域">
              <el-input v-model="addForm.specialty" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个人简介">
              <el-input v-model="addForm.introduction" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd" :loading="submitting">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- 编辑医生对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑医生信息" width="600px" destroy-on-close>
      <el-form :model="editForm" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="科室">
              <el-select v-model="editForm.department" placeholder="选择科室">
                <el-option v-for="d in departments" :key="d" :label="d" :value="d" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称">
              <el-select v-model="editForm.title" placeholder="选择职称">
                <el-option label="主任医师" value="主任医师" />
                <el-option label="副主任医师" value="副主任医师" />
                <el-option label="主治医师" value="主治医师" />
                <el-option label="住院医师" value="住院医师" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作年限">
              <el-input-number v-model="editForm.workYears" :min="0" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="问诊费(元)">
              <el-input-number v-model="editForm.consultFee" :min="0" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="擅长领域">
              <el-input v-model="editForm.specialty" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个人简介">
              <el-input v-model="editForm.introduction" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDoctorList, addDoctor, updateDoctorInfo } from '../../api/doctor'
import { deleteUser } from '../../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const departments = ['内科', '外科', '儿科', '妇产科', '骨科', '皮肤科', '眼科', '耳鼻喉科', '口腔科', '中医科', '急诊科', '全科']

const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const addFormRef = ref()
const editingUserId = ref(null)

const addForm = ref({
  username: '', password: '', realName: '', phone: '', gender: 1,
  department: '', title: '', specialty: '', introduction: '', consultFee: 0, workYears: 0
})

const editForm = ref({
  department: '', title: '', specialty: '', introduction: '', consultFee: 0, workYears: 0
})

const addRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  department: [{ required: true, message: '请选择科室', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDoctorList({ page: 1, size: 100 })
    tableData.value = res.data.records
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  addForm.value = { username: '', password: '', realName: '', phone: '', gender: 1, department: '', title: '', specialty: '', introduction: '', consultFee: 0, workYears: 0 }
  addDialogVisible.value = true
}

const openEditDialog = (row) => {
  editingUserId.value = row.id
  editForm.value = {
    department: row.department || '',
    title: row.title || '',
    specialty: row.specialty || '',
    introduction: row.introduction || '',
    consultFee: row.consultFee || 0,
    workYears: row.workYears || 0
  }
  editDialogVisible.value = true
}

const handleAdd = async () => {
  await addFormRef.value.validate()
  submitting.value = true
  try {
    await addDoctor(addForm.value)
    ElMessage.success('医生添加成功')
    addDialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleEdit = async () => {
  submitting.value = true
  try {
    await updateDoctorInfo(editingUserId.value, editForm.value)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该医生？', '提示', { type: 'warning' })
  await deleteUser(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
