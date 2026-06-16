<script setup lang="ts">
import { h, type Component, watch } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAppStore } from '@/stores/app'
import {
  useMessage,
  NConfigProvider,
  NMessageProvider,
  NLayout,
  NLayoutHeader,
  NLayoutSider,
  NLayoutContent,
  NMenu,
  NButton,
  NIcon,
} from 'naive-ui'
import { zhCN } from 'naive-ui'
import { MenuOutline, ServerOutline, CodeSlashOutline } from '@vicons/ionicons5'

const appStore = useAppStore()
const { drawerOpen } = storeToRefs(appStore)
const { toggleDrawer } = appStore

const router = useRouter()
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

function handleMenuSelect(key: string) {
  router.push({ name: key })
}

function renderIcon(icon: Component) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions = [
  { label: '数据源管理', key: 'datasource', icon: renderIcon(ServerOutline) },
  { label: 'API 管理', key: 'dataapi', icon: renderIcon(CodeSlashOutline) },
]
</script>

<template>
  <n-message-provider>
    <n-config-provider :locale="zhCN">
      <n-layout position="absolute" style="height: 100vh">
        <n-layout-header
          style="display: flex; align-items: center; padding: 0 16px; height: 48px"
        >
          <n-button quaternary style="margin-right: 12px" @click="toggleDrawer">
            <template #icon>
              <n-icon><MenuOutline /></n-icon>
            </template>
          </n-button>
          <span style="font-size: 18px; font-weight: 600">API Hub 管理平台</span>
        </n-layout-header>
        <n-layout has-sider>
          <n-layout-sider
            collapse-mode="width"
            :collapsed="!drawerOpen"
            show-trigger="arrow-circle"
            width="240"
          >
            <n-menu :options="menuOptions" @update:value="handleMenuSelect" />
          </n-layout-sider>
          <n-layout-content content-style="padding: 24px">
            <router-view />
          </n-layout-content>
        </n-layout>
      </n-layout>
    </n-config-provider>
  </n-message-provider>
</template>
