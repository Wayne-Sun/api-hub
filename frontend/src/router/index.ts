import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/datasource',
    },
    {
      path: '/datasource',
      name: 'datasource',
      component: () => import('../views/datasource/DataSourceView.vue'),
    },
    {
      path: '/dataapi',
      name: 'dataapi',
      component: () => import('../views/dataapi/DataApiView.vue'),
    },
  ],
})

export default router
