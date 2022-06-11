import request from '@/utils/request'

const commonPrefix = `/business/paper/`

// 查询数据列表
export function getListPaper(params) {
  return request({
    url: `${commonPrefix}findPaperList`,
    method: 'get',
    params,
  })
}

export function getAllPaperList(params) {
  return request({
    url: `${commonPrefix}getAllPaperList`,
    method: 'get',
    params,
  })
}

// 获取详细试卷信息
export function getPaper(id) {
  return request({
    url: `${commonPrefix}edit/` + id,
    method: 'get',
  })
}

// 导出试卷模板
export function importPracticeWord(data) {
  return request({
    url: `${commonPrefix}importPracticeWord`,
    method: 'post',
    data,
  })
}

// 下载试卷模板
export function downloadTemplate() {
  return request({
    url: `${commonPrefix}downloadTemplate`,
    method: 'get',
    responseType: 'blob',
  })
}

// 修改试卷状态
export function updatePaperStatus(id, status) {
  return request({
    url: `${commonPrefix}updateStatus/` + id + `/` + status,
    method: 'put',
  })
}

// 修改试卷
export function updatePaper(id, data) {
  return request({
    url: `${commonPrefix}update/` + id,
    method: 'put',
    data,
  })
}

// 手动添加试卷
export function addPaper(data) {
  return request({
    url: `${commonPrefix}add`,
    method: 'post',
    data,
  })
}

// 删除试卷
export function delPaper(id) {
  return request({
    url: `${commonPrefix}delete/` + id,
    method: 'delete',
  })
}


