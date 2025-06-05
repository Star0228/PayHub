import {
  asyncRoutes,
  constantRoutes
} from '@/router'

/**
 * Use meta.role to determine if the current user has permission
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    const hasAccess = roles.some(role => route.meta.roles.includes(role));
    console.log(`权限检查 - 路由: ${route.path || route.name}, 需要角色: ${route.meta.roles}, 用户角色: ${roles}, 有权限: ${hasAccess}`);
    return hasAccess;
  } else {
    console.log(`权限检查 - 路由: ${route.path || route.name}, 无角色限制, 允许访问`);
    return true;
  }
}

/**
 * Filter asynchronous routing tables by recursion
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []

  routes.forEach(route => {
    const tmp = {
      ...route
    }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({
    commit
  }, roles) {
    return new Promise(resolve => {
      console.log('permission store generateRoutes - 输入角色:', roles);
      console.log('permission store generateRoutes - asyncRoutes:', asyncRoutes);
      
      var accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
      console.log('permission store generateRoutes - 过滤后的路由:', accessedRoutes);
      
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
