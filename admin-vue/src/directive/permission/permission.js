import Vue from 'vue'
import store from '@/store'

function checkPermi(el, binding) {
  const { value } = binding
  const permissions = store.getters && store.getters.permissions
  const allPermission = '*:*:*'

  if (value && value instanceof Array && value.length > 0) {
    const permissionFlag = value
    const hasPermissions = permissions.some(permission => {
      return allPermission === permission || permissionFlag.includes(permission)
    })

    if (!hasPermissions) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  } else {
    throw new Error('need roles? Like v-hasPermi="[\'admin:user:add\']"')
  }
}

const inserted = (el, binding) => {
  checkPermi(el, binding)
}

const update = (el, binding) => {
  checkPermi(el, binding)
}

export default {
  inserted,
  update
}
