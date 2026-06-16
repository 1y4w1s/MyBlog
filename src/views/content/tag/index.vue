<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>标签管理</span>
      </div>
      <el-table :data="list" border stripe v-loading="listLoading" style="width:100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="name" label="标签名称" width="200"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'TagManagement',
  data() {
    return {
      list: [
        { id: 1, name: 'Java', createTime: '2023-01-01 10:00:00' },
        { id: 2, name: 'Python', createTime: '2023-01-01 10:00:00' },
        { id: 3, name: 'Spring Boot', createTime: '2023-02-15 14:30:00' },
        { id: 4, name: 'Vue.js', createTime: '2023-03-20 09:15:00' },
        { id: 5, name: 'MySQL', createTime: '2023-04-10 16:45:00' }
      ],
      listLoading: false
    }
  }
}
</script>
