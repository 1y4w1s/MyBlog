import store from '@/store'

/**
 * 判断是否有指定权限
 * @param {Array} value 权限标识数组
 * @returns {Boolean}
 */
export function hasPermi(value) {
  const allPermission = '*:*:*'
  const permissions = store.getters && store.getters.permissions

  if (value && value instanceof Array && value.length > 0) {
    const permissionFlag = value
    const hasPermissions = permissions.some(permission => {
      return allPermission === permission || permissionFlag.includes(permission)
    })
    return hasPermissions
  }
  return false
}

/**
 * 判断是否有任一权限
 * @param {Array} value 权限标识数组
 * @returns {Boolean}
 */
export function hasPermiOr(value) {
  const allPermission = '*:*:*'
  const permissions = store.getters && store.getters.permissions

  if (value && value instanceof Array && value.length > 0) {
    const permissionFlag = value
    const hasPermissions = permissions.some(permission => {
      return allPermission === permission || permissionFlag.indexOf(permission) > -1
    })
    return hasPermissions
  }
  return false
}

/**
 * 判断是否具有所有权限
 * @param {Array} value 权限标识数组
 * @returns {Boolean}
 */
export function hasPermiAnd(value) {
  const allPermission = '*:*:*'
  const permissions = store.getters && store.getters.permissions

  if (value && value instanceof Array && value.length > 0) {
    const permissionFlag = value
    const hasPermissions = permissions.every(permission => {
      return allPermission === permission || permissionFlag.indexOf(permission) > -1
    })
    return hasPermissions
  }
  return false
}
