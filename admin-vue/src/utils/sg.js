// 消息提示工具
export function msgSuccess(msg) {
  const { Message } = require('element-ui')
  Message({
    message: msg,
    type: 'success',
    duration: 1500
  })
}

export function msgError(msg) {
  const { Message } = require('element-ui')
  Message({
    message: msg,
    type: 'error',
    duration: 1500
  })
}

export default {
  msgSuccess,
  msgError
}
