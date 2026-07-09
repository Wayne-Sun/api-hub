import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useDatasourceStore } from '@/stores/datasource'
import DataSourceFormDialog from '../DataSourceFormDialog.vue'

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
      expose({
        validate: () => {
          // Reject if model has no 'name' field or it's empty (simulates required validation)
          if (!props.model || !props.model.name) {
            return Promise.reject(new Error('name is required'))
          }
          return Promise.resolve()
        },
      })
      return () => h('form', { class: 'n-form' }, [
        ...(slots.default ? slots.default() : []),
      ])
    },
  })

  return { ...(mod as Record<string, unknown>), NModal: NModalStub, NForm: NFormStub }
})

// Mock the API module
vi.mock('@/api/datasource', () => ({
  insertHbaseSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  insertSolrSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  insertSqlSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  listHbaseSources: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  listSolrSources: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  listSqlSources: vi.fn().mockResolvedValue({ data: { data: { list: [], total: 0 } } }),
  enableHbaseSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  enableSolrSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  enableSqlSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  disableHbaseSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  disableSolrSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
  disableSqlSource: vi.fn().mockResolvedValue({ data: { code: 200 } }),
}))

/**
 * Mount dialog with show:false then switch to show:true.
 * This ensures the `watch` handler fires and initialises formData keys.
 */
async function mountSourceDialog(sourceType: 'hbase' | 'solr' | 'sql') {
  const w = mount(DataSourceFormDialog, {
    props: { show: false, sourceType },
  })
  await w.setProps({ show: true })
  await w.vm.$nextTick()
  return w
}

describe('DataSourceFormDialog.vue', () => {
  let wrapper: VueWrapper

  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('shows HBase form when sourceType is hbase', () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'hbase' },
    })
    expect(wrapper.text()).toContain('HBase')
    expect(wrapper.text()).toContain('HBase 配置路径')
    expect(wrapper.text()).toContain('Core 配置路径')
  })

  it('shows Solr form when sourceType is solr', () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'solr' },
    })
    expect(wrapper.text()).toContain('Solr')
    expect(wrapper.text()).toContain('ZooKeeper 地址')
    expect(wrapper.text()).toContain('Chroot 路径')
  })

  it('shows SQL form when sourceType is sql', () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'sql' },
    })
    expect(wrapper.text()).toContain('SQL')
    expect(wrapper.text()).toContain('数据库类型')
    expect(wrapper.text()).toContain('连接地址')
    expect(wrapper.text()).toContain('用户名')
    expect(wrapper.text()).toContain('密码')
  })

  it('emits close when cancel is clicked', async () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'hbase' },
    })
    const cancelBtn = wrapper.findAll('button').find(b => b.text().includes('取消'))
    if (cancelBtn) await cancelBtn.trigger('click')
    expect(wrapper.emitted('close')).toBeTruthy()
  })

  it('calls store.createSource on submit', async () => {
    const store = useDatasourceStore()
    const createSpy = vi.spyOn(store, 'createSource').mockResolvedValue()

    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'hbase' },
    })

    const vm = wrapper.vm as any
    vm.formData.name = 'test-source'

    const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
    if (confirmBtn) await confirmBtn.trigger('click')

    expect(createSpy).toHaveBeenCalled()
  })

  it('passes correct HBase payload on submit', async () => {
    const store = useDatasourceStore()
    const createSpy = vi.spyOn(store, 'createSource').mockResolvedValue()

    wrapper = await mountSourceDialog('hbase')

    const vm = wrapper.vm as any
    vm.formData.name = 'hbase-source'
    vm.formData.hbaseSitePath = '/etc/hbase/conf/hbase-site.xml'
    vm.formData.coreSitePath = '/etc/hadoop/conf/core-site.xml'

    await wrapper.vm.$nextTick()

    const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
    if (confirmBtn) await confirmBtn.trigger('click')

    expect(createSpy).toHaveBeenCalledWith('hbase', {
      name: 'hbase-source',
      comments: '',
      hbaseSitePath: '/etc/hbase/conf/hbase-site.xml',
      coreSitePath: '/etc/hadoop/conf/core-site.xml',
    })
  })

  it('passes correct Solr payload on submit', async () => {
    const store = useDatasourceStore()
    const createSpy = vi.spyOn(store, 'createSource').mockResolvedValue()

    wrapper = await mountSourceDialog('solr')

    const vm = wrapper.vm as any
    vm.formData.name = 'solr-source'
    vm.formData.zkHosts = 'zk1:2181,zk2:2181'
    vm.formData.zkChroot = '/solr'

    await wrapper.vm.$nextTick()

    const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
    if (confirmBtn) await confirmBtn.trigger('click')

    expect(createSpy).toHaveBeenCalledWith('solr', {
      name: 'solr-source',
      comments: '',
      zkHosts: 'zk1:2181,zk2:2181',
      zkChroot: '/solr',
    })
  })

  it('passes correct SQL payload on submit', async () => {
    const store = useDatasourceStore()
    const createSpy = vi.spyOn(store, 'createSource').mockResolvedValue()

    wrapper = await mountSourceDialog('sql')

    const vm = wrapper.vm as any
    vm.formData.name = 'sql-source'
    vm.formData.dialect = 'MYSQL'
    vm.formData.url = 'jdbc:mysql://localhost:3306/testdb'
    vm.formData.username = 'admin'
    vm.formData.password = 'secret'

    await wrapper.vm.$nextTick()

    const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
    if (confirmBtn) await confirmBtn.trigger('click')

    expect(createSpy).toHaveBeenCalledWith('sql', {
      name: 'sql-source',
      comments: '',
      dialect: 'MYSQL',
      url: 'jdbc:mysql://localhost:3306/testdb',
      username: 'admin',
      password: 'secret',
    })
  })

  it('resets form when show becomes true', async () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: false, sourceType: 'sql' },
    })

    const vm = wrapper.vm as any
    vm.formData.name = 'dirty'
    vm.formData.dialect = 'ORACLE'
    vm.formData.url = 'old-url'
    vm.formData.username = 'old-user'
    vm.formData.password = 'old-pass'

    await wrapper.setProps({ show: true })
    await wrapper.vm.$nextTick()

    expect(vm.formData.name).toBe('')
    expect(vm.formData.comments).toBe('')
    expect(vm.formData.dialect).toBe('MYSQL')
    expect(vm.formData.url).toBe('')
    expect(vm.formData.username).toBe('')
    expect(vm.formData.password).toBe('')
  })

  it('has validation rules on name field', async () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'hbase' },
    })
    const form = wrapper.findComponent({ name: 'NForm' })
    const rules = form.props('rules') as Record<string, any>
    expect(rules).toBeTruthy()
    expect(rules.name).toBeTruthy()
    expect(rules.name[0]?.required).toBe(true)
    expect(rules.name[0]?.message).toBe('名称为必填项')
  })

  it('validates name is required on submit', async () => {
    const store = useDatasourceStore()
    const createSpy = vi.spyOn(store, 'createSource').mockResolvedValue()

    // Mount with show transition so the watch fires and sets formData.name = ''
    wrapper = await mountSourceDialog('hbase')

    // formData.name is empty after reset — validation should prevent submission
    const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
    if (confirmBtn) await confirmBtn.trigger('click')

    expect(createSpy).not.toHaveBeenCalled()
  })

  it('does not show fields from other source types', () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'sql' },
    })
    expect(wrapper.text()).not.toContain('HBase 配置路径')
    expect(wrapper.text()).not.toContain('Core 配置路径')
    expect(wrapper.text()).not.toContain('ZooKeeper 地址')
    expect(wrapper.text()).not.toContain('Chroot 路径')
  })
})
