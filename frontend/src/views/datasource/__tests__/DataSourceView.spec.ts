import { describe, it, expect, beforeEach, vi } from 'vitest'
import { nextTick } from 'vue'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useDatasourceStore } from '@/stores/datasource'
import { useAppStore } from '@/stores/app'
import DataSourceView from '../DataSourceView.vue'
import type { HbaseSourceConf } from '@/types'
import type { Mock } from 'vitest'
import * as datasourceApi from '@/api/datasource'

// Shared mock message for verifying naive-ui error display
const mockMessage = vi.hoisted(() => ({ error: vi.fn(), info: vi.fn(), success: vi.fn(), warning: vi.fn() }))

// Mock naive-ui to prevent useMessage() from crashing in tests without n-message-provider
vi.mock('naive-ui', async (importOriginal) => {
  const mod = await importOriginal()
  return {
    ...(mod as Record<string, unknown>),
    useMessage: () => mockMessage,
  }
})

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

// Stub Naive UI components to avoid jsdom rendering issues
// Pattern matches App.spec.ts and existing component tests
const naiveStubs = {
  'n-tabs': { template: '<div class="n-tabs"><slot /></div>', props: ['value'], emits: ['update:value'] },
  'n-tab': { template: '<button class="n-tab" @click="$emit(\'click\')"><slot /></button>', props: ['name'] },
  'n-data-table': { template: '<div class="n-data-table"><slot /></div>', props: ['columns', 'data', 'bordered', 'singleLine'] },
  'n-pagination': { template: '<div class="n-pagination" />', props: ['page', 'pageCount'], emits: ['update:page'] },
  'n-spin': { template: '<div class="n-spin" :data-show="show"><slot /></div>', props: ['show'] },
  'n-button': { template: '<button class="n-button" @click="$emit(\'click\')"><slot /></button>', props: ['type', 'size', 'quaternary', 'circle', 'loading'], emits: ['click'] },
  'n-icon': { template: '<span class="n-icon"><slot /></span>' },
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
        stubs: naiveStubs,
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

  it('shows n-spin when tabData.loading is true', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: true },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    // Real NSpin renders .n-spin element only when show=true
    expect(wrapper.find('.n-spin').exists()).toBe(true)
  })

  it('hides n-spin when tabData.loading is false', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    // Real NSpin does not render .n-spin element when show=false
    expect(wrapper.find('.n-spin').exists()).toBe(false)
  })

  it('shows n-data-table when tabData.list has items', () => {
    store.sources = {
      hbase: { list: [sampleHbaseItem], total: 1, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.n-data-table').exists()).toBe(true)
  })

  it('hides n-data-table when tabData.list is empty', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.n-data-table').exists()).toBe(false)
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

  it('shows n-pagination when tabData.total > 0', () => {
    store.sources = {
      hbase: { list: [sampleHbaseItem], total: 25, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.n-pagination').exists()).toBe(true)
  })

  it('hides n-pagination when tabData.total is 0', () => {
    store.sources = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.n-pagination').exists()).toBe(false)
  })

  it('calls message.error when store.error is set after mount and clears it', async () => {
    wrapper = createWrapper()
    store.error = '出错了'
    await nextTick()
    expect(mockMessage.error).toHaveBeenCalledWith('出错了')
    expect(store.error).toBeNull()
  })

  it('does not call message.error when store.error is null', () => {
    mockMessage.error.mockClear()
    store.error = null
    wrapper = createWrapper()
    expect(mockMessage.error).not.toHaveBeenCalled()
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
