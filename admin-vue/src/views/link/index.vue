<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>友链管理</span>
      </div>
      <el-table :data="list" border stripe v-loading="listLoading" style="width:100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="name" label="名称" width="150"></el-table-column>
        <el-table-column prop="address" label="地址" min-width="250"></el-table-column>
        <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
      </el-table>
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryParams.pageNum" :page-sizes="[10,20,30]" :page-size="queryParams.pageSize" layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px"></el-pagination>
    </el-card>
  </div>
</template>

<script>
import { listLink } from '@/api/link'
export default {
  name: 'LinkManagement',
  data() {
    return {
      list: [], total: 0, listLoading: false,
      queryParams: { pageNum: 1, pageSize: 10 }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.listLoading = true
      listLink(this.queryParams).then(res => {
        const d = res.data
        this.list = d.rows || d || []
        this.total = d.total || this.list.length
        this.listLoading = false
      }).catch(() => { this.listLoading = false })
    },
    handleSizeChange(val) { this.queryParams.pageSize = val; this.getList() },
    handleCurrentChange(val) { this.queryParams.pageNum = val; this.getList() }
  }
}
</script>
