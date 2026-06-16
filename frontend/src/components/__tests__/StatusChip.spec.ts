import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import StatusChip from '../StatusChip.vue'

vi.mock('naive-ui', async (importOriginal) => {
  const mod = await importOriginal<Record<string, unknown>>()
  return {
    ...mod,
    NTag: { template: '<div class="n-tag" v-bind="$attrs"><slot /></div>' },
  }
})

describe('StatusChip', () => {
  it('renders "已启用" with success type when enabled is true', () => {
    const wrapper = mount(StatusChip, {
      props: { enabled: true },
    })
    expect(wrapper.text()).toContain('已启用')
    expect(wrapper.find('.n-tag').attributes('type')).toBe('success')
  })

  it('renders "已禁用" with error type when enabled is false', () => {
    const wrapper = mount(StatusChip, {
      props: { enabled: false },
    })
    expect(wrapper.text()).toContain('已禁用')
    expect(wrapper.find('.n-tag').attributes('type')).toBe('error')
  })
})
