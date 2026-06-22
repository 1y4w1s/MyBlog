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
export default {
  name: 'MenuManagement',
  data() {
    return {
      list: [
        { id: 1, menuName: '系统管理', perms: '', menuType: 'M', status: '0', children: [
          { id: 11, menuName: '用户管理', perms: 'system:user:list', menuType: 'C', status: '0' },
          { id: 12, menuName: '角色管理', perms: 'system:role:list', menuType: 'C', status: '0' },
          { id: 13, menuName: '菜单管理', perms: 'system:menu:list', menuType: 'C', status: '0' }
        ]},
        { id: 2, menuName: '内容管理', perms: '', menuType: 'M', status: '0', children: [
          { id: 21, menuName: '文章管理', perms: 'content:article:list', menuType: 'C', status: '0' },
          { id: 22, menuName: '分类管理', perms: 'content:category:list', menuType: 'C', status: '0' },
          { id: 23, menuName: '友链管理', perms: 'content:link:list', menuType: 'C', status: '0' },
          { id: 24, menuName: '标签管理', perms: 'content:tag:index', menuType: 'C', status: '0' }
        ]}
      ],
      listLoading: false
    }
  }
}
</script>
