<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>角色管理</span>
      </div>
      <el-table :data="list" border stripe v-loading="listLoading" style="width:100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="roleName" label="角色名称" width="150"></el-table-column>
        <el-table-column prop="roleKey" label="角色标识" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200"></el-table-column>
      </el-table>
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryParams.pageNum" :page-sizes="[10,20,30]" :page-size="queryParams.pageSize" layout="total,sizes,prev,pager,next,jumper" :total="total" style="margin-top:15px"></el-pagination>
    </el-card>
  </div>
</template>

<script>
import { listRole } from '@/api/role'
export default {
  name: 'RoleManagement',
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
      listRole(this.queryParams).then(res => {
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
