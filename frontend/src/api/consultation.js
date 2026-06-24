import request from '../utils/request'

export function createConsultation(data) {
  return request.post('/consultation', data)
}

export function replyConsultation(id, data) {
  return request.post('/consultation/' + id + '/reply', data)
}

export function closeConsultation(id) {
  return request.put('/consultation/' + id + '/close')
}

export function getConsultationList(params) {
  return request.get('/consultation/list', { params })
}

export function getConsultationDetail(id) {
  return request.get('/consultation/' + id)
}
