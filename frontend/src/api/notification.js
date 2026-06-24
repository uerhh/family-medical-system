import request from '../utils/request'

export function getNotificationList(params) {
  return request.get('/notification/list', { params })
}

export function markAsRead(id) {
  return request.put('/notification/' + id + '/read')
}
