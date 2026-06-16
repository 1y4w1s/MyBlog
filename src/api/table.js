import request from '@/utils/request'

export function listTable(query) {
  return request({
    url: '/table/list',
    method: 'get',
    params: query
  })
}
