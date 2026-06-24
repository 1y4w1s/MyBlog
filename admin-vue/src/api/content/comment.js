import request from '@/utils/request'

// 查询评论列表
export function listComment(query) {
  return request({
    url: '/content/comment/list',
    method: 'get',
    params: query
  })
}

// 查询评论详细
export function getComment(commentId) {
  return request({
    url: '/content/comment/' + commentId,
    method: 'get'
  })
}

// 修改评论
export function updateComment(data) {
  return request({
    url: '/content/comment',
    method: 'put',
    data: data
  })
}

// 删除评论
export function delComment(commentId) {
  return request({
    url: '/content/comment/' + commentId,
    method: 'delete'
  })
}
