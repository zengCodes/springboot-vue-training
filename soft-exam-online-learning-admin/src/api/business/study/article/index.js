import request from '@/utils/request'

const commonPrefix = `/business/article/`

// 查询列表
export function getListArticle(query) {
  return request({
    url: `${commonPrefix}getListArticle`,
    method: 'get',
    params: query,
  })
}

// 获取详细信息
export function getArticle(id) {
  return request({
    url: `${commonPrefix}edit/${id}`,
    method: 'get',
  })
}

// 修改状态
export function updateArticleStatus(id, status) {
  return request({
    url: `${commonPrefix}updateStatus/` + id + `/` + status,
    method: 'put',
  })
}

// 修改
export function updateArticle(id, data) {
  return request({
    url: `${commonPrefix}update/` + id,
    method: 'put',
    data: data,
  })
}

// 添加
export function addArticle(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 删除
export function delArticle(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}
