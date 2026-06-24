import request from '../utils/request'

export function getChatHistory(params) {
  return request.get('/chat/history', { params })
}

export function getUnreadMessages() {
  return request.get('/chat/unread')
}

export function markChatAsRead(senderId) {
  return request.put('/chat/read/' + senderId)
}
