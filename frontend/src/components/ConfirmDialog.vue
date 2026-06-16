<script setup lang="ts">
import { NModal, NCard, NButton } from 'naive-ui'

defineProps<{
  show: boolean
  title: string
  message: string
  confirmText?: string
}>()

const emit = defineEmits<{
  confirm: []
  cancel: []
}>()
</script>

<template>
  <n-modal
    :show="show"
    preset="card"
    :style="{ maxWidth: '400px' }"
    @update:show="(v) => !v && emit('cancel')"
  >
    <n-card :title="title">
      {{ message }}
      <template #footer>
        <div style="display: flex; justify-content: flex-end; gap: 8px">
          <n-button @click="emit('cancel')">取消</n-button>
          <n-button type="primary" @click="emit('confirm')">
            {{ confirmText || '确认' }}
          </n-button>
        </div>
      </template>
    </n-card>
  </n-modal>
</template>
