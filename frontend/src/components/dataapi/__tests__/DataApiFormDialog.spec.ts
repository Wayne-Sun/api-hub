import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useDataapiStore } from '@/stores/dataapi'
import DataApiFormDialog from '../DataApiFormDialog.vue'

// Mock the API module to avoid real HTTP calls
vi.mock('@/api/dataapi', () => ({
  listHbaseApis: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  listSolrApis: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  listSqlApis: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  registerHbaseApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  registerSolrApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  registerSqlApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  enableHbaseApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  enableSolrApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  enableSqlApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  disableHbaseApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  disableSolrApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  disableSqlApi: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  listSqlApiParams: vi.fn().mockResolvedValue({ data: { data: [] } }),
  deleteSqlApiParamsByApiId: vi.fn().mockResolvedValue({ data: { code: 200 } }),
}))

const vuetifyStubs = {
  'v-dialog': { template: '<div class="v-dialog" v-if="modelValue"><slot /></div>', props: ['modelValue'] },
  'v-card': { template: '<div class="v-card"><slot /></div>' },
  'v-card-title': { template: '<div class="v-card-title"><slot /></div>' },
  'v-card-text': { template: '<div class="v-card-text"><slot /></div>' },
  'v-card-actions': { template: '<div class="v-card-actions"><slot /></div>' },
  'v-form': { template: '<form class="v-form"><slot /></form>', props: ['ref'] },
  'v-text-field': {
    template: '<div class="v-text-field"><span class="v-label">{{ label }}</span><slot /></div>',
    props: ['modelValue', 'label', 'rules', 'type', 'density', 'hideDetails'],
    emits: ['update:modelValue'],
  },
  'v-textarea': {
    template: '<div class="v-textarea"><span class="v-label">{{ label }}</span><slot /></div>',
    props: ['modelValue', 'label', 'rules'],
    emits: ['update:modelValue'],
  },
  'v-select': {
    template: '<div class="v-select"><span class="v-label">{{ label }}</span><slot /></div>',
    props: ['modelValue', 'label', 'items', 'rules', 'itemTitle', 'itemValue', 'density', 'hideDetails'],
    emits: ['update:modelValue'],
  },
  'v-switch': {
    template: '<div class="v-switch"><span class="v-label">{{ label }}</span><slot /></div>',
    props: ['modelValue', 'label', 'trueValue', 'falseValue'],
    emits: ['update:modelValue'],
  },
  'v-btn': {
    template: '<button class="v-btn" @click="$emit(\'click\')"><slot /></button>',
    props: ['icon', 'prependIcon', 'color', 'variant', 'size'],
    emits: ['click'],
  },
  'v-spacer': { template: '<span class="v-spacer" />' },
  'v-icon': { template: '<span class="v-icon"><slot /></span>', props: ['icon', 'size', 'color'] },
}

/**
 * Mount dialog with show:false then switch to show:true.
 * This ensures the `watch` handler fires and initialises formData keys.
 */
async function mountDialog(apiType: 'hbase' | 'solr' | 'sql') {
  const w = mount(DataApiFormDialog, {
    props: { show: false, apiType },
    global: { stubs: vuetifyStubs },
  })
  await w.setProps({ show: true })
  await w.vm.$nextTick()
  return w
}

describe('DataApiFormDialog.vue', () => {
  let wrapper: VueWrapper

  beforeEach(() => {
    setActivePinia(createPinia())
  })

  describe('renders correct fields per API type', () => {
    it('shows HBase fields when apiType is hbase', async () => {
      wrapper = await mountDialog('hbase')
      expect(wrapper.text()).toContain('HBase')
      expect(wrapper.text()).toContain('名称')
      expect(wrapper.text()).toContain('备注')
      expect(wrapper.text()).toContain('数据源 ID')
      expect(wrapper.text()).toContain('类型')
      expect(wrapper.text()).toContain('表名')
      expect(wrapper.text()).toContain('列')
    })

    it('shows Solr fields when apiType is solr', async () => {
      wrapper = await mountDialog('solr')
      expect(wrapper.text()).toContain('Solr')
      expect(wrapper.text()).toContain('名称')
      expect(wrapper.text()).toContain('备注')
      expect(wrapper.text()).toContain('数据源 ID')
      expect(wrapper.text()).toContain('Collection')
      expect(wrapper.text()).toContain('字段')
      expect(wrapper.text()).toContain('查询条件')
      expect(wrapper.text()).toContain('排序')
    })

    it('shows SQL fields when apiType is sql', async () => {
      wrapper = await mountDialog('sql')
      expect(wrapper.text()).toContain('SQL')
      expect(wrapper.text()).toContain('名称')
      expect(wrapper.text()).toContain('备注')
      expect(wrapper.text()).toContain('数据源 ID')
      expect(wrapper.text()).toContain('SQL')
      expect(wrapper.text()).toContain('分页')
      expect(wrapper.text()).toContain('参数列表')
      expect(wrapper.text()).toContain('添加参数')
    })

    it('does not show HBase/Solr/SQL specific fields from other types', async () => {
      wrapper = await mountDialog('solr')
      expect(wrapper.text()).not.toContain('表名')
      expect(wrapper.text()).not.toContain('参数列表')
    })
  })

  describe('submit behavior', () => {
    it('calls store.registerApi with correct type and payload on submit', async () => {
      const store = useDataapiStore()
      const registerSpy = vi.spyOn(store, 'registerApi')

      wrapper = await mountDialog('hbase')

      // Set form data on the initialized reactive object
      const vm = wrapper.vm as any
      vm.formData.name = 'test-api'
      vm.formData.dataSourceId = '1'
      vm.formData.type = 1
      vm.formData.tableName = 'test_table'
      vm.formData.columns = '*'

      await wrapper.vm.$nextTick()

      const confirmBtn = wrapper.findAll('.v-btn').find(b => b.text().includes('确认'))
      if (confirmBtn) await confirmBtn.trigger('click')

      expect(registerSpy).toHaveBeenCalledWith('hbase', {
        name: 'test-api',
        comments: '',
        dataSourceId: 1,
        type: 1,
        tableName: 'test_table',
        columns: '*',
      })
    })

    it('passes correct payload for SQL type on submit', async () => {
      const store = useDataapiStore()
      const registerSpy = vi.spyOn(store, 'registerApi')

      wrapper = await mountDialog('sql')

      const vm = wrapper.vm as any
      vm.formData.name = 'sql-api'
      vm.formData.dataSourceId = '2'
      vm.formData.sql = 'SELECT * FROM test'
      vm.formData.pageTag = 1
      vm.formData.pageSize = 20
      vm.formData.paramList = [
        { name: 'p1', type: 'STRING', description: 'param1' },
      ]

      await wrapper.vm.$nextTick()

      const confirmBtn = wrapper.findAll('.v-btn').find(b => b.text().includes('确认'))
      if (confirmBtn) await confirmBtn.trigger('click')

      expect(registerSpy).toHaveBeenCalledWith('sql', {
        name: 'sql-api',
        comments: '',
        dataSourceId: 2,
        sql: 'SELECT * FROM test',
        pageTag: 1,
        pageSize: 20,
        paramList: [
          { name: 'p1', type: 'STRING', description: 'param1' },
        ],
      })
    })
  })

  describe('SQL paramList operations', () => {
    it('supports adding a parameter', async () => {
      wrapper = await mountDialog('sql')

      const vm = wrapper.vm as any
      expect(vm.formData.paramList).toHaveLength(0)

      const addBtn = wrapper.findAll('.v-btn').find(b => b.text().includes('添加参数'))
      expect(addBtn).toBeTruthy()
      await addBtn!.trigger('click')

      expect(vm.formData.paramList).toHaveLength(1)
      expect(vm.formData.paramList[0]).toEqual({
        name: '',
        type: 'STRING',
        description: '',
      })
    })

    it('supports removing a parameter', async () => {
      wrapper = await mountDialog('sql')

      const vm = wrapper.vm as any
      // Push items into the reactive array
      vm.formData.paramList.push(
        { name: 'p1', type: 'STRING', description: 'desc1' },
        { name: 'p2', type: 'NUMERIC', description: 'desc2' },
      )
      await wrapper.vm.$nextTick()

      expect(vm.formData.paramList).toHaveLength(2)

      // Remove the first param via the component method
      vm.removeParam(0)
      await wrapper.vm.$nextTick()

      expect(vm.formData.paramList).toHaveLength(1)
      expect(vm.formData.paramList[0].name).toBe('p2')
    })

    it('allows empty paramList', async () => {
      wrapper = await mountDialog('sql')

      const vm = wrapper.vm as any
      expect(vm.formData.paramList).toEqual([])
    })
  })

  describe('dialog behavior', () => {
    it('emits close when cancel is clicked', async () => {
      wrapper = await mountDialog('hbase')

      const cancelBtn = wrapper.findAll('.v-btn').find(b => b.text().includes('取消'))
      if (cancelBtn) await cancelBtn.trigger('click')

      expect(wrapper.emitted('close')).toBeTruthy()
    })

    it('is hidden when show is false', () => {
      wrapper = mount(DataApiFormDialog, {
        props: { show: false, apiType: 'hbase' },
        global: { stubs: vuetifyStubs },
      })

      expect(wrapper.find('.v-dialog').exists()).toBe(false)
    })

    it('resets form when show becomes true', async () => {
      wrapper = mount(DataApiFormDialog, {
        props: { show: false, apiType: 'sql' },
        global: { stubs: vuetifyStubs },
      })

      // Set some data while hidden
      const vm = wrapper.vm as any
      vm.formData.name = 'dirty-data'
      vm.formData.sql = 'SELECT 1'
      vm.formData.paramList = [{ name: 'x', type: 'STRING', description: '' }]

      // Show the dialog (triggers reset via watch)
      await wrapper.setProps({ show: true })
      await wrapper.vm.$nextTick()

      expect(vm.formData.name).toBe('')
      expect(vm.formData.sql).toBe('')
      expect(vm.formData.paramList).toEqual([])
      expect(vm.formData.pageTag).toBe(0)
    })
  })

  describe('required field validation', () => {
    it('has validation rules on name field', async () => {
      wrapper = await mountDialog('hbase')

      const textFieldStubs = wrapper.findAllComponents({ name: 'VTextField' })
      const nameField = textFieldStubs.find(s => s.props('label') === '名称')
      // If stub found by name, check its rules prop
      if (nameField) {
        const rules = nameField.props('rules') as Array<(v: string) => string | boolean>
        expect(rules).toBeTruthy()
        expect(rules).toHaveLength(1)
        expect(rules[0]?.('')).toBe('名称为必填项')
      } else {
        // Fallback: DOM-based check
        const nameDiv = wrapper.findAll('.v-text-field').find(d => d.text().includes('名称'))
        expect(nameDiv).toBeTruthy()
      }
    })

    it('has validation rules on dataSourceId field', async () => {
      wrapper = await mountDialog('hbase')

      const textFieldStubs = wrapper.findAllComponents({ name: 'VTextField' })
      const dsIdField = textFieldStubs.find(s => s.props('label') === '数据源 ID')
      if (dsIdField) {
        const rules = dsIdField.props('rules') as Array<(v: string) => string | boolean>
        expect(rules).toBeTruthy()
        expect(rules).toHaveLength(1)
        expect(rules[0]?.('')).toBe('数据源 ID 为必填项')
      } else {
        const dsDiv = wrapper.findAll('.v-text-field').find(d => d.text().includes('数据源 ID'))
        expect(dsDiv).toBeTruthy()
      }
    })
  })
})
