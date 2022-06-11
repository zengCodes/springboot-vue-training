import request from '@/utils/request'

const commonPrefix = `/business/resource/`

export function mergeFile(data) {
  return request({
    url: `${commonPrefix}upload/mergeFile`,
    method: 'post',
    data: data,
  })
}
