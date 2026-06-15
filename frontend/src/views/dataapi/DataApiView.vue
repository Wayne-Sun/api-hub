<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useDataapiStore } from '@/stores/dataapi'
import { useAppStore } from '@/stores/app'
import type { ApiType } from '@/types'
import { storeToRefs } from 'pinia'
import EmptyState from '@/components/EmptyState.vue'
import ErrorSnackbar from '@/components/ErrorSnackbar.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import DataApiFormDialog from '@/components/dataapi/DataApiFormDialog.vue'

const store = useDataapiStore()
const { apis, error } = storeToRefs(store)
const { fetchApis } = store

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

/** Columns for the active tab. */
const activeColumns = computed(() => {
  return columnsMap[activeTab.value]
})

// Fetch data on tab or page change
watch([activeTab, page], ([newTab, newPage]) => {
  fetchApis(newTab, newPage)
})

onMounted(() => {
  fetchApis(activeTab.value, page.value)
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
    <div class="d-flex align-center mb-4">
      <h2 class="text-h5">API 管理</h2>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" @click="showRegisterDialog = true">
        注册
      </v-btn>
    </div>

    <v-tabs v-model="activeTab">
      <v-tab value="hbase">HBase</v-tab>
      <v-tab value="solr">Solr</v-tab>
      <v-tab value="sql">SQL</v-tab>
    </v-tabs>

    <!-- Loading state -->
    <v-progress-linear
      v-if="tabData.loading"
      indeterminate
      color="primary"
    />

    <!-- Data table -->
    <v-data-table
      v-else-if="tabData.list.length > 0"
      :headers="activeColumns"
      :items="tabData.list"
      :hide-default-footer="true"
      class="mt-2"
    >
      <template #item.actions="{ item }">
        <v-btn
          icon="mdi-check-circle-outline"
          variant="text"
          size="small"
          :loading="actionLoading"
          @click="onToggle(activeTab, item.id as number, item.name)"
        />
      </template>
    </v-data-table>

    <!-- Pagination -->
    <v-pagination
      v-if="tabData.total > 0"
      v-model="page"
      :length="totalPages"
      @update:model-value="onPageChange"
      class="mt-4"
    />

    <!-- Empty state (no loading + no data) -->
    <EmptyState
      v-if="!tabData.loading && tabData.list.length === 0"
      message="暂无数据"
    />

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

    <!-- Error state -->
    <ErrorSnackbar
      :show="!!error"
      :message="error || ''"
      @close="store.error = null"
    />
  </div>
</template>
