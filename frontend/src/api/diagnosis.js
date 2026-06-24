import request from '../utils/request'

export function createDiagnosis(data) {
  return request.post('/diagnosis', data)
}

export function getDiagnosisList(params) {
  return request.get('/diagnosis/list', { params })
}

export function getDiagnosisById(id) {
  return request.get('/diagnosis/' + id)
}
