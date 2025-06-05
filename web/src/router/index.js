import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true               
 * alwaysShow: true           
 *                            
 *                              
 * redirect: noRedirect       
 * name:'router-name'             
 * meta : {
    roles: ['admin','editor']    
    title: 'title'               
    icon: 'svg-name'            
    breadcrumb: false            
    activeMenu: '/example/list' 
  }
 */

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
  }
]

export const asyncRoutes = [
  {
    path: '/',
    component: Layout,
    redirect: '/deposit',
    children: [
      {
        path: 'deposit',
        name: 'Deposit',
        component: () => import('@/views/deposit/editor/index'),
        meta: {
          title: '储蓄卡管理',
          icon: 'xj',
          roles: ['editor'] 
        }
      }
    ]
  },
  {
    path: '/',
    component: Layout,
    redirect: '/deposit',
    children: [
      {
        path: 'deposit',
        name: 'Deposit',
        component: () => import('@/views/deposit/admin/index'),
        meta: {
          title: '储蓄卡管理',
          icon: 'xj',
          roles: ['admin'] 
        }
      }
    ]
  },
  {
    path: '/loan',
    component: Layout,
    redirect: '/loan/all',
    children: [{
      path: 'all',
      name: 'Loan',
      component: () => import('@/views/loan/editor/index'),
      meta: {
        roles: ['editor'],
        title: '贷款管理',
        icon: 'new2'
      }
    }]
  },
  {
    path: '/loan',
    component: Layout,
    redirect: '/loan/all',
    children: [{
      path: 'all',
      name: 'Loan',
      component: () => import('@/views/loan/admin/index'),
      meta: {
        roles: ['admin'],
        title: '贷款管理',
        icon: 'new2'
      }
    }]
  },
  {
    path: '/foreign',
    component: Layout,
    redirect: '/foreign',
    children: [{
      path: '',
      name: 'Foreign',
      component: () => import('@/views/foreign/admin/index'),
      meta: {
        title: '外汇管理',
        icon: 'pen',
        roles: ['admin'] 
      }
    }]
  },
  {
    path: '/foreign',
    component: Layout,
    redirect: '/foreign',
    children: [{
      path: '',
      name: 'Foreign',
      component: () => import('@/views/foreign/editor/index'),
      meta: {
        title: '外汇管理',
        icon: 'pen',
        roles: ['editor'] 
      }
    }]
  },
  {
    path: '/credit-card',
    component: Layout,
    redirect: '/credit-card',
    children: [
      {
        path: '',
        name: 'CreditCard',
        component: () => import('@/views/credit-card/editor/index'),
        meta: { 
          title: '信用卡管理', 
          icon: 'new5', 
          roles: ['editor']  
        }
      }
    ]
  },
  {
    path: '/credit-card',
    component: Layout,
    redirect: '/credit-card',
    children: [
      {
        path: '',
        name: 'CreditCard',
        component: () => import('@/views/credit-card/admin/index'),
        meta: { 
          title: '信用卡管理', 
          icon: 'new5', 
          roles: ['admin']  
        }
      }
    ]
  },
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
