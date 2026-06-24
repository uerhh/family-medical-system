import request from '../utils/request'

export function getOverview() {
  return request.get('/stats/overview')
}

export function getDoctorStats(doctorId) {
  return request.get('/stats/doctor', { params: { doctorId } })
}
