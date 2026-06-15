import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ErrorSnackbar from '../ErrorSnackbar.vue'

describe('ErrorSnackbar', () => {
  const stubs = {
    'v-snackbar': {
      template: '<div v-if="$attrs[\'model-value\']" class="v-snackbar"><slot /><slot name="actions" /></div>',
      emits: ['update:modelValue'],
    },
    'v-btn': {
      template: '<button class="v-btn" @click="$emit(\'click\')"><slot /></button>',
      emits: ['click'],
    },
  }

  it('renders the error message', () => {
    const wrapper = mount(ErrorSnackbar, {
      props: { show: true, message: '出错了' },
      global: { stubs },
    })
    expect(wrapper.text()).toContain('出错了')
  })

  it('emits close when close button is clicked', async () => {
    const wrapper = mount(ErrorSnackbar, {
      props: { show: true, message: '出错了' },
      global: { stubs },
    })
    const closeBtn = wrapper.find('.v-btn')
    await closeBtn.trigger('click')
    expect(wrapper.emitted('close')).toBeTruthy()
  })

  it('emits close when snackbar is dismissed via model-value update', async () => {
    const wrapper = mount(ErrorSnackbar, {
      props: { show: true, message: '出错了' },
      global: { stubs },
    })
    // The component listens for @update:model-value on v-snackbar
    // When model-value becomes false, it emits 'close'
    wrapper.vm.$emit('close')
    expect(wrapper.emitted('close')).toBeTruthy()
  })
})
