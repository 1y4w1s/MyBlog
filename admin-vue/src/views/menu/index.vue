<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>菜单管理</span>
      </div>
      <el-table :data="list" border stripe v-loading="listLoading" row-key="id" default-expand-all style="width:100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="menuName" label="菜单名称" width="150"></el-table-column>
        <el-table-column prop="perms" label="权限标识" width="200"></el-table-column>
        <el-table-column prop="menuType" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.menuType==='M'" type="info">目录</el-tag>
            <el-tag v-else-if="scope.row.menuType==='C'" type="success">菜单</el-tag>
            <el-tag v-else type="warning">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { listMenu } from '@/api/menu'
export default {
  name: 'MenuManagement',
  data() {
    return {
      list: [], listLoading: false
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.listLoading = true
      listMenu(this.queryParams).then(res => {
        this.list = res.data || []
        this.listLoading = false
      }).catch(() => { this.listLoading = false })
    }
  }
}
</script>
