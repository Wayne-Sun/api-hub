<script setup lang="ts">
import { h, ref, computed, onMounted, watch } from 'vue'
import { useDatasourceStore } from '@/stores/datasource'
import { useAppStore } from '@/stores/app'
import type { SourceType } from '@/types'
import { storeToRefs } from 'pinia'
import { useMessage, NButton, NIcon, NTabs, NTab, NDataTable, NPagination, NSpin } from 'naive-ui'
import type { DataTableColumn } from 'naive-ui'
import { AddOutline, CheckmarkCircleOutline } from '@vicons/ionicons5'
import EmptyState from '@/components/EmptyState.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import DataSourceFormDialog from '@/components/datasource/DataSourceFormDialog.vue'

const store = useDatasourceStore()
const { sources, error } = storeToRefs(store)
const { fetchSources } = store

const message = useMessage()

const appStore = useAppStore()

const activeTab = ref<SourceType>('hbase')
const page = ref(1)
const pageSize = 10

type ColumnDef = { key: string; title: string }

const columnsMap: Record<SourceType, ColumnDef[]> = {
  hbase: [
    { key: 'id', title: 'ID' },
    { key: 'name', title: '名称' },
    { key: 'comments', title: '备注' },
    { key: 'hbaseSitePath', title: 'HBase 配置路径' },
    { key: 'coreSitePath', title: 'Core 配置路径' },
    { key: 'actions', title: '操作' },
  ],
  solr: [
    { key: 'id', title: 'ID' },
    { key: 'name', title: '名称' },
    { key: 'comments', title: '备注' },
    { key: 'zkHosts', title: 'ZooKeeper 地址' },
    { key: 'zkChroot', title: 'Chroot 路径' },
    { key: 'actions', title: '操作' },
  ],
  sql: [
    { key: 'id', title: 'ID' },
    { key: 'name', title: '名称' },
    { key: 'comments', title: '备注' },
    { key: 'dialect', title: '数据库类型' },
    { key: 'url', title: '连接地址' },
    { key: 'username', title: '用户名' },
    { key: 'actions', title: '操作' },
  ],
}

/** Current tab's data slice from the store (loading, list, total). */
const tabData = computed(() => {
  const tab = activeTab.value
  // The store always initializes all three tabs, so data is guaranteed to exist.
  return sources.value[tab]!
})

/** Total pages for the active tab. */
const totalPages = computed(() => {
  return Math.ceil(tabData.value.total / pageSize)
})

/** Columns for the active tab, with render function for actions column. */
const tableColumns = computed<DataTableColumn[]>(() => {
  return columnsMap[activeTab.value].map(col => {
    if (col.key === 'actions') {
      return {
        key: 'actions',
        title: col.title,
        render(row: Record<string, unknown>) {
          return h(
            NButton,
            {
              size: 'small',
              quaternary: true,
              circle: true,
              loading: actionLoading.value,
              onClick: () => onToggle(activeTab.value, row.id as number, row.name as string)
            },
            {
              icon: () => h(NIcon, null, { default: () => h(CheckmarkCircleOutline) })
            }
          )
        }
      }
    }
    return col
  })
})

// Fetch data on tab or page change
watch([activeTab, page], ([newTab, newPage]) => {
  fetchSources(newTab, newPage)
})

onMounted(() => {
  fetchSources(activeTab.value, page.value)
})

// Watch for store errors and display as naive-ui message
watch(() => store.error, (err) => {
  if (err) {
    message.error(err)
    store.error = null
  }
})

function onPageChange(newPage: number) {
  page.value = newPage
}

// Enable/disable toggle state
const confirmDialog = ref<{
  show: boolean
  title: string
  message: string
  targetType: SourceType
  targetId: number
  action: 'enable' | 'disable'
}>({
  show: false,
  title: '',
  message: '',
  targetType: 'hbase',
  targetId: 0,
  action: 'enable',
})
const actionLoading = ref(false)

// Create dialog state
const showCreateDialog = ref(false)

function onCreated() {
  showCreateDialog.value = false
  fetchSources(activeTab.value, page.value)
}

function onToggle(type: SourceType, id: number, name: string) {
  confirmDialog.value = {
    show: true,
    title: '确认操作',
    message: `确认启用 ${name}？`,
    targetType: type,
    targetId: id,
    action: 'enable',
  }
}

async function handleConfirm() {
  actionLoading.value = true
  const { targetType, targetId } = confirmDialog.value
  try {
    await store.enableSource(targetType, targetId)
    appStore.showSnackbar('启用成功', 'success')
    await store.fetchSources(activeTab.value, page.value)
  } catch {
    // If enable fails (already enabled), try disable
    try {
      await store.disableSource(targetType, targetId)
      appStore.showSnackbar('禁用成功', 'success')
      await store.fetchSources(activeTab.value, page.value)
    } catch {
      appStore.showSnackbar('操作失败', 'error')
    }
  } finally {
    actionLoading.value = false
    confirmDialog.value.show = false
  }
}

function handleCancel() {
  confirmDialog.value.show = false
}

// Expose internals for testing
defineExpose({ confirmDialog, actionLoading, onToggle, handleConfirm, handleCancel })
</script>

<template>
  <div>
    <div style="display: flex; align-items: center; margin-bottom: 16px">
      <h2 style="font-size: 24px; font-weight: 500; margin: 0">数据源管理</h2>
      <div style="flex: 1" />
      <n-button type="primary" @click="showCreateDialog = true">
        <template #icon><n-icon><AddOutline /></n-icon></template>
        新增
      </n-button>
    </div>

    <n-tabs v-model:value="activeTab" type="line">
      <n-tab name="hbase">HBase</n-tab>
      <n-tab name="solr">Solr</n-tab>
      <n-tab name="sql">SQL</n-tab>
    </n-tabs>

    <!-- Loading + Data table or Empty state -->
    <n-spin :show="tabData.loading" style="margin-top: 8px">
      <n-data-table
        v-if="tabData.list.length > 0"
        :columns="tableColumns"
        :data="tabData.list"
        bordered
        single-line
      />

      <EmptyState v-else message="暂无数据" />
    </n-spin>

    <!-- Pagination -->
    <div v-if="tabData.total > 0" style="margin-top: 16px">
      <n-pagination v-model:page="page" :page-count="totalPages" @update:page="onPageChange" />
    </div>

    <!-- Create data source dialog -->
    <DataSourceFormDialog
      :show="showCreateDialog"
      :source-type="activeTab"
      @close="showCreateDialog = false"
      @created="onCreated"
    />

    <!-- Confirm dialog for enable/disable -->
    <ConfirmDialog
      :show="confirmDialog.show"
      :title="confirmDialog.title"
      :message="confirmDialog.message"
      @confirm="handleConfirm"
      @cancel="handleCancel"
    />

  </div>
</template>
