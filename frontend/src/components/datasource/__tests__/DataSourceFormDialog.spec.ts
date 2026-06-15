import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { useDatasourceStore } from '@/stores/datasource'
import DataSourceFormDialog from '../DataSourceFormDialog.vue'

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

const vuetifyStubs = {
  'v-dialog': { template: '<div class="v-dialog" v-if="modelValue"><slot /></div>', props: ['modelValue'] },
  'v-card': { template: '<div class="v-card"><slot /></div>' },
  'v-card-title': { template: '<div class="v-card-title"><slot /></div>' },
  'v-card-text': { template: '<div class="v-card-text"><slot /></div>' },
  'v-card-actions': { template: '<div class="v-card-actions"><slot /></div>' },
  'v-form': { template: '<div class="v-form"><slot /></div>', props: ['ref'] },
  'v-text-field': { template: '<div class="v-text-field"><span class="v-label">{{ label }}</span><slot /></div>', props: ['modelValue', 'label', 'rules', 'type'], emits: ['update:modelValue'] },
  'v-select': { template: '<div class="v-select"><span class="v-label">{{ label }}</span><slot /></div>', props: ['modelValue', 'label', 'items', 'rules'], emits: ['update:modelValue'] },
  'v-btn': { template: '<button class="v-btn" @click="$emit(\'click\')"><slot /></button>', emits: ['click'] },
  'v-spacer': { template: '<span class="v-spacer" />' },
}

describe('DataSourceFormDialog.vue', () => {
  let wrapper: VueWrapper

  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('shows HBase form when sourceType is hbase', () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'hbase' },
      global: { stubs: vuetifyStubs },
    })
    expect(wrapper.text()).toContain('HBase')
    expect(wrapper.text()).toContain('HBase 配置路径')
    expect(wrapper.text()).toContain('Core 配置路径')
  })

  it('shows Solr form when sourceType is solr', () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'solr' },
      global: { stubs: vuetifyStubs },
    })
    expect(wrapper.text()).toContain('Solr')
    expect(wrapper.text()).toContain('ZooKeeper 地址')
    expect(wrapper.text()).toContain('Chroot 路径')
  })

  it('shows SQL form when sourceType is sql', () => {
    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'sql' },
      global: { stubs: vuetifyStubs },
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
      global: { stubs: vuetifyStubs },
    })
    const cancelBtn = wrapper.findAll('.v-btn').find(b => b.text().includes('取消'))
    if (cancelBtn) await cancelBtn.trigger('click')
    expect(wrapper.emitted('close')).toBeTruthy()
  })

  it('calls store.createSource on submit', async () => {
    const store = useDatasourceStore()
    const createSpy = vi.spyOn(store, 'createSource').mockResolvedValue()

    wrapper = mount(DataSourceFormDialog, {
      props: { show: true, sourceType: 'hbase' },
      global: { stubs: vuetifyStubs },
    })

    const confirmBtn = wrapper.findAll('.v-btn').find(b => b.text().includes('确认'))
    if (confirmBtn) await confirmBtn.trigger('click')

    expect(createSpy).toHaveBeenCalled()
  })
})
