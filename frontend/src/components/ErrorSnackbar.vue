<script setup lang="ts">
defineProps<{
  show: boolean
  message: string
}>()

const emit = defineEmits<{
  close: []
}>()
</script>

<template>
  <v-snackbar
    :model-value="show"
    color="error"
    timeout="5000"
    @update:model-value="(val: boolean) => !val && emit('close')"
  >
    {{ message }}
    <template #actions>
      <v-btn variant="text" @click="emit('close')">关闭</v-btn>
    </template>
  </v-snackbar>
</template>
