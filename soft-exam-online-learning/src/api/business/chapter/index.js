import request from '@/utils/request'

const commonPrefix = `/business/chapter/`
// 查询数据列表
export function getLessonChapter(data) {
  return request({
    url: `${commonPrefix}getLessonChapter`,
    method: 'get',
    params: data,
  })
}

// 查询数据信息
export function getChapter(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}
