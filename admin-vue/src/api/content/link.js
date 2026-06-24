import request from '@/utils/request'

// 查询友链列表
export function listLink(query) {
  return request({
    url: '/content/link/list',
    method: 'get',
    params: query
  })
}

// 查询友链详细
export function getLink(linkId) {
  return request({
    url: '/content/link/' + linkId,
    method: 'get'
  })
}

// 新增友链
export function addLink(data) {
  return request({
    url: '/content/link',
    method: 'post',
    data: data
  })
}

// 修改友链
export function updateLink(data) {
  return request({
    url: '/content/link',
    method: 'put',
    data: data
  })
}

// 删除友链
export function delLink(linkId) {
  return request({
    url: '/content/link/' + linkId,
    method: 'delete'
  })
}

// 修改友链状态
export function changeLinkStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/content/link/changeStatus',
    method: 'put',
    data: data
  })
}
