import axios from 'axios'
import {
  MessageBox,
  Message
} from 'element-ui'
import { getToken } from '@/utils/auth'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      // 如果token是对象，提取token字符串
      const tokenString = typeof token === 'object' ? token.token : token
      config.headers['Authorization'] = tokenString
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      MessageBox.alert('服务器开小差了', 'error', {
        confirmButtonText: '确定',
        type: 'warning'
      })
      return Promise.reject(new Error(res.msg || 'Error'))
    } else {
      return res.data
    }
  },
  error => {
    if (error.response === undefined) {
      Message({
        message: '请求失败 ' + error.message,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(error)
    } else {
      Message({
        message: '失败 ' + error.response.data.data,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(error.response)
    }
  }
)

export default service
