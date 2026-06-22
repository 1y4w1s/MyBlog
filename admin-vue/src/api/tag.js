import request from '@/utils/request'

export function listTag(query) {
  return request({
    baseURL: '/blog-api',
    url: '/tag/tagList',
    method: 'get',
    params: query
  })
}

export function getTag(id) {
  return request({
    baseURL: '/blog-api',
    url: '/tag/' + id,
    method: 'get'
  })
}

export function addTag(data) {
  return request({
    baseURL: '/blog-api',
    url: '/tag',
    method: 'post',
    data
  })
}

export function updateTag(data) {
  return request({
    baseURL: '/blog-api',
    url: '/tag',
    method: 'put',
    data
  })
}

export function delTag(id) {
  return request({
    baseURL: '/blog-api',
    url: '/tag/' + id,
    method: 'delete'
  })
}
