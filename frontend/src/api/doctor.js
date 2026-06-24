import request from '../utils/request'

export function getDoctorList(params) {
  return request.get('/doctor/list', { params })
}

export function getDoctorDetail(userId) {
  return request.get('/doctor/detail/' + userId)
}

export function getMyDoctorInfo() {
  return request.get('/doctor/my-info')
}

export function updateMyDoctorInfo(data) {
  return request.put('/doctor/my-info', data)
}

export function addDoctor(data) {
  return request.post('/doctor/add', data)
}

export function updateDoctorInfo(userId, data) {
  return request.put('/doctor/info/' + userId, data)
}
