<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>用户管理</span>
      </div>
      <el-table :data="list" border stripe v-loading="listLoading" style="width:100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="userName" label="用户名" width="120"></el-table-column>
        <el-table-column prop="nickName" label="昵称" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180"></el-table-column>
        <el-table-column prop="sex" label="性别" width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.sex === '1' ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:15px;text-align:right" layout="total,sizes,prev,pager,next" :total="list.length" :page-sizes="[10,20,30]"></el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'UserManagement',
  data() {
    return {
      list: [
        { id: 1, userName: 'admin', nickName: '管理员', email: 'admin@example.com', sex: '1', status: '0' }
      ],
      listLoading: false
    }
  }
}
</script>
