import request from '@/utils/request'

const commonPrefix = `/monitor/operlog/`

// 查询操作日志列表
export function getListOpenLog(query) {
  return request({
    url: `${commonPrefix}list`,
    method: 'get',
    params: query,
  })
}

// 删除操作日志
export function delOpenLog(operId) {
  return request({
    url: `${commonPrefix}${operId}`,
    method: 'delete',
  })
}

// 清空操作日志
export function cleanOpenLog() {
  return request({
    url: `${commonPrefix}clean`,
    method: 'delete',
  })
}

// 导出操作日志
export function exportOpenLog(query) {
  return request({
    url: `${commonPrefix}export`,
    method: 'get',
    params: query,
  })
}
