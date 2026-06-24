import request from '../utils/request'

export function getUserGrowthTrend() {
  return request.get('/stats/user-growth')
}

export function getAppointmentTrend() {
  return request.get('/stats/appointment-trend')
}

export function getDepartmentDistribution() {
  return request.get('/stats/department-distribution')
}

export function getDoctorWorkload() {
  return request.get('/stats/doctor-workload')
}

export function getDoctorPersonalTrend(doctorId) {
  return request.get('/stats/doctor-personal', { params: { doctorId } })
}

export function getPatientDemographics(doctorId) {
  return request.get('/stats/patient-demographics', { params: { doctorId } })
}

export function getDiagnosisCategories(doctorId) {
  return request.get('/stats/diagnosis-categories', { params: { doctorId } })
}
