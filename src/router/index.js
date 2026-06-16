import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/',
    component: () => import('@/layout'),
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '首页', icon: 'dashboard', affix: true }
    }]
  }
]

export const asyncRoutes = [
  {
    path: '/write',
    component: () => import('@/layout'),
    children: [{
      path: 'index',
      name: 'WriteBlog',
      component: () => import('@/views/write/index'),
      meta: { title: '写博文', icon: 'edit', permission: ['content:article:writer'] }
    }]
  },
  {
    path: '/system',
    component: () => import('@/layout'),
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'system', permission: ['system:user:list', 'system:role:list', 'system:menu:list'] },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index'),
        meta: { title: '用户管理', icon: 'user', permission: ['system:user:list'] }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index'),
        meta: { title: '角色管理', icon: 'peoples', permission: ['system:role:list'] }
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index'),
        meta: { title: '菜单管理', icon: 'tree-table', permission: ['system:menu:list'] }
      }
    ]
  },
  {
    path: '/content',
    component: () => import('@/layout'),
    redirect: '/content/article',
    meta: { title: '内容管理', icon: 'example', permission: ['content:article:list', 'content:category:list', 'content:link:list', 'content:tag:index'] },
    children: [
      {
        path: 'article',
        name: 'Article',
        component: () => import('@/views/content/article/index'),
        meta: { title: '文章管理', icon: 'table', permission: ['content:article:list'] }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/content/category/index'),
        meta: { title: '分类管理', icon: 'tree', permission: ['content:category:list'] }
      },
      {
        path: 'link',
        name: 'Link',
        component: () => import('@/views/content/link/index'),
        meta: { title: '友链管理', icon: 'link', permission: ['content:link:list'] }
      },
      {
        path: 'tag',
        name: 'Tag',
        component: () => import('@/views/content/tag/index'),
        meta: { title: '标签管理', icon: 'tag', permission: ['content:tag:index'] }
      }
    ]
  }
]

const createRouter = () => new Router({
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
