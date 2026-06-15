<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
const { drawerOpen, snackbar } = storeToRefs(appStore)
const { toggleDrawer, hideSnackbar } = appStore
</script>

<template>
  <v-app>
    <v-navigation-drawer v-model="drawerOpen" app>
      <v-list-item prepend-icon="mdi-view-dashboard" title="API Hub" />
      <v-divider />
      <v-list nav>
        <v-list-item
          prepend-icon="mdi-database"
          title="数据源管理"
          :to="{ name: 'datasource' }"
        />
        <v-list-item
          prepend-icon="mdi-api"
          title="API 管理"
          :to="{ name: 'dataapi' }"
        />
      </v-list>
    </v-navigation-drawer>

    <v-app-bar app>
      <v-app-bar-nav-icon @click="toggleDrawer" />
      <v-app-bar-title>API Hub 管理平台</v-app-bar-title>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <router-view />
      </v-container>
    </v-main>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.message }}
      <template #actions>
        <v-btn variant="text" @click="hideSnackbar">关闭</v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>
