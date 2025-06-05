import request from '@/utils/request'

/**
 * 用户申请贷款
 * @param {object} data - 贷款申请数据
 * Expected fields: accountId, amountOfMoney, applicationDate, term, purpose, creditScore?
 */
export function applyLoan(data) {
  return request({
    url: 'http://localhost:8080/api/loan/apply',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * 审批贷款申请
 * @param {object} data - 审批数据
 * Expected fields: applicationId, examinerId, result
 */
export function approveLoan(data) {
  return request({
    url: 'http://localhost:8080/api/loan/approve',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * 用户进行贷款还款
 * @param {object} data - 还款数据
 * Expected fields: loanId, amount
 */
export function repayLoan(data) {
  return request({
    url: 'http://localhost:8080/api/loan/repay',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * 查询用户贷款申请记录
 * @param {string} accountId - 用户账户ID
 */
export function getLoanApplications(accountId) {
  return request({
    url: `http://localhost:8080/api/loan/applications/${accountId}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  })
}

/**
 * 查询用户贷款记录
 * @param {string} accountId - 用户账户ID
 */
export function getLoans(accountId) {
  return request({
    url: `http://localhost:8080/api/loan/loans/${accountId}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  })
}

/**
 * 手动触发逾期检查（管理员功能）
 */
export function checkOverdue() {
  return request({
    url: 'http://localhost:8080/api/loan/check-overdue',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post'
  })
}

