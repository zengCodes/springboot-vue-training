import request from '@/utils/request'

const commonPrefix = `/system/config/`
// 查询参数列表
export function getListConfig(query) {
  return request({
    url: `${commonPrefix}list`,
    method: 'get',
    params: query,
  })
}

// 查询参数详细
export function getConfig(configId) {
  return request({
    url: `${commonPrefix}` + configId,
    method: 'get',
  })
}

// 根据参数键名查询参数值
export function getConfigKey(configKey) {
  return request({
    url: `${commonPrefix}configKey/` + configKey,
    method: 'get',
  })
}

// 新增参数配置
export function addConfig(data) {
  return request({
    url: `${commonPrefix}`,
    method: 'post',
    data: data,
  })
}

// 修改参数配置
export function updateConfig(data) {
  return request({
    url: `${commonPrefix}`,
    method: 'put',
    data: data,
  })
}

// 删除参数配置
export function delConfig(configId) {
  return request({
    url: `${commonPrefix}` + configId,
    method: 'delete',
  })
}

// 刷新参数缓存
export function refreshCache() {
  return request({
    url: 'refreshCache',
    method: 'delete',
  })
}

// 导出参数
export function exportConfig(query) {
  return request({
    url: `${commonPrefix}export`,
    method: 'get',
    params: query,
  })
}
