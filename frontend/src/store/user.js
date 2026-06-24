import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '../api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  async function login(form) {
    const res = await loginApi(form)
    token.value = res.data.token
    user.value = res.data.user
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    return res.data
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function updateAvatar(url) {
    if (user.value) {
      user.value.avatar = url
      localStorage.setItem('user', JSON.stringify(user.value))
    }
  }

  const isAdmin = () => user.value?.role === 1
  const isDoctor = () => user.value?.role === 2
  const isUser = () => user.value?.role === 3

  return { token, user, login, logout, updateAvatar, isAdmin, isDoctor, isUser }
})
