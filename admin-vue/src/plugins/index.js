import dialog from './dialog'

const plugins = {
  install: function(Vue) {
    Vue.use(dialog)
  }
}

export default plugins
