import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', () => {
  const drawerOpen = ref(true)
  const snackbar = ref<{ show: boolean; message: string; color: string }>({
    show: false,
    message: '',
    color: 'info',
  })

  function toggleDrawer() {
    drawerOpen.value = !drawerOpen.value
  }

  function showSnackbar(message: string, color = 'info') {
    snackbar.value = { show: true, message, color }
  }

  function hideSnackbar() {
    snackbar.value = { ...snackbar.value, show: false }
  }

  return { drawerOpen, snackbar, toggleDrawer, showSnackbar, hideSnackbar }
})
