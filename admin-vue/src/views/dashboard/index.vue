<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(item, idx) in stats" :key="idx">
        <el-card><div class="card-item"><div class="card-num">{{ item.value }}</div><div class="card-label">{{ item.label }}</div></div></el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'Dashboard',
  data() {
    return {
      stats: [
        { label: '文章总数', value: '-' },
        { label: '分类总数', value: '-' },
        { label: '评论总数', value: '-' },
        { label: '友链总数', value: '-' }
      ]
    }
  },
  created() {
    request({ baseURL: '/blog-api', url: '/article/articleList', method: 'get', params: { pageNum: 1, pageSize: 1 } }).then(res => { if (res.code === 200) this.stats[0].value = res.data.total || '0' })
    request({ baseURL: '/blog-api', url: '/category/getCategoryList', method: 'get' }).then(res => { if (res.code === 200) this.stats[1].value = (res.data || []).length || '0' })
    request({ baseURL: '/blog-api', url: '/comment/linkCommentList', method: 'get', params: { pageNum: 1, pageSize: 1 } }).then(res => { if (res.code === 200) this.stats[2].value = res.data.total || '0' })
    request({ baseURL: '/blog-api', url: '/link/getLinkList', method: 'get' }).then(res => { if (res.code === 200) this.stats[3].value = (res.data || []).length || '0' })
  }
}
</script>

<style scoped>
.card-item { text-align: center; padding: 10px 0; }
.card-num { font-size: 28px; font-weight: bold; color: #409EFF; }
.card-label { font-size: 14px; color: #999; margin-top: 5px; }
</style>
