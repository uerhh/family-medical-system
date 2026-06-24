import request from '../utils/request'

export function getMyRecord() {
  return request.get('/health-record')
}

export function getRecordByUserId(userId) {
  return request.get('/health-record/' + userId)
}

export function saveRecord(data) {
  return request.post('/health-record', data)
}
