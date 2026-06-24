import axios from 'axios'
import { getToken } from '@/utils/auth'
import { Message } from 'element-ui'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 60000
})

service.interceptors.request.use(config => {
  const isToken = (config.headers || {}).isToken === false
  if (getToken() && !isToken) {
    config.headers['token'] = getToken()
  }
  return config
}, error => {
  console.log(error)
  Promise.reject(error)
})

export function download(url, params) {
  return service({
    url: url,
    method: 'get',
    params: params,
    responseType: 'blob'
  })
}

export function downloadFile(fileName) {
  return service({
    url: '/common/download',
    method: 'get',
    params: { fileName: fileName },
    responseType: 'blob'
  })
}

export function downLoadExcel(url, name) {
  download(url).then(res => {
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = name
    a.click()
  }).catch(err => {
    Message.error('下载文件出现错误，请联系管理员！')
  })
}

export default service
