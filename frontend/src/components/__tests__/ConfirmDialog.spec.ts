import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ConfirmDialog from '../ConfirmDialog.vue'

vi.mock('naive-ui', async (importOriginal) => {
  const mod = await importOriginal<Record<string, unknown>>()
  return {
    ...mod,
    NModal: { template: '<div v-if="$attrs.show" class="n-modal"><slot /></div>' },
    NCard: { template: '<div class="n-card">{{ $attrs.title }}<slot /><slot name="footer" /></div>' },
    NButton: {
      template: '<button class="n-button" @click="$emit(\'click\')"><slot /></button>',
      emits: ['click'],
    },
  }
})

describe('ConfirmDialog', () => {
  it('renders title and message', () => {
    const wrapper = mount(ConfirmDialog, {
      props: {
        show: true,
        title: '确认删除',
        message: '确定要删除吗？',
      },
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
    })
    const confirmBtn = wrapper.findAll('button').find((btn) => btn.text() === '确认')
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
    })
    const cancelBtn = wrapper.findAll('button').find((btn) => btn.text() === '取消')
    await cancelBtn?.trigger('click')
    expect(wrapper.emitted('cancel')).toBeTruthy()
  })
})
