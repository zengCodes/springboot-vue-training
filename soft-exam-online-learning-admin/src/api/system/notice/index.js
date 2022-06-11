import request from '@/utils/request'

const commonPrefix = `/system/notice/`

// 查询公告列表
export function getListNotice(query) {
  return request({
    url: `${commonPrefix}list`,
    method: 'get',
    params: query,
  })
}

// 查询公告详细
export function getNotice(noticeId) {
  return request({
    url: `${commonPrefix}${noticeId}`,
    method: 'get',
  })
}

// 新增公告
export function addNotice(data) {
  return request({
    url: `${commonPrefix}`,
    method: 'post',
    data: data,
  })
}

// 修改公告
export function updateNotice(data) {
  return request({
    url: `${commonPrefix}`,
    method: 'put',
    data: data,
  })
}

// 删除公告
export function delNotice(noticeId) {
  return request({
    url: `${commonPrefix}${noticeId}`,
    method: 'delete',
  })
}
