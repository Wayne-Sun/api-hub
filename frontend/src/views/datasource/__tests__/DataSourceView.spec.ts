import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useDatasourceStore } from '@/stores/datasource'
import { useAppStore } from '@/stores/app'
import DataSourceView from '../DataSourceView.vue'
import type { HbaseSourceConf } from '@/types'
import type { Mock } from 'vitest'
import * as datasourceApi from '@/api/datasource'

// Mock the API module to avoid real HTTP calls
vi.mock('@/api/datasource', () => ({
  listHbaseSources: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  listSolrSources: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  listSqlSources: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  insertHbaseSource: vi.fn(),
  insertSolrSource: vi.fn(),
  insertSqlSource: vi.fn(),
  enableHbaseSource: vi.fn(),
  enableSolrSource: vi.fn(),
  enableSqlSource: vi.fn(),
  disableHbaseSource: vi.fn(),
  disableSolrSource: vi.fn(),
  disableSqlSource: vi.fn(),
}))

// Stub Vuetify components to avoid jsdom rendering issues
// Pattern matches App.spec.ts and existing component tests
const vuetifyStubs = {
  'v-tabs': {
    template: '<div class="v-tabs"><slot /></div>',
    props: ['modelValue'],
    emits: ['update:modelValue'],
  },
  'v-tab': {
    template: '<button class="v-tab" @click="$emit(\'click\')"><slot /></button>',
    props: ['value'],
    emits: ['click'],
  },
  'v-data-table': {
    template: '<div class="v-data-table"><slot name="item.actions" v-bind="{ item: items && items[0] }" /><slot /></div>',
    props: ['headers', 'items', 'hideDefaultFooter'],
  },
  'v-pagination': {
    template: '<div class="v-pagination" />',
    props: ['modelValue', 'length'],
    emits: ['update:modelValue'],
  },
  'v-progress-linear': {
    template: '<div class="v-progress-linear" />',
    props: ['indeterminate', 'color'],
  },
  'v-btn': {
    template: '<button class="v-btn" @click="$emit(\'click\')"><slot /></button>',
    props: ['icon', 'prependIcon', 'color', 'variant', 'size', 'loading'],
    emits: ['click'],
  },
  'v-spacer': {
    template: '<span class="v-spacer" />',
  },
  'v-icon': {
    template: '<span class="v-icon"><slot /></span>',
    props: ['icon', 'size', 'color'],
  },
  'v-snackbar': {
    template: '<div class="v-snackbar" v-if="modelValue"><slot /><slot name="actions" /></div>',
    props: ['modelValue', 'color', 'timeout'],
    emits: ['update:modelValue'],
  },
  'v-dialog': {
    template: '<div class="v-dialog" v-if="modelValue"><slot /></div>',
    props: ['modelValue', 'maxWidth'],
    emits: ['update:modelValue'],
  },
  'v-card': {
    template: '<div class="v-card"><slot /></div>',
  },
  'v-card-title': {
    template: '<div class="v-card-title"><slot /></div>',
  },
  'v-card-text': {
    template: '<div class="v-card-text"><slot /></div>',
  },
  'v-card-actions': {
    template: '<div class="v-card-actions"><slot /></div>',
  },
}

const sampleHbaseItem: HbaseSourceConf = {
  id: 1,
  name: '测试 HBase',
  comments: '备注信息',
  hbaseSitePath: '/etc/hbase/conf/hbase-site.xml',
  coreSitePath: '/etc/hadoop/conf/core-site.xml',
}

describe('DataSourceView.vue', () => {
  let wrapper: VueWrapper
  let store: ReturnType<typeof useDatasourceStore>

  function createWrapper() {
    return mount(DataSourceView, {
      global: {
        stubs: vuetifyStubs,
      },
    })
  }

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useDatasourceStore()

    // Prevent onMounted fetchSources from overriding test-specific store state
    vi.spyOn(store, 'fetchSources').mockImplementation(() => {
      // Keep the store as-is so pre-set state is preserved
      return Promise.resolve()
    })
  })

  it('renders the title', () => {
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('数据源管理')
  })

  it('renders the 新增 button', () => {
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('新增')
  })

  it('renders three tabs: HBase, Solr, SQL', () => {
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('HBase')
    expect(wrapper.text()).toContain('Solr')
    expect(wrapper.text()).toContain('SQL')
  })

  it('shows v-progress-linear when tabData.loading is true', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: true },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-progress-linear').exists()).toBe(true)
  })

  it('hides v-progress-linear when tabData.loading is false', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-progress-linear').exists()).toBe(false)
  })

  it('shows v-data-table when tabData.list has items', () => {
    store.sources = {
      hbase: { list: [sampleHbaseItem], total: 1, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-data-table').exists()).toBe(true)
  })

  it('hides v-data-table when tabData.list is empty', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-data-table').exists()).toBe(false)
  })

  it('shows EmptyState when list is empty and not loading', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('暂无数据')
  })

  it('shows v-pagination when tabData.total > 0', () => {
    store.sources = {
      hbase: { list: [sampleHbaseItem], total: 25, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-pagination').exists()).toBe(true)
  })

  it('hides v-pagination when tabData.total is 0', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-pagination').exists()).toBe(false)
  })

  it('shows error snackbar when store has error', () => {
    store.error = '网络连接失败'
    wrapper = createWrapper()
    const snackbar = wrapper.find('.v-snackbar')
    expect(snackbar.exists()).toBe(true)
    expect(snackbar.text()).toContain('网络连接失败')
  })

  it('hides error snackbar when store has no error', () => {
    store.error = null
    wrapper = createWrapper()
    expect(wrapper.find('.v-snackbar').exists()).toBe(false)
  })

  it('clears error when snackbar close is emitted', async () => {
    store.error = '出错了'
    wrapper = createWrapper()

    // Emit the close event
    const snackbar = wrapper.findComponent({ name: 'ErrorSnackbar-stub' })
    // If ErrorSnackbar is not stubbed, find its emit differently
    // ErrorSnackbar is a real component, so we check via its emit
    const errorSnackbar = wrapper.findComponent({ name: 'ErrorSnackbar' })
    if (errorSnackbar.exists()) {
      errorSnackbar.vm.$emit('close')
    } else {
      // Fallback: the real component emits 'close' from its button
      const closeBtn = wrapper.find('.v-snackbar .v-btn')
      if (closeBtn.exists()) {
        await closeBtn.trigger('click')
      }
    }

    await wrapper.vm.$nextTick()
    expect(store.error).toBeNull()
  })

  describe('initial fetch on mount', () => {
    it('calls fetchSources with activeTab and page on mount', () => {
      // For this test only, restore the original fetchSources
      const fetchSpy = vi.spyOn(store, 'fetchSources').mockResolvedValue()
      wrapper = createWrapper()
      expect(fetchSpy).toHaveBeenCalledOnce()
      expect(fetchSpy).toHaveBeenCalledWith('hbase', 1)
    })
  })

  describe('enable/disable toggle', () => {
    let snackbarSpy: ReturnType<typeof vi.spyOn>
    let enableHbaseMock: Mock
    let disableHbaseMock: Mock

    function setupStoreWithData() {
      store.sources = {
        hbase: { list: [sampleHbaseItem], total: 1, loading: false },
        solr: { list: [], total: 0, loading: false },
        sql: { list: [], total: 0, loading: false },
      }
    }

    beforeEach(() => {
      wrapper = createWrapper()
      setupStoreWithData()
      // Re-create wrapper after store data is set so the table renders with items
      wrapper = createWrapper()
      setupStoreWithData()

      const appStore = useAppStore()
      snackbarSpy = vi.spyOn(appStore, 'showSnackbar')

      enableHbaseMock = datasourceApi.enableHbaseSource as Mock
      disableHbaseMock = datasourceApi.disableHbaseSource as Mock
    })

    it('initializes confirmDialog with show=false', () => {
      wrapper = createWrapper()
      setupStoreWithData()
      const vm = wrapper.vm as any
      expect(vm.confirmDialog.show).toBe(false)
    })

    it('sets confirmDialog state when onToggle is called', () => {
      wrapper = createWrapper()
      setupStoreWithData()
      const vm = wrapper.vm as any
      vm.onToggle('hbase', 1, '测试 HBase')
      expect(vm.confirmDialog.show).toBe(true)
      expect(vm.confirmDialog.targetType).toBe('hbase')
      expect(vm.confirmDialog.targetId).toBe(1)
      expect(vm.confirmDialog.message).toContain('测试 HBase')
    })

    it('closes dialog on cancel', () => {
      wrapper = createWrapper()
      setupStoreWithData()
      const vm = wrapper.vm as any
      vm.confirmDialog = {
        ...vm.confirmDialog,
        show: true,
        message: '确认启用 测试 HBase？',
        targetType: 'hbase',
        targetId: 1,
      }
      vm.handleCancel()
      expect(vm.confirmDialog.show).toBe(false)
    })

    it('calls enableSource on confirm and shows 启用成功 snackbar on success', async () => {
      enableHbaseMock.mockResolvedValue({ data: { code: 200, message: 'success' } })
      const vm = wrapper.vm as any
      vm.confirmDialog = {
        ...vm.confirmDialog,
        show: true,
        message: '确认启用 测试 HBase？',
        targetType: 'hbase',
        targetId: 1,
      }
      await vm.handleConfirm()
      expect(enableHbaseMock).toHaveBeenCalledWith(1)
      expect(snackbarSpy).toHaveBeenCalledWith('启用成功', 'success')
    })

    it('falls back to disableSource when enableSource fails', async () => {
      enableHbaseMock.mockRejectedValue(new Error('already enabled'))
      disableHbaseMock.mockResolvedValue({ data: { code: 200, message: 'success' } })
      const vm = wrapper.vm as any
      vm.confirmDialog = {
        ...vm.confirmDialog,
        show: true,
        message: '确认启用 测试 HBase？',
        targetType: 'hbase',
        targetId: 1,
      }
      await vm.handleConfirm()
      expect(enableHbaseMock).toHaveBeenCalledWith(1)
      expect(disableHbaseMock).toHaveBeenCalledWith(1)
      expect(snackbarSpy).toHaveBeenCalledWith('禁用成功', 'success')
    })

    it('shows 操作失败 snackbar when both enable and disable fail', async () => {
      enableHbaseMock.mockRejectedValue(new Error('already enabled'))
      disableHbaseMock.mockRejectedValue(new Error('already disabled'))
      const vm = wrapper.vm as any
      vm.confirmDialog = {
        ...vm.confirmDialog,
        show: true,
        message: '确认启用 测试 HBase？',
        targetType: 'hbase',
        targetId: 1,
      }
      await vm.handleConfirm()
      expect(snackbarSpy).toHaveBeenCalledWith('操作失败', 'error')
    })

    it('sets actionLoading during the toggle operation and resets after', async () => {
      enableHbaseMock.mockResolvedValue({ data: { code: 200, message: 'success' } })
      const vm = wrapper.vm as any
      vm.confirmDialog = {
        ...vm.confirmDialog,
        show: true,
        message: '确认启用 测试 HBase？',
        targetType: 'hbase',
        targetId: 1,
      }
      const promise = vm.handleConfirm()
      expect(vm.actionLoading).toBe(true)
      await promise
      expect(vm.actionLoading).toBe(false)
    })

    it('closes confirm dialog after handleConfirm completes', async () => {
      enableHbaseMock.mockResolvedValue({ data: { code: 200, message: 'success' } })
      const vm = wrapper.vm as any
      vm.confirmDialog = {
        ...vm.confirmDialog,
        show: true,
        message: '确认启用 测试 HBase？',
        targetType: 'hbase',
        targetId: 1,
      }
      await vm.handleConfirm()
      expect(vm.confirmDialog.show).toBe(false)
    })

    it('disables button during toggle operation via actionLoading', async () => {
      enableHbaseMock.mockResolvedValue({ data: { code: 200, message: 'success' } })
      const vm = wrapper.vm as any
      vm.confirmDialog = {
        ...vm.confirmDialog,
        show: true,
        message: '确认启用 测试 HBase？',
        targetType: 'hbase',
        targetId: 1,
      }
      const promise = vm.handleConfirm()
      // actionLoading is bound as :loading to v-btn to prevent double-click
      expect(vm.actionLoading).toBe(true)
      await promise
      expect(vm.actionLoading).toBe(false)
    })
  })
})
