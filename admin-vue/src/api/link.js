import request from '@/utils/request'

export function listLink(query) {
  return request({
    baseURL: '/blog-api',
    url: '/link/getLinkList',
    method: 'get',
    params: query
  })
}

export function getLink(id) {
  return request({
    baseURL: '/blog-api',
    url: '/link/' + id,
    method: 'get'
  })
}

export function addLink(data) {
  return request({
    baseURL: '/blog-api',
    url: '/link',
    method: 'post',
    data
  })
}

export function updateLink(data) {
  return request({
    baseURL: '/blog-api',
    url: '/link',
    method: 'put',
    data
  })
}

export function delLink(id) {
  return request({
    baseURL: '/blog-api',
    url: '/link/' + id,
    method: 'delete'
  })
}
