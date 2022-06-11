import request from '@/utils/request'

const commonPrefix = `/business/resourceType/`

// 查询数据列表
export function getListType(query) {
  return request({
    url: `${commonPrefix}findResourceTypeList`,
    method: 'get',
    params: query,
  })
}

export function updateType(id, data) {
  return request({
    url: `${commonPrefix}update/` + id,
    method: 'put',
    data: data,
  })
}

// 添加类型
export function addType(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 删除
export function delType(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}
