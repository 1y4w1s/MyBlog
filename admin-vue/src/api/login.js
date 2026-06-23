import request from '@/utils/request'

export function login(data) {
  return request({
    baseURL: '/dev-api',
    url: '/user/login',
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    baseURL: '/dev-api',
    url: '/getInfo',
    method: 'get'
  })
}

export function logout() {
  return request({
    baseURL: '/dev-api',
    url: '/logout',
    method: 'post'
  })
}
