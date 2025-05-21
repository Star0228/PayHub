import request from '@/utils/request'

// queryAllUsers is no longer used by the updated login form.
// If needed for other parts of your application, it should be verified against available backend APIs.
// export function queryAllUsers() { ... }

// queryUser does not have an equivalent in the new "Account Management API" docs provided.
// export function queryUser(data) { ... }

/**
 * Registers a new account.
 * @param {object} data - Registration data.
 * Expected fields: username, password, email, address?, gender?, occupation?, phoneNumber?, annualIncome?
 */
export function registerUser(data) {
  return request({
    url: 'http://localhost:8080/user/register', // UPDATED URL
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
    url: 'http://localhost:8080/user/login', // UPDATED URL
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
    url: 'http://localhost:8080/user/reset-password', // UPDATED URL
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

// The 'sendCode' function's endpoint 'http://localhost:8080/api/user/sendver?email=${email}'
// is not part of the new "Account Management API" documentation for register, login, or reset flows.
// The registration API (/account/register) does not take a verificationCode.
// The reset password API (/account/reset-password) does not take an email or verificationCode.
// If email verification is still a required separate step for any process,
// the backend API endpoint and parameters for it need to be clarified and updated here.
// Based strictly on the provided new API docs, this function might be obsolete for these flows.
/*
export function sendCode(email) {
  return request({
    url: `http://localhost:8080/email/sendver?email=${email}`, // This URL might be from an old API
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post'
  })
}
*/