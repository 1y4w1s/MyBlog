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
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'RoleManagement',
  data() {
    return {
      list: [
        { id: 1, roleName: '超级管理员', roleKey: 'admin', status: '0', remark: '拥有所有权限' },
        { id: 2, roleName: '普通用户', roleKey: 'test', status: '0', remark: '部分权限' }
      ],
      listLoading: false
    }
  }
}
</script>
