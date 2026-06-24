export default {
  install(Vue) {
    Vue.directive('dialogDrag', {
      bind(el) {
        const dialogHeaderEl = el.querySelector('.el-dialog__header')
        const dialogBody = el.querySelector('.el-dialog__body')
        if (!dialogHeaderEl || !dialogBody) return

        dialogHeaderEl.style.cssText += ';cursor:move;'
        dialogBody.style.cssText += ';overflow:auto;'

        const minWidth = 400
        const minHeight = 300

        // 记录初始位置
        const dragMousedown = function(event) {
          if (event.button !== 0) return

          const elLeft = el.offsetLeft
          const elTop = el.offsetTop
          const elWidth = el.offsetWidth
          const elHeight = el.offsetHeight
          const clientX = event.clientX
          const clientY = event.clientY

          el.style.position = 'fixed'
          el.style.marginLeft = '0px'
          el.style.marginTop = '0px'

          if (elLeft + elWidth > window.innerWidth) {
            el.style.left = (window.innerWidth - elWidth) + 'px'
          } else {
            el.style.left = elLeft + 'px'
          }

          if (elTop + elHeight > window.innerHeight) {
            el.style.top = (window.innerHeight - elHeight) + 'px'
          } else {
            el.style.top = elTop + 'px'
          }

          const dragMouseMove = function(event) {
            const moveX = event.clientX - clientX
            const moveY = event.clientY - clientY

            if (moveX < 0) {
              el.style.left = Math.max(0, elLeft + moveX) + 'px'
            } else if (moveX > 0) {
              el.style.left = Math.min(window.innerWidth - elWidth, elLeft + moveX) + 'px'
            }

            if (moveY < 0) {
              el.style.top = Math.max(0, elTop + moveY) + 'px'
            } else if (moveY > 0) {
              el.style.top = Math.min(window.innerHeight - elHeight, elTop + moveY) + 'px'
            }
          }

          const dragMouseup = function() {
            document.removeEventListener('mousemove', dragMouseMove)
            document.removeEventListener('mouseup', dragMouseup)
          }

          document.addEventListener('mousemove', dragMouseMove)
          document.addEventListener('mouseup', dragMouseup)
        }

        dialogHeaderEl.addEventListener('mousedown', dragMousedown)
      }
    })
  }
}
