import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useDataapiStore } from '@/stores/dataapi'
import DataApiFormDialog from '../DataApiFormDialog.vue'

// Mock naive-ui components that have jsdom-specific issues:
// - NModal uses <Teleport> which teleports content outside the wrapper
// - NForm's validate() rejects with missing injection tokens in jsdom
vi.mock('naive-ui', async (importOriginal) => {
  const mod = await importOriginal()
  const { h, defineComponent } = await import('vue')

  const NModalStub = defineComponent({
    name: 'NModal',
    props: ['show', 'title'],
    setup(props: Record<string, any>, { slots }: any) {
      return () => {
        if (!props.show) return null
        return h('div', { class: 'n-modal' }, [
          props.title ? h('div', { class: 'n-modal-title' }, props.title) : null,
          ...(slots.default ? slots.default() : []),
          ...(slots.footer ? slots.footer() : []),
        ])
      }
    },
  })

  // Stub NForm so formRef.value?.validate() resolves instead of rejecting
  const NFormStub = defineComponent({
    name: 'NForm',
    props: ['model', 'rules'],
    setup(props: Record<string, any>, { expose, slots }: any) {
      expose({ validate: () => Promise.resolve() })
      return () => h('form', { class: 'n-form' }, [
        ...(slots.default ? slots.default() : []),
      ])
    },
  })

  return { ...(mod as Record<string, unknown>), NModal: NModalStub, NForm: NFormStub }
})

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

/**
 * Mount dialog with show:false then switch to show:true.
 * This ensures the `watch` handler fires and initialises formData keys.
 */
async function mountDialog(apiType: 'hbase' | 'solr' | 'sql') {
  const w = mount(DataApiFormDialog, {
    props: { show: false, apiType },
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

      const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
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

      const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
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

    it('calls store.registerApi with correct Solr payload on submit', async () => {
      const store = useDataapiStore()
      const registerSpy = vi.spyOn(store, 'registerApi')

      wrapper = await mountDialog('solr')

      const vm = wrapper.vm as any
      vm.formData.name = 'solr-api'
      vm.formData.dataSourceId = '3'
      vm.formData.collection = 'my_collection'
      vm.formData.fields = 'field1,field2'
      vm.formData.conditions = 'field1:value1'
      vm.formData.orders = 'field1 asc'

      await wrapper.vm.$nextTick()

      const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
      if (confirmBtn) await confirmBtn.trigger('click')

      expect(registerSpy).toHaveBeenCalledWith('solr', {
        name: 'solr-api',
        comments: '',
        dataSourceId: 3,
        collection: 'my_collection',
        fields: 'field1,field2',
        conditions: 'field1:value1',
        orders: 'field1 asc',
      })
    })
  })

  describe('SQL paramList operations', () => {
    it('supports adding a parameter', async () => {
      wrapper = await mountDialog('sql')

      const vm = wrapper.vm as any
      expect(vm.formData.paramList).toHaveLength(0)

      const addBtn = wrapper.findAll('button').find(b => b.text().includes('添加参数'))
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

      const cancelBtn = wrapper.findAll('button').find(b => b.text().includes('取消'))
      if (cancelBtn) await cancelBtn.trigger('click')

      expect(wrapper.emitted('close')).toBeTruthy()
    })

    it('is hidden when show is false', () => {
      wrapper = mount(DataApiFormDialog, {
        props: { show: false, apiType: 'hbase' },
      })

      expect(wrapper.find('.n-modal').exists()).toBe(false)
    })

    it('resets form when show becomes true', async () => {
      wrapper = mount(DataApiFormDialog, {
        props: { show: false, apiType: 'sql' },
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

    it('closes dialog on confirm after successful submit', async () => {
      wrapper = await mountDialog('hbase')

      const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
      if (confirmBtn) await confirmBtn.trigger('click')

      // Drain all microtasks so the full async chain (onSubmit → validate →
      // registerApi → API call → fetchApis → emit('close')) completes
      await new Promise(resolve => setTimeout(resolve, 0))

      expect(wrapper.emitted('close')).toBeTruthy()
    })
  })

  describe('required field validation', () => {
    it('has validation rules on name field', async () => {
      wrapper = await mountDialog('hbase')
      const form = wrapper.findComponent({ name: 'NForm' })
      const rules = form.props('rules') as Record<string, any>
      expect(rules).toBeTruthy()
      expect(rules.name).toBeTruthy()
      expect(rules.name[0]?.required).toBe(true)
      expect(rules.name[0]?.message).toBe('名称为必填项')
    })

    it('has validation rules on dataSourceId field', async () => {
      wrapper = await mountDialog('hbase')
      const form = wrapper.findComponent({ name: 'NForm' })
      const rules = form.props('rules') as Record<string, any>
      expect(rules).toBeTruthy()
      expect(rules.dataSourceId).toBeTruthy()
      expect(rules.dataSourceId[0]?.required).toBe(true)
      expect(rules.dataSourceId[0]?.message).toBe('数据源 ID 为必填项')
    })

    it('has validation rules on sql field for sql type', async () => {
      wrapper = await mountDialog('sql')
      const form = wrapper.findComponent({ name: 'NForm' })
      const rules = form.props('rules') as Record<string, any>
      expect(rules).toBeTruthy()
      expect(rules.sql).toBeTruthy()
      expect(rules.sql[0]?.required).toBe(true)
      expect(rules.sql[0]?.message).toBe('SQL 为必填项')
    })

    it('has validation rules on tableName field for hbase type', async () => {
      wrapper = await mountDialog('hbase')
      const form = wrapper.findComponent({ name: 'NForm' })
      const rules = form.props('rules') as Record<string, any>
      expect(rules).toBeTruthy()
      expect(rules.tableName).toBeTruthy()
      expect(rules.tableName[0]?.required).toBe(true)
      expect(rules.tableName[0]?.message).toBe('表名为必填项')
    })

    it('has validation rules on collection field for solr type', async () => {
      wrapper = await mountDialog('solr')
      const form = wrapper.findComponent({ name: 'NForm' })
      const rules = form.props('rules') as Record<string, any>
      expect(rules).toBeTruthy()
      expect(rules.collection).toBeTruthy()
      expect(rules.collection[0]?.required).toBe(true)
      expect(rules.collection[0]?.message).toBe('Collection 为必填项')
    })

    it('has validation rules on fields field for solr type', async () => {
      wrapper = await mountDialog('solr')
      const form = wrapper.findComponent({ name: 'NForm' })
      const rules = form.props('rules') as Record<string, any>
      expect(rules).toBeTruthy()
      expect(rules.fields).toBeTruthy()
      expect(rules.fields[0]?.required).toBe(true)
      expect(rules.fields[0]?.message).toBe('字段为必填项')
    })
  })
})
