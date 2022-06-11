import request from '@/utils/request'

const commonPrefix = `/business/chapter/`
// 查询数据列表
export function getChapterTree() {
  return request({
    url: `${commonPrefix}getChapterTreeList`,
    method: 'get',
  })
}

// 查询数据信息
export function getChapter(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}

// 新增数据
export function addChapter(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data: data,
  })
}

// 修改数据
export function updateChapter(id, data) {
  return request({
    url: `${commonPrefix}update/${id}`,
    method: 'put',
    data: data,
  })
}

// 删除数据
export function delChapter(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}

// 启用
export function updateStatus(id, status) {
  return request({
    url: `${commonPrefix}updateStatus/` + id + `/` + status,
    method: 'put',
  })
}
