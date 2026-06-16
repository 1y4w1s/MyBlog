import request from '@/utils/request'

export function listComment(query) {
  return request({
    baseURL: '/blog-api',
    url: '/comment/linkCommentList',
    method: 'get',
    params: query
  })
}

export function getComment(id) {
  return request({
    baseURL: '/blog-api',
    url: '/comment/' + id,
    method: 'get'
  })
}

export function addComment(data) {
  return request({
    baseURL: '/blog-api',
    url: '/comment',
    method: 'post',
    data
  })
}

export function delComment(id) {
  return request({
    baseURL: '/blog-api',
    url: '/comment/' + id,
    method: 'delete'
  })
}
