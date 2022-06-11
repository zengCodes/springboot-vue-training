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

// 删除文件数据 改变状态
export function delResource(row) {
  return request({
    url: `${commonPrefix}deleteFile`,
    method: 'post',
    data: row,
  })
}

export function getAlbumImage(query) {
  return request({
    url: `${commonPrefix}getAlbumImage`,
    method: 'get',
    params: query,
  })
}

export function uploadImg(data) {
  return request({
    url: `${commonPrefix}image`,
    method: 'post',
    data: data,
  })
}
