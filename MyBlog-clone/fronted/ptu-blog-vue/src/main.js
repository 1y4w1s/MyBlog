// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-default/index.css'
import './assets/css/style.less'
import store from './store'
import MavonEditor from 'mavon-editor'

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.use(MavonEditor)

const originalPush = router.push
router.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    // 忽略重复导航错误
    if (err.name === 'NavigationDuplicated') {
      return Promise.resolve(err)
    }
    return Promise.reject(err)
  })
}

// 同样处理 replace 方法
const originalReplace = router.replace
router.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => {
    if (err.name === 'NavigationDuplicated') {
      return Promise.resolve(err)
    }
    return Promise.reject(err)
  })
}

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>',
  store
})
