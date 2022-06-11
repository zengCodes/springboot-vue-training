import request from '@/utils/request'

const commonPrefix = `/monitor/logininfor/`

// 查询登录日志列表
export function list(query) {
  return request({
    url: `${commonPrefix}list`,
    method: 'get',
    params: query,
  })
}

// 删除登录日志
export function delLogininfor(infoId) {
  return request({
    url: `${commonPrefix}${infoId}`,
    method: 'delete',
  })
}

// 清空登录日志
export function cleanLogininfor() {
  return request({
    url: `${commonPrefix}clean`,
    method: 'delete',
  })
}

// 导出登录日志
export function exportLogininfor(query) {
  return request({
    url: `${commonPrefix}export`,
    method: 'get',
    params: query,
  })
}

// 获取登入报表
export function getLoginReport(data) {
  return request({
    url: `${commonPrefix}loginReport`,
    method: 'post',
    data: data,
  })
}
