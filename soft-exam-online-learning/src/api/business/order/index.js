import request from "@/utils/request";

const commonPrefix = `/business/order/`;

// 创建订单
export function createOrder(data) {
  return request({
    url: `${commonPrefix}createOrder`,
    method: "post",
    data,
  });
}

// 查询数据列表
export function getOrderList(params) {
  return request({
    url: `${commonPrefix}getOrderList`,
    method: "get",
    params,
  });
}

/**
 * 查看订单状态
 * @param {*} query
 * @returns
 */
export function getOrderStatus(courseId, memberId) {
  return request({
    url: `${commonPrefix}getOrderStatus/${courseId}/${memberId}`,
    method: "get",
  });
}

/**
 * 根据订单号查询
 * @param {*} params
 * @returns
 */
export function getOrderInfoByNo(params) {
  return request({
    url: `${commonPrefix}getOrderInfoByNo/` + params,
    method: "get",
  });
}

// 删除数据
export function delOrder(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: "delete",
  });
}

/**
 * 订单支付
 * @param {} data
 * @returns
 */
export function orderPay(data) {
  return request({
    url: `${commonPrefix}pay`,
    method: "post",
    data,
  });
}
