<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>写博文</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题"></el-input>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入文章摘要"></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="15" placeholder="请输入文章内容"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">发布文章</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'WriteBlog',
  data() {
    return {
      form: {
        title: '',
        summary: '',
        content: '',
        categoryId: ''
      },
      categories: [],
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadCategories()
  },
  methods: {
    loadCategories() {
      request({ baseURL: '/blog-api', url: '/category/getCategoryList', method: 'get' }).then(res => {
        if (res.code === 200) {
          this.categories = res.data || []
        }
      })
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.$message.success('文章发布功能需要后端API支持')
      })
    },
    handleReset() {
      this.form = { title: '', summary: '', content: '', categoryId: '' }
    }
  }
}
</script>
