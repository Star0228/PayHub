import request from '@/utils/request'

// queryAllUsers is no longer used by the updated login form.
// If needed for other parts of your application, it should be verified against available backend APIs.
// export function queryAllUsers() { ... }

// queryUser does not have an equivalent in the new "Account Management API" docs provided.
// export function queryUser(data) { ... }

/**
 * Registers a new account.
 * @param {object} data - Registration data.
 * Expected fields: username, password, email, address?, gender?, occupation?, phoneNumber?, annualIncome?, userFlag?
 */
export function registerUser(data) {
  return request({
    url: 'http://localhost:8080/account/register', // UPDATED URL
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * Logs in an account.
 * @param {object} data - Login credentials.
 * Expected fields: accountId, username, password
 */
export function loginUser(data) {
  return request({
    url: 'http://localhost:8080/account/login', // UPDATED URL
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * Resets account password.
 * @param {object} data - Reset password data.
 * Expected fields: username, password (the new password)
 */
export function resetPassword(data) {
  return request({
    url: 'http://localhost:8080/account/reset-password', // UPDATED URL
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * Sends verification code to email
 * @param {string} email - Email address to send verification code to
 * @param {string} type - Verification code type (optional, defaults to 'REGISTRATION')
 */
export function sendCode(email, type = 'REGISTRATION') {
  return request({
    url: 'http://localhost:8080/vcode/send', // 根据您的后端日志，使用正确的接口
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data: {
      email: email,
      type: type // REGISTRATION, LOGIN, RESET_PASSWORD 等
    }
  })
}

/**
 * Verifies the verification code
 * @param {string} email - Email address
 * @param {string} code - Verification code
 * @param {string} type - Verification code type
 */
export function verifyCode(email, code, type = 'REGISTRATION') {
  return request({
    url: 'http://localhost:8080/vcode/verify', // 验证验证码的接口
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data: {
      email: email,
      code: code,
      type: type
    }
  })
}

// 保留原有的 queryAllUsers 函数，如果其他地方需要使用
export function queryAllUsers() {
  return request({
    url: 'http://localhost:8080/account/users', // 根据需要调整URL
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  })
}