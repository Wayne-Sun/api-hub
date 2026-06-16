<script setup lang="ts">
import { NIcon } from 'naive-ui'
import { ArchiveOutline } from '@vicons/ionicons5'

defineProps<{
  message: string
}>()
</script>

<template>
  <div style="display:flex; flex-direction:column; align-items:center; justify-content:center; padding:32px 0;">
    <n-icon size="64" color="#999">
      <ArchiveOutline />
    </n-icon>
    <p style="color:#999; margin-top:16px;">{{ message }}</p>
  </div>
</template>
