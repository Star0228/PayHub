import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/deposit',
    children: [
      {
        path: 'deposit',
        name: 'Deposit',
        component: () => import('@/views/deposit/list/index'),
        meta: {
          title: '储蓄卡管理',
          icon: 'xj'
        }
      }]
    },
    {
      path: '/',
      component: Layout,
      redirect: '/loan',
      children: [{
        path: 'loan',
        name: 'Loan',
        component: () => import('@/views/loan/all/index'),
        meta: {
          title: '贷款管理',
          icon: 'new2'
        }
      }
      ]
    },
    {
      path: '/',
      component: Layout,
      redirect: '/foreign',
      children: [{
        path: 'foreign',
        name: 'Foreign',
        component: () => import('@/views/foreign/all/index'),
        meta: {
          title: '外汇管理',
          icon: 'pen'
        }
      }
      ]
    },
    {
      path: '/',
      component: Layout,
      redirect: '/credit-card',
      children: [
        {
          path: 'index',
          name: 'CreditCard',
          component: () => import('@/views/credit-card/index'),
          meta: { title: '信用卡管理', icon: 'new5' }
        }
      ]
    }
  // ,
  // {
  //   path: '/',
  //   component: Layout,
  //   redirect: '/record',
  //   children: [{
  //     path: 'record',
  //     name: 'Record',
  //     component: () => import('@/views/record/all/index'),
  //     meta: {
  //       title: '个性化记录',
  //       icon: 'new3'
  //     }
  //   }]
  // }
]

export const asyncRoutes = [
  {
    path: '*',
    redirect: '/404',
    hidden: true
  }
]


const createRouter = () => new Router({
  base: '/',
  // mode: 'history', // require service support
  scrollBehavior: () => ({
    y: 0
  }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
