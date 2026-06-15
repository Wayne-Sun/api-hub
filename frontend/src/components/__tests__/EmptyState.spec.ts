import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import EmptyState from '../EmptyState.vue'

describe('EmptyState', () => {
  const stubs = {
    'v-icon': { template: '<span class="v-icon" :icon="$attrs.icon"><slot /></span>' },
  }

  it('renders the message', () => {
    const wrapper = mount(EmptyState, {
      props: { message: '暂无数据' },
      global: { stubs },
    })
    expect(wrapper.text()).toContain('暂无数据')
  })

  it('renders default icon when none provided', () => {
    const wrapper = mount(EmptyState, {
      props: { message: '空的' },
      global: { stubs },
    })
    expect(wrapper.find('.v-icon').attributes('icon')).toBe('mdi-inbox-outline')
  })

  it('renders custom icon when provided', () => {
    const wrapper = mount(EmptyState, {
      props: { message: '空的', icon: 'mdi-alert' },
      global: { stubs },
    })
    expect(wrapper.find('.v-icon').attributes('icon')).toBe('mdi-alert')
  })
})
