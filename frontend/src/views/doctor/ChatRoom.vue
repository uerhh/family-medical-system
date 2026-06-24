<template>
  <div class="chat-container">
    <div class="chat-sidebar">
      <div class="sidebar-title">联系人</div>
      <div v-for="contact in contacts" :key="contact.id"
           :class="['contact-item', selectedContact?.id === contact.id ? 'active' : '']"
           @click="selectContact(contact)">
        <el-avatar :src="contact.avatar" :size="36">{{ contact.name?.charAt(0) }}</el-avatar>
        <div class="contact-info">
          <div class="contact-name">{{ contact.name }}</div>
          <div class="contact-last">{{ contact.lastMessage }}</div>
        </div>
        <el-badge v-if="contact.unread" :value="contact.unread" :max="99" />
      </div>
      <el-empty v-if="!contacts.length" description="暂无联系人" :image-size="60" />
    </div>

    <div class="chat-main">
      <template v-if="selectedContact">
        <div class="chat-header">{{ selectedContact.name }}</div>
        <div class="chat-messages" ref="messagesRef">
          <div v-for="msg in messages" :key="msg.id"
               :class="['msg-item', msg.senderId === userId ? 'self' : 'other']">
            <el-avatar :src="msg.senderAvatar" :size="32">{{ msg.senderName?.charAt(0) }}</el-avatar>
            <div class="msg-bubble">{{ msg.content }}</div>
            <span class="msg-time">{{ msg.createTime?.substring(11, 16) }}</span>
          </div>
        </div>
        <div class="chat-input">
          <el-input v-model="inputMsg" placeholder="输入消息..." @keyup.enter="sendMsg" />
          <el-button type="primary" @click="sendMsg">发送</el-button>
        </div>
      </template>
      <el-empty v-else description="请选择联系人开始聊天" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getChatHistory, getUnreadMessages, markChatAsRead } from '../../api/chat'
import { connectWs, sendWsMessage, disconnectWs } from '../../utils/websocket'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const userId = userStore.user?.id
const token = userStore.token
const contacts = ref([])
const selectedContact = ref(null)
const messages = ref([])
const inputMsg = ref('')
const messagesRef = ref(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const loadContacts = async () => {
  try {
    const unreadRes = await getUnreadMessages()
    const unreadMsgs = unreadRes.data || []
    const contactMap = new Map()
    for (const msg of unreadMsgs) {
      if (!contactMap.has(msg.senderId)) {
        contactMap.set(msg.senderId, {
          id: msg.senderId,
          name: msg.senderName,
          avatar: msg.senderAvatar,
          lastMessage: msg.content,
          unread: 0
        })
      }
      contactMap.get(msg.senderId).unread++
    }
    contacts.value = Array.from(contactMap.values())
  } catch (e) {
    console.error(e)
  }
}

const selectContact = async (contact) => {
  selectedContact.value = contact
  try {
    const res = await getChatHistory({ otherUserId: contact.id, limit: 100 })
    messages.value = (res.data || []).reverse()
    scrollToBottom()
    await markChatAsRead(contact.id)
    contact.unread = 0
  } catch (e) {
    console.error(e)
  }
}

const sendMsg = () => {
  if (!inputMsg.value.trim() || !selectedContact.value) return
  const success = sendWsMessage({
    receiverId: selectedContact.value.id,
    content: inputMsg.value,
    msgType: 0
  })
  if (success) {
    inputMsg.value = ''
  } else {
    ElMessage.error('发送失败，请检查连接')
  }
}

const onWsMessage = (data) => {
  if (selectedContact.value && (data.senderId === selectedContact.value.id || data.receiverId === selectedContact.value.id)) {
    messages.value.push(data)
    scrollToBottom()
    if (data.senderId === selectedContact.value.id) {
      markChatAsRead(data.senderId)
    }
  } else if (data.senderId !== userId) {
    let contact = contacts.value.find(c => c.id === data.senderId)
    if (!contact) {
      contact = { id: data.senderId, name: data.senderName, avatar: data.senderAvatar, lastMessage: '', unread: 0 }
      contacts.value.unshift(contact)
    }
    contact.lastMessage = data.content
    contact.unread = (contact.unread || 0) + 1
  }
}

onMounted(() => {
  loadContacts()
  connectWs(token, onWsMessage)
})

onBeforeUnmount(() => {
  disconnectWs()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 120px);
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}
.chat-sidebar {
  width: 260px;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
}
.sidebar-title {
  padding: 16px;
  font-weight: bold;
  border-bottom: 1px solid #e4e7ed;
}
.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}
.contact-item:hover, .contact-item.active { background: #f5f7fa; }
.contact-info { flex: 1; overflow: hidden; }
.contact-name { font-size: 14px; }
.contact-last { font-size: 12px; color: #909399; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.chat-main { flex: 1; display: flex; flex-direction: column; }
.chat-header {
  padding: 12px 16px;
  font-weight: bold;
  border-bottom: 1px solid #e4e7ed;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}
.msg-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 12px;
}
.msg-item.self { flex-direction: row-reverse; }
.msg-bubble {
  background: #f4f4f5;
  padding: 8px 12px;
  border-radius: 8px;
  max-width: 60%;
  word-break: break-word;
}
.msg-item.self .msg-bubble { background: #ecf5ff; }
.msg-time { font-size: 11px; color: #909399; align-self: flex-end; }
.chat-input {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #e4e7ed;
}
</style>
