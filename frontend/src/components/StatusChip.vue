<script setup lang="ts">
import { NTag } from 'naive-ui'

defineProps<{
  enabled: boolean
}>()
</script>

<template>
  <n-tag
    :type="enabled ? 'success' : 'error'"
    size="small"
  >
    {{ enabled ? '已启用' : '已禁用' }}
  </n-tag>
</template>
