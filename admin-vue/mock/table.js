const Mock = require('mockjs')

const data = Mock.mock({
  'list|100': [{
    'id|+1': 1,
    'name':'@cname',
    'email':'@email',
    'phone':/^1[3-9]\d{9}$/,
    'department':'@pick(["技术部", "产品部", "财务部", "市场部", "运营部"])',
    'status':'@pick(["在职", "离职"])'
  }]
})

module.exports = [
  {
    url: '/api/table',
    type: 'get',
    response: config => {
      const {page = 1 ,size = 10} = config.query
      const start = (page - 1) * size
      const end = start + size
      const total = data.list.length
      const list = data.list.slice(start, end)
      
      return {
        code: 20000,
        data: {
          list,
          total
        }
      }
    }
  }
]
