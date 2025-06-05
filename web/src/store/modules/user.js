import {
  queryUser
} from '@/api/account'
import {
  getToken,
  setToken,
  removeToken
} from '@/utils/auth'
import {
  resetRouter
} from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    userId: '',
    userName: '',
    roles: []
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USERID: (state, userId) => {
    state.userId = userId
  },
  SET_USERNAME: (state, userName) => {
    state.userName = userName
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  login({ commit }, payload) { // payload 现在是 { token, accountId, username, userFlag }
    return new Promise((resolve, reject) => {
      if (!payload || !payload.token) {
        // 如果没有token，则这是一个无效的登录尝试
        console.error('Vuex login action: No token provided in payload.');
        reject(new Error('无法完成登录：缺少认证Token。'));
        return;
      }

      // 1. 提交并存储真正的JWT Token
      commit('SET_TOKEN', payload.token);
      setToken(payload.token); // utils/auth.js 中的函数，用于持久化存储Token

      // 2. 存储用户ID和用户名 (如果 payload 中有提供)
      if (payload.accountId) {
        commit('SET_USERID', payload.accountId);
      }
      if (payload.username) {
        commit('SET_USERNAME', payload.username);
      }

      // 3. 设置用户角色并确定跳转路径
      let userRoles = ['editor']; // 默认角色
      let redirectPath = '/deposit'; // 默认跳转路径

      if (payload.userFlag) {
        console.log('设置用户角色，userFlag:', payload.userFlag);
        if (payload.userFlag === 1) {
          userRoles = ['editor']; // 普通用户
          redirectPath = '/deposit'; // 跳转到储蓄卡管理
          console.log('角色设置为普通用户: editor');
        } else if (payload.userFlag === 2) {
          userRoles = ['admin']; // 管理员
          redirectPath = '/deposit'; // 管理员也先跳转到储蓄卡管理
          console.log('角色设置为管理员: admin');
        }
      } else {
        // 如果没有提供userFlag，设置默认角色
        userRoles = ['editor'];
        console.log('未提供userFlag，设置默认角色: editor');
      }

      commit('SET_ROLES', userRoles);

      // 4. 打印最终状态用于调试
      console.log('登录完成后的用户状态:', {
        token: payload.token,
        userId: payload.accountId,
        username: payload.username,
        roles: userRoles,
        redirectPath: redirectPath
      });

      console.log('登录成功，准备跳转到:', redirectPath);
      resolve({ redirectPath, needRouteGeneration: true }); // 返回对象，标记需要生成路由
    });
  },

  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      try {
        if (!state.userId) {
          return reject(new Error('getInfo: User ID not available in state.'));
        }
        
        // 如果已经有角色信息，直接返回roles数组
        if (state.roles && state.roles.length > 0) {
          console.log('用户信息已存在:', {
            userId: state.userId,
            userName: state.userName,
            roles: state.roles
          });
          resolve(state.roles); // 直接返回roles数组
          return;
        }

        // 如果没有角色信息，设置默认角色
        const defaultRoles = ['editor'];
        commit('SET_ROLES', defaultRoles);
        
        console.log('设置默认角色:', defaultRoles);
        resolve(defaultRoles); // 返回roles数组
      } catch (error) {
        console.error('获取用户信息失败:', error);
        commit('RESET_STATE'); // 获取用户信息失败，可能token无效，重置状态
        removeToken();
        reject(error);
      }
    });
  },

  logout({ commit }) {
    return new Promise(resolve => {
      console.log('用户登出');
      removeToken()
      resetRouter()
      commit('RESET_STATE')
      resolve()
    })
  },

  resetToken({ commit }) {
    return new Promise(resolve => {
      console.log('重置用户token');
      removeToken()
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
