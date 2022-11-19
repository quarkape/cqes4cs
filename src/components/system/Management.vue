<template>
  <div style="width:100%;">
    <el-card shadow="hover">
      <div style="text-align:right;margin-bottom:10px">
        <el-button size="medium" type="primary" @click="addAccountDialogVisible=true">增加账号</el-button>
      </div>
      <div style="width:100%;display:flex;flex-direction:row;align-items:flex-start">
        <!-- <div class="sider">
          <el-tree accordion :data="data" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
        </div> -->
        <div class="mainbody">
          <el-table :data="tableData" style="width: 100%" border>
            <el-table-column prop="name" label="姓名" width="140"></el-table-column>
            <el-table-column prop="userid" label="工号"></el-table-column>
            <el-table-column prop="year" label="管理年级"></el-table-column>
            <el-table-column label="操作" width="200">
              <template slot-scope="props">
                <el-button icon="el-icon-unlock" size="mini" type="primary" title="重置为随机密码" @click="resetPassword(props.row.userid)"></el-button>
                <el-button icon="el-icon-delete" size="mini" type="warning" title="删除账号" @click="deleteAccount(props.row.userid)"></el-button>
                <el-button icon="el-icon-edit" size="mini" type="success" title="修改管理年级" @click="AlterManagerYear(props.row.userid)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>
    <!-- 增加账号的dialog -->
    <el-dialog title="新增账号" :visible.sync="addAccountDialogVisible" :close-on-click-modal="false" width="40%" @closed="addAccountDialogClosed">
      <el-form ref="addAccountDialogFormRef" :model="addAccountDialogForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="addAccountDialogForm.name"></el-input>
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="addAccountDialogForm.userid" type="number"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="addAccountDialogForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="管理年级">
          <el-input v-model="addAccountDialogForm.year"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="medium" style="width:100%" @click="addNewManagerAccount">新增账号</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      tableData: [],
      addAccountDialogVisible: false,
      addAccountDialogForm: {
        userid: '',
        password: '',
        name: '',
        year: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    resetPassword(userid) {
      this.$confirm('确认重置密码?', '确认', '取消').then(async () => {
          let {data: res} = await this.$http.post("/resetPasswordManager", this.$qs.stringify({userid:userid}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          // this.$msg.success("重置密码成功!")
          this.$notify({
              title: '提示',
              // message: '密码重置成功，重置后的随机密码为: ' + res.data + '(区分大小写)。请告知用户尽快修改密码。',
              duration: 0,
              dangerouslyUseHTMLString: true,
              message: '密码重置成功，重置后的随机密码为: <strong><span style=\"color:#0081ff\;text-decoration:underline;">' + res.data + '</span></strong>(区分大小写)。请告知教师尽快修改密码。'
            });
        })
    },
    deleteAccount(userid) {
      this.$confirm('确认删除该账号?', '确认', '取消').then(async () => {
        let {data: res} = await this.$http.post("/deleteAccountManager", this.$qs.stringify({userid:userid}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.$msg.success("账号删除成功!")
        for (let i in this.tableData) {
          if (this.tableData[i].userid == userid) {
            this.tableData.splice(i, 1)
          }
        }
      })
    },
    addAccountDialogClosed() {
      this.$refs.addAccountDialogFormRef.resetFields()
    },
    AlterManagerYear(userid) {
      this.$prompt('请输入管理的年级', '修改管理年级', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(async ({ value }) => {
          let {data: res} = await this.$http.post("/alterManagerYear", this.$qs.stringify({userid:userid,year:value}))
          if (!res) return
          if (res.code != 0) return thos.$msg.error(res.message)
          this.$msg.success("操作成功")
          for (let i in this.tableData) {
            if (this.tableData[i].userid == userid) {
              this.tableData[i].year = value
              break
            }
          }
        })
    },
    async getList() {
      let {data: res} = await this.$http.post("/getManagerList")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.tableData = res.data
    },
    async addNewManagerAccount() {
      for (let i in this.addAccountDialogForm) {
        if (this.addAccountDialogForm[i].trim().length == 0) {
          return this.$msg.error("请填写完整")
        }
      }
      for (let i in this.tableData) {
        if (this.tableData[i].userid == this.addAccountDialogForm.userid) {
          return this.$msg.error("该账号已存在")
        }
      }
      let {data: res} = await this.$http.post("/addManagerAccount", this.$qs.stringify(this.addAccountDialogForm))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.$msg.success("账号添加成功!")
      this.tableData.push(this.addAccountDialogForm)
      this.addAccountDialogVisible = false
      for (let i in this.addAccountDialogForm) {
        this.addAccountDialogForm[i] == ''
      }
    }
  }
}
</script>

<style scoped>
.sider {
  min-width: 200px;
  padding: 20px;
  min-height: 200px;
  background-color: #fff;
  margin-right: 20px;
  color: #fff;
}
.mainbody {
  flex: 1;
}
</style>