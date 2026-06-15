import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ConfirmDialog from '../ConfirmDialog.vue'

describe('ConfirmDialog', () => {
  const stubs = {
    'v-dialog': { template: '<div v-if="$attrs[\'model-value\']" class="v-dialog"><slot /></div>' },
    'v-card': { template: '<div class="v-card"><slot /></div>' },
    'v-card-title': { template: '<div class="v-card-title"><slot /></div>' },
    'v-card-text': { template: '<div class="v-card-text"><slot /></div>' },
    'v-card-actions': { template: '<div class="v-card-actions"><slot /></div>' },
    'v-spacer': { template: '<span class="v-spacer" />' },
    'v-btn': {
      template: '<button class="v-btn" @click="$emit(\'click\')"><slot /></button>',
      emits: ['click'],
    },
  }

  it('renders title and message', () => {
    const wrapper = mount(ConfirmDialog, {
      props: {
        show: true,
        title: '确认删除',
        message: '确定要删除吗？',
      },
      global: { stubs },
    })
    expect(wrapper.text()).toContain('确认删除')
    expect(wrapper.text()).toContain('确定要删除吗？')
  })

  it('shows custom confirm text when provided', () => {
    const wrapper = mount(ConfirmDialog, {
      props: {
        show: true,
        title: '确认',
        message: 'test',
        confirmText: '确定',
      },
      global: { stubs },
    })
    expect(wrapper.text()).toContain('确定')
  })

  it('emits confirm when confirm button is clicked', async () => {
    const wrapper = mount(ConfirmDialog, {
      props: {
        show: true,
        title: '确认',
        message: 'test',
      },
      global: { stubs },
    })
    const confirmBtn = wrapper.findAll('.v-btn').find((btn) => btn.text() === '确认')
    await confirmBtn?.trigger('click')
    expect(wrapper.emitted('confirm')).toBeTruthy()
  })

  it('emits cancel when cancel button is clicked', async () => {
    const wrapper = mount(ConfirmDialog, {
      props: {
        show: true,
        title: '确认',
        message: 'test',
      },
      global: { stubs },
    })
    const cancelBtn = wrapper.findAll('.v-btn').find((btn) => btn.text() === '取消')
    await cancelBtn?.trigger('click')
    expect(wrapper.emitted('cancel')).toBeTruthy()
  })
})
