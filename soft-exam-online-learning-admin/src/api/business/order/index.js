import request from '@/utils/request'

const commonPrefix = `/business/order/`
// 查询数据列表
export function getOrderList(params) {
  return request({
    url: `${commonPrefix}getOrderList`,
    method: 'get',
    params,
  })
}

// 查询详细信息
export function getOrderInfo(id) {
  return request({
    url: `${commonPrefix}getOrderInfo/` + id,
    method: 'get',
  })
}

// 删除数据
export function delOrder(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}

export function getOrderInfoByNo(params) {
  return request({
    url: `${commonPrefix}getOrderInfoByNo/` + params,
    method: 'get',
    params,
  })
}
