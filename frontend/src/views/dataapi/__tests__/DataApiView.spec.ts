import { describe, it, expect, beforeEach, vi } from 'vitest'
import { nextTick } from 'vue'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useDataapiStore } from '@/stores/dataapi'
import { useAppStore } from '@/stores/app'
import DataApiView from '../DataApiView.vue'
import type { HbaseApiConf } from '@/types'

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
vi.mock('@/api/dataapi', () => ({
  listHbaseApis: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  listSolrApis: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  listSqlApis: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  registerHbaseApi: vi.fn(),
  registerSolrApi: vi.fn(),
  registerSqlApi: vi.fn(),
  enableHbaseApi: vi.fn(),
  enableSolrApi: vi.fn(),
  enableSqlApi: vi.fn(),
  disableHbaseApi: vi.fn(),
  disableSolrApi: vi.fn(),
  disableSqlApi: vi.fn(),
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
  'ConfirmDialog': {
    template: '<div v-if="show" class="confirm-dialog-stub"><div class="confirm-title">{{ title }}</div><div class="confirm-message">{{ message }}</div><button class="confirm-btn" @click="$emit(\'confirm\')">确认</button><button class="cancel-btn" @click="$emit(\'cancel\')">取消</button></div>',
    props: ['show', 'title', 'message', 'confirmText'],
    emits: ['confirm', 'cancel'],
  },
}

const sampleHbaseApi: HbaseApiConf = {
  id: 1,
  name: '测试 API',
  comments: '备注信息',
  dataSourceId: 1,
  type: 1,
  tableName: 'test_table',
  columns: '*',
}

describe('DataApiView.vue', () => {
  let wrapper: VueWrapper
  let store: ReturnType<typeof useDataapiStore>

  function createWrapper() {
    return mount(DataApiView, {
      global: {
        stubs: naiveStubs,
      },
    })
  }

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useDataapiStore()

    // Prevent onMounted fetchApis from overriding test-specific store state
    vi.spyOn(store, 'fetchApis').mockImplementation(() => {
      // Keep the store as-is so pre-set state is preserved
      return Promise.resolve()
    })
  })

  it('renders the title', () => {
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('API 管理')
  })

  it('renders the 注册 button', () => {
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('注册')
  })

  it('renders three tabs: HBase, Solr, SQL', () => {
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('HBase')
    expect(wrapper.text()).toContain('Solr')
    expect(wrapper.text()).toContain('SQL')
  })

  it('shows n-spin when tabData.loading is true', () => {
    store.apis = {
      hbase: { list: [], total: 0, loading: true },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    // Real NSpin renders .n-spin element only when show=true
    expect(wrapper.find('.n-spin').exists()).toBe(true)
  })

  it('hides n-spin when tabData.loading is false', () => {
    store.apis = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    // Real NSpin does not render .n-spin element when show=false
    expect(wrapper.find('.n-spin').exists()).toBe(false)
  })

  it('shows n-data-table when tabData.list has items', () => {
    store.apis = {
      hbase: { list: [sampleHbaseApi], total: 1, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.n-data-table').exists()).toBe(true)
  })

  it('hides n-data-table when tabData.list is empty', () => {
    store.apis = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.n-data-table').exists()).toBe(false)
  })

  it('shows EmptyState when list is empty and not loading', () => {
    store.apis = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.text()).toContain('暂无数据')
  })

  it('shows n-pagination when tabData.total > 0', () => {
    store.apis = {
      hbase: { list: [sampleHbaseApi], total: 25, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.n-pagination').exists()).toBe(true)
  })

  it('hides n-pagination when tabData.total is 0', () => {
    store.apis = {
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
    it('calls fetchApis with activeTab and page on mount', () => {
      // For this test only, restore the original fetchApis
      const fetchSpy = vi.spyOn(store, 'fetchApis').mockResolvedValue()
      wrapper = createWrapper()
      expect(fetchSpy).toHaveBeenCalledOnce()
      expect(fetchSpy).toHaveBeenCalledWith('hbase', 1)
    })
  })

  describe('enable/disable toggle', () => {
    let appStore: ReturnType<typeof useAppStore>
    let snackbarSpy: ReturnType<typeof vi.spyOn>

    beforeEach(() => {
      appStore = useAppStore()
      snackbarSpy = vi.spyOn(appStore, 'showSnackbar')
    })

    it('shows confirm dialog when onToggle is called', () => {
      wrapper = createWrapper()
      const vm = wrapper.vm as any
      vm.onToggle('hbase', 1, '测试 API')

      expect(vm.confirmDialog.show).toBe(true)
      expect(vm.confirmDialog.title).toBe('确认操作')
      expect(vm.confirmDialog.message).toContain('测试 API')
    })

    it('hides confirm dialog on handleCancel', () => {
      wrapper = createWrapper()
      const vm = wrapper.vm as any
      vm.onToggle('hbase', 1, '测试 API')
      expect(vm.confirmDialog.show).toBe(true)

      vm.handleCancel()

      expect(vm.confirmDialog.show).toBe(false)
    })

    it('renders confirm dialog in template when show is true', async () => {
      wrapper = createWrapper()
      const vm = wrapper.vm as any
      vm.onToggle('hbase', 1, '测试 API')
      await wrapper.vm.$nextTick()

      const dialog = wrapper.find('.confirm-dialog-stub')
      expect(dialog.exists()).toBe(true)
      expect(dialog.text()).toContain('确认操作')
      expect(dialog.text()).toContain('测试 API')
    })

    it('calls enableApi and shows success snackbar', async () => {
      wrapper = createWrapper()
      const vm = wrapper.vm as any
      const fetchSpy = vi.spyOn(store, 'fetchApis').mockResolvedValue()

      vm.onToggle('hbase', 1, '测试 API')
      await vm.handleConfirm()

      expect(snackbarSpy).toHaveBeenCalledWith('启用成功', 'success')
      expect(fetchSpy).toHaveBeenCalled()
      expect(vm.confirmDialog.show).toBe(false)
      expect(vm.actionLoading).toBe(false)
    })

    it('falls back to disableApi when enableApi fails', async () => {
      wrapper = createWrapper()
      const vm = wrapper.vm as any
      vi.spyOn(store, 'enableApi').mockRejectedValueOnce(new Error('already enabled'))

      vm.onToggle('hbase', 1, '测试 API')
      await vm.handleConfirm()

      expect(snackbarSpy).toHaveBeenCalledWith('禁用成功', 'success')
      expect(vm.confirmDialog.show).toBe(false)
      expect(vm.actionLoading).toBe(false)
    })

    it('shows error snackbar when both enable and disable fail', async () => {
      wrapper = createWrapper()
      const vm = wrapper.vm as any
      vi.spyOn(store, 'enableApi').mockRejectedValueOnce(new Error('API error'))
      vi.spyOn(store, 'disableApi').mockRejectedValueOnce(new Error('API error'))

      vm.onToggle('hbase', 1, '测试 API')
      await vm.handleConfirm()

      expect(snackbarSpy).toHaveBeenCalledWith('操作失败', 'error')
      expect(vm.confirmDialog.show).toBe(false)
      expect(vm.actionLoading).toBe(false)
    })

    it('disables button during API call (actionLoading)', async () => {
      wrapper = createWrapper()
      const vm = wrapper.vm as any

      vm.onToggle('hbase', 1, '测试 API')
      // Don't await - check loading state during the async operation
      const promise = vm.handleConfirm()

      // After handleConfirm starts (synchronous part) but before await
      expect(vm.actionLoading).toBe(true)

      await promise
      expect(vm.actionLoading).toBe(false)
    })
  })
})
