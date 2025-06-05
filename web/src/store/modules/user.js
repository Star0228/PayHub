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
  login({ commit }, payload) { // payload 现在是 { token, accountId, username }
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
      
      // login action 的主要职责完成，resolve Promise
      // 它不再自己调用 queryUser
      resolve(); 
    });
  },
  getInfo({commit,  state}) {
    return new Promise((resolve, reject) => {
      if (!state.userId) {
        return reject(new Error('getInfo: User ID not available in state.'));
      }
      // queryUser({ id: state.userId }).then(response => { // response 是 queryUser 的返回
      //   if (!response || !response.id || !response.name) {
      //     return reject(new Error('getInfo: Invalid user data received.'));
      //   }
      var roles;
      // 这里的角色逻辑应该由后端返回，或者基于更可靠的字段
      if (state.id === '1') { // 假设后端可能返回 roles 数组
        roles = ['admin'];
      } else {
        roles = ['editor']; // 默认角色
      }
      commit('SET_ROLES', roles);
      commit('SET_USERID', state.userId); // 可以再次确认或更新
      commit('SET_USERNAME', state.userName);
      resolve(roles); // 返回包含角色和其他信息的数据
    }).catch(error => {
      commit('RESET_STATE'); // 获取用户信息失败，可能token无效，重置状态
      removeToken();
      reject(error);
    });
    // });
  },
  logout({
    commit
  }) {
    return new Promise(resolve => {
      removeToken()
      resetRouter()
      commit('RESET_STATE')
      resolve()
    })
  },

  resetToken({
    commit
  }) {
    return new Promise(resolve => {
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
