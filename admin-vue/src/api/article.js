import request from '@/utils/request'

export function listArticle(query) {
  return request({
    baseURL: '/blog-api',
    url: '/article/articleList',
    method: 'get',
    params: query
  })
}

export function getArticle(id) {
  return request({
    baseURL: '/blog-api',
    url: '/article/' + id,
    method: 'get'
  })
}

export function addArticle(data) {
  return request({
    baseURL: '/blog-api',
    url: '/article',
    method: 'post',
    data
  })
}

export function updateArticle(data) {
  return request({
    baseURL: '/blog-api',
    url: '/article',
    method: 'put',
    data
  })
}

export function delArticle(id) {
  return request({
    baseURL: '/blog-api',
    url: '/article/' + id,
    method: 'delete'
  })
}
