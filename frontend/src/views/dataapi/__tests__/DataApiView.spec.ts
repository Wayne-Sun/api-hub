import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useDataapiStore } from '@/stores/dataapi'
import { useAppStore } from '@/stores/app'
import DataApiView from '../DataApiView.vue'
import type { HbaseApiConf } from '@/types'

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
    template: '<div class="v-data-table"><slot /></div>',
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
    props: ['icon', 'prependIcon', 'color', 'variant', 'size'],
    emits: ['click'],
  },
  'v-spacer': {
    template: '<span class="v-spacer" />',
  },
  'v-icon': {
    template: '<span class="v-icon"><slot /></span>',
    props: ['icon', 'size', 'color'],
  },
  'ConfirmDialog': {
    template: '<div v-if="show" class="confirm-dialog-stub"><div class="confirm-title">{{ title }}</div><div class="confirm-message">{{ message }}</div><button class="confirm-btn" @click="$emit(\'confirm\')">确认</button><button class="cancel-btn" @click="$emit(\'cancel\')">取消</button></div>',
    props: ['show', 'title', 'message', 'confirmText'],
    emits: ['confirm', 'cancel'],
  },
  'v-snackbar': {
    template: '<div class="v-snackbar" v-if="modelValue"><slot /><slot name="actions" /></div>',
    props: ['modelValue', 'color', 'timeout'],
    emits: ['update:modelValue'],
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
        stubs: vuetifyStubs,
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

  it('shows v-progress-linear when tabData.loading is true', () => {
    store.apis = {
      hbase: { list: [], total: 0, loading: true },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-progress-linear').exists()).toBe(true)
  })

  it('hides v-progress-linear when tabData.loading is false', () => {
    store.apis = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-progress-linear').exists()).toBe(false)
  })

  it('shows v-data-table when tabData.list has items', () => {
    store.apis = {
      hbase: { list: [sampleHbaseApi], total: 1, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-data-table').exists()).toBe(true)
  })

  it('hides v-data-table when tabData.list is empty', () => {
    store.apis = {
      hbase: { list: [], total: 0, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-data-table').exists()).toBe(false)
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

  it('shows v-pagination when tabData.total > 0', () => {
    store.apis = {
      hbase: { list: [sampleHbaseApi], total: 25, loading: false },
      solr: { list: [], total: 0, loading: false },
      sql: { list: [], total: 0, loading: false },
    }
    wrapper = createWrapper()
    expect(wrapper.find('.v-pagination').exists()).toBe(true)
  })

  it('hides v-pagination when tabData.total is 0', () => {
    store.apis = {
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
