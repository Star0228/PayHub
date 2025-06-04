import request from '@/utils/request'

// 存款管理相关API

/**
 * 创建存款
 * @param {object} data - 存款数据
 * Expected fields: accountId, type, amount
 */
export function createDeposit(data) {
  return request({
    url: 'http://localhost:8080/deposit/create',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * 查询存款记录
 * @param {object} data - 查询参数
 * Expected fields: accountId
 */
export function queryDeposits(data) {
  return request({
    url: 'http://localhost:8080/deposit/list',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * 检查取款资格
 * @param {object} data - 检查参数
 * Expected fields: depositId
 */
export function checkWithdrawEligibility(data) {
  return request({
    url: 'http://localhost:8080/deposit/can-withdraw',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * 执行取款
 * @param {object} data - 取款参数
 * Expected fields: depositId
 */
export function withdrawDeposit(data) {
  return request({
    url: 'http://localhost:8080/deposit/withdraw',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

// 转账管理相关API

/**
 * 转账
 * @param {object} data - 转账数据
 * Expected fields: fromAccountId, toAccountId, amount, currency?, description?
 */
export function transferMoney(data) {
  return request({
    url: 'http://localhost:8080/transaction/transfer',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * 查询账户余额
 * @param {number} accountId - 账户ID
 * @param {string} currency - 币种，默认CNY
 */
export function queryBalance(accountId, currency = 'CNY') {
  return request({
    url: `http://localhost:8080/transaction/balance/${accountId}`,
    method: 'get',
    params: { currency }
  })
}

/**
 * 查询交易记录
 * @param {number} accountId - 账户ID
 */
export function queryTransactionRecords(accountId) {
  return request({
    url: `http://localhost:8080/transaction/records/${accountId}`,
    method: 'get'
  })
}

/**
 * 按时间范围查询交易记录（管理员）
 * @param {string} startTime - 开始时间
 * @param {string} endTime - 结束时间
 */
export function queryTransactionsByRange(startTime, endTime) {
  return request({
    url: 'http://localhost:8080/transaction/records-range',
    method: 'get',
    params: { startTime, endTime }
  })
}
