<script setup lang="ts">
withDefaults(defineProps<{
  message: string
  icon?: string
}>(), {
  icon: 'mdi-inbox-outline',
})
</script>

<template>
  <div class="d-flex flex-column align-center justify-center py-8">
    <v-icon :icon="icon" size="64" color="grey-lighten-1" />
    <p class="text-grey mt-4">{{ message }}</p>
  </div>
</template>
