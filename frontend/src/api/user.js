import request from '../utils/request'

export function getUserInfo() {
  return request.get('/user/info')
}

export function updateUserInfo(data) {
  return request.put('/user/info', data)
}

export function changePassword(data) {
  return request.put('/user/password', data)
}

export function getUserList(params) {
  return request.get('/admin/users', { params })
}

export function deleteUser(id) {
  return request.delete('/admin/users/' + id)
}

export function updateUser(id, data) {
  return request.put('/admin/users/' + id, data)
}

export function updateUserStatus(id, status) {
  return request.put('/admin/users/' + id + '/status', null, { params: { status } })
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
