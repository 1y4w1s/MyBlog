<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="文章标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入文章标题" />
      </el-form-item>
      <el-form-item label="文章摘要" prop="summary">
        <el-input v-model="form.summary" type="textarea" placeholder="请输入文章摘要" />
      </el-form-item>
      <el-form-item label="文章分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="请选择文章分类">
          <el-option
            v-for="item in categoryList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="文章标签" prop="tagIds">
        <el-select v-model="form.tagIds" multiple placeholder="请选择文章标签">
          <el-option
            v-for="item in tagList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="文章内容" prop="content">
        <mavon-editor v-model="form.content" :toolbars="toolbars" style="min-height: 500px; width: 100%" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { listCategory } from '@/api/content/category'
import { listTag } from '@/api/content/tag'
import { getArticle, addArticle, updateArticle } from '@/api/content/article'

export default {
  name: 'ArticleWrite',
  data() {
    return {
      form: {
        id: null,
        title: '',
        summary: '',
        content: '',
        categoryId: null,
        tagIds: []
      },
      rules: {
        title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择文章分类', trigger: 'change' }]
      },
      categoryList: [],
      tagList: [],
      toolbars: {
        bold: true,
        italic: true,
        header: true,
        underline: true,
        strikethrough: true,
        mark: true,
        quote: true,
        ol: true,
        ul: true,
        link: true,
        imagelink: true,
        code: true,
        table: true,
        fullscreen: true,
        htmlcode: true,
        undo: true,
        redo: true,
        navigation: true,
        subfield: true,
        preview: true
      }
    }
  },
  created() {
    this.getCategoryList()
    this.getTagList()
    const id = this.$route.query.id
    if (id) {
      this.getArticleInfo(id)
    }
  },
  methods: {
    getCategoryList() {
      listCategory({}).then(response => {
        this.categoryList = response.rows
      })
    },
    getTagList() {
      listTag({}).then(response => {
        this.tagList = response.rows
      })
    },
    getArticleInfo(id) {
      getArticle(id).then(response => {
        this.form = response
        if (this.form.tagIds) {
          this.form.tagIds = this.form.tagIds.split(',').map(Number)
        }
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          const data = {
            ...this.form,
            tagIds: this.form.tagIds ? this.form.tagIds.join(',') : ''
          }
          if (this.form.id != null) {
            updateArticle(data).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.$router.push('/article')
            })
          } else {
            addArticle(data).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.$router.push('/article')
            })
          }
        }
      })
    },
    handleCancel() {
      this.$router.push('/article')
    }
  }
}
</script>
