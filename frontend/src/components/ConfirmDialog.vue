<script setup lang="ts">
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
  <v-dialog :model-value="show" max-width="400">
    <v-card>
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text>{{ message }}</v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="emit('cancel')">取消</v-btn>
        <v-btn color="primary" variant="text" @click="emit('confirm')">
          {{ confirmText || '确认' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
