import request from '@/utils/request'

const commonPrefix = `/monitor/logininfor/`

// 查询登录日志列表
export function getListLog(query) {
  return request({
    url: `${commonPrefix}list`,
    method: 'get',
    params: query,
  })
}

// 删除登录日志
export function delLoginLog(infoId) {
  return request({
    url: `${commonPrefix}${infoId}`,
    method: 'delete',
  })
}

// 清空登录日志
export function cleanLoginLog() {
  return request({
    url: `${commonPrefix}clean`,
    method: 'delete',
  })
}

// 导出登录日志
export function exportLoginLog(query) {
  return request({
    url: `${commonPrefix}export`,
    method: 'get',
    params: query,
  })
}

// 获取登入报表
export function getLoginReport(data) {
  return new Promise((resolve, reject) => {
    resolve(
      request({
        url: `${commonPrefix}loginReport`,
        method: 'post',
        data: data,
      })
    )
  })
}
