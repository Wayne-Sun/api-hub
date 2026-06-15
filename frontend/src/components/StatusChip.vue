<script setup lang="ts">
defineProps<{
  enabled: boolean
}>()
</script>

<template>
  <v-chip
    :color="enabled ? 'green' : 'red'"
    :text-color="'white'"
    size="small"
  >
    {{ enabled ? '已启用' : '已禁用' }}
  </v-chip>
</template>
