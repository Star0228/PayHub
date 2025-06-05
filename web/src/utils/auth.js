import Cookies from 'js-cookie'

const TokenKey = 'user_id_token'

export function getToken() {
  const token = Cookies.get(TokenKey)
  // 如果token是JSON字符串，解析它并返回token字段
  try {
    const parsedToken = JSON.parse(token)
    return parsedToken.token || token
  } catch (e) {
    return token
  }
}

export function setToken(token) {
  // 如果token是对象，只存储token字符串
  const tokenString = typeof token === 'object' ? token.token : token
  return Cookies.set(TokenKey, tokenString)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}
