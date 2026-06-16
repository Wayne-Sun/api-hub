<script setup lang="ts">
import { watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useAppStore } from '@/stores/app'
import { useMessage } from 'naive-ui'

const appStore = useAppStore()
const message = useMessage()
watch(
  () => appStore.snackbar,
  (val) => {
    if (val?.show) {
      ;(message as any)[val.color]?.(val.message) ||
        message.info(val.message)
      appStore.hideSnackbar()
    }
  },
  { deep: true },
)
</script>

<template>
  <slot />
</template>
