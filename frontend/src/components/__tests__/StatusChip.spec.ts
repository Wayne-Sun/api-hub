import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import StatusChip from '../StatusChip.vue'

describe('StatusChip', () => {
  const stubs = {
    'v-chip': { template: '<div class="v-chip" :color="$attrs.color"><slot /></div>' },
  }

  it('renders "已启用" with green color when enabled is true', () => {
    const wrapper = mount(StatusChip, {
      props: { enabled: true },
      global: { stubs },
    })
    expect(wrapper.text()).toContain('已启用')
    expect(wrapper.find('.v-chip').attributes('color')).toBe('green')
  })

  it('renders "已禁用" with red color when enabled is false', () => {
    const wrapper = mount(StatusChip, {
      props: { enabled: false },
      global: { stubs },
    })
    expect(wrapper.text()).toContain('已禁用')
    expect(wrapper.find('.v-chip').attributes('color')).toBe('red')
  })
})
