import request from '@/utils/request'

// 获取我的信用卡列表
export function getMyCreditCards() {
  return request({
    url: 'http://localhost:8080/credit-card/my-cards',
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'get'
  })
}

// 获取信用卡详情
export function getCreditCard(id) {
  return request({
    url: `http://localhost:8080/credit-card/${id}`,
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'get'
  })
}

// 挂失信用卡
export function reportLost(id, reason) {
  return request({
    url: `http://localhost:8080/credit-card/${id}/report-lost`,
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'post',
    data: { reason }
  })
}

// 解挂信用卡
export function cancelLost(id) {
  return request({
    url: `http://localhost:8080/credit-card/${id}/cancel-lost`,
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'post'
  })
}

// 信用卡消费
export function consume(id, data) {
  return request({
    url: `http://localhost:8080/credit-card/${id}/consume`,
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'post',
    data
  })
}

// 信用卡还款
export function repay(id, data) {
  return request({
    url: `http://localhost:8080/credit-card/${id}/repay`,
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'post',
    data
  })
}

// 获取交易记录
export function getTransactions(id) {
  return request({
    url: `http://localhost:8080/credit-card/${id}/transactions`,
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'get'
  })
}

// 创建信用卡申请
export function createApplication() {
  return request({
    url: 'http://localhost:8080/credit-card/application/create',
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'post'
  })
}

// 获取我的申请列表
export function getMyApplications() {
  return request({
    url: 'http://localhost:8080/credit-card/application/my',
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'get'
  })
}

// 获取申请详情
export function getApplication(id) {
  return request({
    url: `http://localhost:8080/credit-card/application/${id}`,
    headers: {
        'Content-Type': 'application/json'
      },
    method: 'get'
  })
}

// 获取所有申请记录（管理员）
export function getAllApplications() {
  return request({
    url: 'http://localhost:8080/credit-card/application/status/PENDING',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  })
}

// 更新申请状态（管理员）
export function updateApplicationStatus(id, status) {
  return request({
    url: 'http://localhost:8080/credit-card/application/status',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data: {
      id: id,
      status: status
    }
  })
} 