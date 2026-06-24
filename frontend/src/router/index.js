import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, role: 1 },
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('../views/admin/UserManage.vue') },
      { path: 'doctors', name: 'AdminDoctors', component: () => import('../views/admin/DoctorManage.vue') },
      { path: 'appointments', name: 'AdminAppointments', component: () => import('../views/admin/AppointmentManage.vue') },
      { path: 'profile', name: 'AdminProfile', component: () => import('../views/admin/Profile.vue') },
      { path: 'analytics', name: 'AdminAnalytics', component: () => import('../views/admin/Analytics.vue') }
    ]
  },
  {
    path: '/doctor',
    component: () => import('../views/doctor/Layout.vue'),
    redirect: '/doctor/dashboard',
    meta: { requiresAuth: true, role: 2 },
    children: [
      { path: 'dashboard', name: 'DoctorDashboard', component: () => import('../views/doctor/Dashboard.vue') },
      { path: 'appointments', name: 'DoctorAppointments', component: () => import('../views/doctor/Appointments.vue') },
      { path: 'diagnosis', name: 'DoctorDiagnosis', component: () => import('../views/doctor/Diagnosis.vue') },
      { path: 'schedule', name: 'DoctorSchedule', component: () => import('../views/doctor/Schedule.vue') },
      { path: 'profile', name: 'DoctorProfile', component: () => import('../views/doctor/Profile.vue') },
      { path: 'consultations', name: 'DoctorConsultations', component: () => import('../views/doctor/ConsultationList.vue') },
      { path: 'consultation/:id', name: 'DoctorConsultationDetail', component: () => import('../views/doctor/ConsultationDetail.vue') },
      { path: 'chat', name: 'DoctorChat', component: () => import('../views/doctor/ChatRoom.vue') },
      { path: 'analytics', name: 'DoctorAnalytics', component: () => import('../views/doctor/Analytics.vue') }
    ]
  },
  {
    path: '/user',
    component: () => import('../views/user/Layout.vue'),
    redirect: '/user/dashboard',
    meta: { requiresAuth: true, role: 3 },
    children: [
      { path: 'dashboard', name: 'UserDashboard', component: () => import('../views/user/Dashboard.vue') },
      { path: 'doctors', name: 'DoctorList', component: () => import('../views/user/DoctorList.vue') },
      { path: 'health-record', name: 'HealthRecord', component: () => import('../views/user/HealthRecord.vue') },
      { path: 'appointment', name: 'UserAppointment', component: () => import('../views/user/Appointment.vue') },
      { path: 'diagnosis', name: 'UserDiagnosis', component: () => import('../views/user/DiagnosisList.vue') },
      { path: 'notifications', name: 'Notifications', component: () => import('../views/user/Notifications.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/user/Profile.vue') },
      { path: 'health-indicator', name: 'HealthIndicator', component: () => import('../views/user/HealthIndicator.vue') },
      { path: 'consultation', name: 'UserConsultation', component: () => import('../views/user/Consultation.vue') },
      { path: 'consultation/:id', name: 'UserConsultationDetail', component: () => import('../views/user/ConsultationDetail.vue') },
      { path: 'chat', name: 'UserChat', component: () => import('../views/user/ChatRoom.vue') }
    ]
  },
  { path: '/', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
    } else {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (to.meta.role && user.role !== to.meta.role) {
        const roleMap = { 1: '/admin', 2: '/doctor', 3: '/user' }
        next(roleMap[user.role] || '/login')
      } else {
        next()
      }
    }
  } else {
    next()
  }
})

export default router
