import request from '../utils/request'

export function addIndicator(data) {
  return request.post('/health-indicator', data)
}

export function getIndicatorList(params) {
  return request.get('/health-indicator/list', { params })
}

export function getIndicatorChart(params) {
  return request.get('/health-indicator/chart', { params })
}

export function getHealthTips() {
  return request.get('/health-indicator/tips')
}
