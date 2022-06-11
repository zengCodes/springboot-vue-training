import request from '@/utils/request'

const commonPrefix = `/business/resource/`

// 查询数据列表
export function getListResource(query) {
  return request({
    url: `${commonPrefix}findFileResourceList`,
    method: 'get',
    params: query,
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

// 删除文件数据 改变状态
export function delResource(row) {
  return request({
    url: `${commonPrefix}deleteFile`,
    method: 'post',
    data: row,
  })
}

export function uploadImg(data) {
  return request({
    url: `${commonPrefix}file`,
    method: 'post',
    data: data,
  })
}

export function getAlbumImage(query) {
  return request({
    url: `${commonPrefix}getAlbumImage`,
    method: 'get',
    params: query,
  })
}

// 删除相册
export function deleteAlbum(id) {
  return request({
    url: `${commonPrefix}deleteAlbum/${id}`,
    method: 'delete',
  })
}


export function deletePhoto(id) {
  return request({
    url: `${commonPrefix}deletePhoto/${id}`,
    method: 'put',
  })
}


export function updateAlbum(id, data) {
  return request({
    url: `${commonPrefix}update/${id}`,
    method: 'put',
    data: data,
  })
}


export function updateStatus(id, data) {
  return request({
    url: `${commonPrefix}updateStatus/${id}`,
    method: 'put',
    data: data,
  })
}