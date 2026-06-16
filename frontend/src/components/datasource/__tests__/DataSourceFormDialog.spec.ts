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
      expose({ validate: () => Promise.resolve() })
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

    const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('确认'))
    if (confirmBtn) await confirmBtn.trigger('click')

    expect(createSpy).toHaveBeenCalled()
  })
})
