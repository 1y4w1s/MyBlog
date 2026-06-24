import permission from './permission'

const install = function(Vue) {
  Vue.directive('hasPermi', permission)
}

if (window.Vue) {
  window.Vue.directive('hasPermi', permission)
}

export default {
  install
}
