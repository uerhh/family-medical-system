import request from '../utils/request'

export function createAppointment(data) {
  return request.post('/appointment', data)
}

export function getAppointmentList(params) {
  return request.get('/appointment/list', { params })
}

export function updateAppointmentStatus(id, status) {
  return request.put('/appointment/' + id + '/status', null, { params: { status } })
}
