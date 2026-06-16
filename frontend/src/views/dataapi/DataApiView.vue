<script setup lang="ts">
import { h, ref, computed, onMounted, watch } from 'vue'
import { useDataapiStore } from '@/stores/dataapi'
import { useAppStore } from '@/stores/app'
import type { ApiType } from '@/types'
import { storeToRefs } from 'pinia'
import { useMessage, NButton, NIcon, NTabs, NTab, NDataTable, NPagination, NSpin } from 'naive-ui'
import type { DataTableColumn } from 'naive-ui'
import { AddOutline, CheckmarkCircleOutline } from '@vicons/ionicons5'
import EmptyState from '@/components/EmptyState.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import DataApiFormDialog from '@/components/dataapi/DataApiFormDialog.vue'

const store = useDataapiStore()
const { apis, error } = storeToRefs(store)
const { fetchApis } = store

const message = useMessage()

const appStore = useAppStore()

const activeTab = ref<ApiType>('hbase')
const page = ref(1)
const pageSize = 10

type ColumnDef = { key: string; title: string }

const columnsMap: Record<ApiType, ColumnDef[]> = {
  hbase: [
    { key: 'id', title: 'ID' },
    { key: 'name', title: '名称' },
    { key: 'comments', title: '备注' },
    { key: 'dataSourceId', title: '数据源 ID' },
    { key: 'tableName', title: '表名' },
    { key: 'columns', title: '列' },
    { key: 'actions', title: '操作' },
  ],
  solr: [
    { key: 'id', title: 'ID' },
    { key: 'name', title: '名称' },
    { key: 'comments', title: '备注' },
    { key: 'dataSourceId', title: '数据源 ID' },
    { key: 'collection', title: 'Collection' },
    { key: 'fields', title: '字段' },
    { key: 'actions', title: '操作' },
  ],
  sql: [
    { key: 'id', title: 'ID' },
    { key: 'name', title: '名称' },
    { key: 'comments', title: '备注' },
    { key: 'dataSourceId', title: '数据源 ID' },
    { key: 'pageTag', title: '分页' },
    { key: 'pageSize', title: '每页大小' },
    { key: 'actions', title: '操作' },
  ],
}

/** Current tab's data slice from the store (loading, list, total). */
const tabData = computed(() => {
  return apis.value[activeTab.value]!
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
  fetchApis(newTab, newPage)
})

onMounted(() => {
  fetchApis(activeTab.value, page.value)
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
  targetType: ApiType
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

// Register dialog state
const showRegisterDialog = ref(false)

function onRegistered() {
  showRegisterDialog.value = false
  fetchApis(activeTab.value, page.value)
}

function onToggle(type: ApiType, id: number, name: string) {
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
    await store.enableApi(targetType, targetId)
    appStore.showSnackbar('启用成功', 'success')
    await store.fetchApis(activeTab.value, page.value)
  } catch {
    // If enable fails (already enabled), try disable
    try {
      await store.disableApi(targetType, targetId)
      appStore.showSnackbar('禁用成功', 'success')
      await store.fetchApis(activeTab.value, page.value)
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
      <h2 style="font-size: 24px; font-weight: 500; margin: 0">API 管理</h2>
      <div style="flex: 1" />
      <n-button type="primary" @click="showRegisterDialog = true">
        <template #icon><n-icon><AddOutline /></n-icon></template>
        注册
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

    <!-- Register API dialog -->
    <DataApiFormDialog
      :show="showRegisterDialog"
      :api-type="activeTab"
      @close="showRegisterDialog = false"
      @registered="onRegistered"
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
