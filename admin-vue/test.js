const http = require('http')

const BASE = 'http://localhost:9528'

function request(url) {
  return new Promise((resolve, reject) => {
    http.get(url, (res) => {
      let data = ''
      res.on('data', (chunk) => { data += chunk })
      res.on('end', () => {
        try { resolve(JSON.parse(data)) } catch (e) { resolve(data) }
      })
    }).on('error', reject)
  })
}

async function run() {
  console.log('=== 测试登录接口 ===')
  try {
    const loginRes = await request(BASE + '/dev-api/user/login?username=admin&password=123456')
    console.log('登录结果:', JSON.stringify(loginRes, null, 2))
  } catch (e) {
    console.log('登录失败:', e.message)
  }

  console.log('\n=== 测试获取用户信息 ===')
  try {
    const infoRes = await request(BASE + '/dev-api/getInfo')
    console.log('用户信息:', JSON.stringify(infoRes, null, 2))
  } catch (e) {
    console.log('获取信息失败:', e.message)
  }

  console.log('\n=== 测试文章列表 ===')
  try {
    const articleRes = await request(BASE + '/blog-api/article/articleList?pageNum=1&pageSize=5')
    console.log('文章列表:', JSON.stringify(articleRes, null, 2))
  } catch (e) {
    console.log('文章列表失败:', e.message)
  }

  console.log('\n=== 测试分类列表 ===')
  try {
    const categoryRes = await request(BASE + '/blog-api/category/getCategoryList')
    console.log('分类列表:', JSON.stringify(categoryRes, null, 2))
  } catch (e) {
    console.log('分类列表失败:', e.message)
  }

  console.log('\n=== 测试友链列表 ===')
  try {
    const linkRes = await request(BASE + '/blog-api/link/getLinkList')
    console.log('友链列表:', JSON.stringify(linkRes, null, 2))
  } catch (e) {
    console.log('友链列表失败:', e.message)
  }

  console.log('\n=== 测试评论列表 ===')
  try {
    const commentRes = await request(BASE + '/blog-api/comment/linkCommentList?pageNum=1&pageSize=5')
    console.log('评论列表:', JSON.stringify(commentRes, null, 2))
  } catch (e) {
    console.log('评论列表失败:', e.message)
  }
}

run()
