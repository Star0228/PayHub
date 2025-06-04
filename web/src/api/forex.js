import request from '@/utils/request'

/**
 * 查询指定货币对的当前汇率
 * @param {string} currencyPair - 货币对，例如 "USD/CNY"
 */
export function getExchangeRate(currencyPair) {
  return request({
    url: `http://localhost:8080/api/forex/rate?currencyPair=${currencyPair}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  })
}

/**
 * 查询所有可用货币对的汇率
 */
export function getAllExchangeRates() {
  return request({
    url: 'http://localhost:8080/api/forex/rates',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  })
}

/**
 * 执行外汇买卖交易
 * @param {object} data - 外汇交易数据
 * Expected fields: accountId, currencyPair, amount, type
 */
export function tradeForex(data) {
  return request({
    url: 'http://localhost:8080/api/forex/trade',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

/**
 * 查询用户的外汇交易历史记录
 * @param {string} accountId - 用户账户ID
 */
export function getForexTrades(accountId) {
  return request({
    url: `http://localhost:8080/api/forex/trades/${accountId}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  })
}

/**
 * 根据交易ID查询具体交易详情
 * @param {string} tradeId - 外汇交易ID
 */
export function getForexTradeDetails(tradeId) {
  return request({
    url: `http://localhost:8080/api/forex/trade/${tradeId}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  })
}