<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>用户管理</span>
          <el-input v-model="keyword" placeholder="搜索用户名/姓名/手机号" style="width:300px" clearable @clear="loadData" @keyup.enter="loadData">
            <template #append>
              <el-button @click="loadData">搜索</el-button>
            </template>
          </el-input>
        </div>
      </template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户名" width="180">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <el-avatar :size="28" :src="row.avatar">{{ row.realName ? row.realName.charAt(0) : row.username.charAt(0) }}</el-avatar>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">{{ ['', '男', '女'][row.gender] }}</template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="['', 'danger', 'success', ''][row.role]">{{ ['', '管理员', '医生', '用户'][row.role] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="v => handleStatusChange(row.id, v ? 1 : 0)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" text size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" background layout="total, prev, pager, next"
        :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, deleteUser, updateUserStatus } from '../../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const loadData = async () => {
  const res = await getUserList({ page: currentPage.value, size: pageSize.value, keyword: keyword.value })
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' })
  await deleteUser(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleStatusChange = async (id, status) => {
  await updateUserStatus(id, status)
  ElMessage.success('操作成功')
  loadData()
}

onMounted(loadData)
</script>
