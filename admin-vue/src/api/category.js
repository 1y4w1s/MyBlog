import request from '@/utils/request'

export function listCategory(query) {
  return request({
    baseURL: '/blog-api',
    url: '/category/getCategoryList',
    method: 'get',
    params: query
  })
}

export function getCategory(id) {
  return request({
    baseURL: '/blog-api',
    url: '/category/' + id,
    method: 'get'
  })
}

export function addCategory(data) {
  return request({
    baseURL: '/blog-api',
    url: '/category',
    method: 'post',
    data
  })
}

export function updateCategory(data) {
  return request({
    baseURL: '/blog-api',
    url: '/category',
    method: 'put',
    data
  })
}

export function delCategory(id) {
  return request({
    baseURL: '/blog-api',
    url: '/category/' + id,
    method: 'delete'
  })
}
