import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import EmptyState from '../EmptyState.vue'

vi.mock('naive-ui', async (importOriginal) => {
  const mod = await importOriginal<Record<string, unknown>>()
  return {
    ...mod,
    NIcon: { template: '<span class="n-icon"><slot /></span>' },
  }
})

describe('EmptyState', () => {
  it('renders the message', () => {
    const wrapper = mount(EmptyState, {
      props: { message: '暂无数据' },
    })
    expect(wrapper.text()).toContain('暂无数据')
  })
})
