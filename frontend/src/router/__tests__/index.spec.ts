import { describe, it, expect } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'

describe('router', () => {
  const router = createRouter({
    history: createWebHistory(),
    routes: [
      { path: '/', redirect: '/datasource' },
      {
        path: '/datasource',
        name: 'datasource',
        component: { template: '<div></div>' },
      },
      {
        path: '/dataapi',
        name: 'dataapi',
        component: { template: '<div></div>' },
      },
    ],
  })

  it('has datasource route', () => {
    const route = router.resolve('/datasource')
    expect(route.name).toBe('datasource')
  })

  it('has dataapi route', () => {
    const route = router.resolve('/dataapi')
    expect(route.name).toBe('dataapi')
  })

  it('redirects root to /datasource', () => {
    const rootRoute = router.getRoutes().find((r) => r.path === '/')
    expect(rootRoute?.redirect).toBe('/datasource')
  })
})
