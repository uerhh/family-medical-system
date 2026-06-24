<template>
  <div>
    <el-card>
      <template #header>消息通知</template>
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="['', 'info', 'warning', 'success'][row.type]">
              {{ ['', '系统通知', '预约提醒', '诊断结果'][row.type] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 0 ? 'danger' : 'success'">{{ row.isRead === 0 ? '未读' : '已读' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-if="row.isRead === 0" type="primary" text size="small" @click="handleRead(row.id)">标为已读</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNotificationList, markAsRead } from '../../api/notification'
import { useUserStore } from '../../store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const tableData = ref([])

const loadData = async () => {
  const res = await getNotificationList({ page: 1, size: 100 })
  tableData.value = res.data.records
}

const handleRead = async (id) => {
  await markAsRead(id)
  ElMessage.success('已标为已读')
  loadData()
}

onMounted(loadData)
</script>
