<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useDatasourceStore } from '@/stores/datasource'
import { useAppStore } from '@/stores/app'
import type { SourceType } from '@/types'
import { storeToRefs } from 'pinia'
import EmptyState from '@/components/EmptyState.vue'
import ErrorSnackbar from '@/components/ErrorSnackbar.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import DataSourceFormDialog from '@/components/datasource/DataSourceFormDialog.vue'

const store = useDatasourceStore()
const { sources, error } = storeToRefs(store)
const { fetchSources } = store

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

/** Columns for the active tab. */
const activeColumns = computed(() => {
  return columnsMap[activeTab.value]
})

// Fetch data on tab or page change
watch([activeTab, page], ([newTab, newPage]) => {
  fetchSources(newTab, newPage)
})

onMounted(() => {
  fetchSources(activeTab.value, page.value)
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
    <div class="d-flex align-center mb-4">
      <h2 class="text-h5">数据源管理</h2>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" @click="showCreateDialog = true">
        新增
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

    <!-- Error state -->
    <ErrorSnackbar
      :show="!!error"
      :message="error || ''"
      @close="store.error = null"
    />
  </div>
</template>
