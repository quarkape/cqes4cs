<template>
  <div style="width:100%">
    <div style="display:flex;align-items:center;justify-content:space-between">
      <div>
        <el-button type="primary" size="medium" @click="openAdd()">添加赛事</el-button>
      </div>
    </div>
    <el-card style="margin-top:14px" shadow="hover">
      <el-table v-loading="isLoading" :data="items" border>
        <el-table-column prop="title" label="标题"></el-table-column>
        <el-table-column label="日期" width="120">
          <template slot-scope="props">{{props.row.id | dateFormat}}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="props">
            <el-button icon="el-icon-delete" size="mini" circle @click="delItem(props.row.id)"></el-button>
            <el-button icon="el-icon-edit" size="mini" circle @click="openContestDetailDialog(props.row.id)"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:center;margin-top:20px">
        <el-pagination :hide-on-single-page="true" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageConfig.currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pageConfig.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalItems"></el-pagination>
      </div>
    </el-card>

    <!-- 赛事编辑 -->
    <el-dialog title="赛事详情" :visible.sync="contestDetailDialogVisible" width="50%" :closed="handleContestDetailDialogClosed" :close-on-click-modal="false">
      <el-form ref="contestDetailDialogFormRef" :model="contestDetail" label-width="80px">
        <el-form-item label="赛事名称">
          <el-input v-model="contestDetail.title" type="text" maxlength="30" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="赛事描述">
          <el-input v-model="contestDetail.content" type="text" maxlength="200" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="赛事规则">
              <el-select v-model="contestDetail.ruleid">
                <el-option v-for="item in rules" :key="item.uuid" :label="item.name" :value="item.uuid"></el-option>
              </el-select>
            </el-form-item>
        <el-form-item>
          <el-button type="primary" size="medium" @click="checkPassed">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 增加比赛 -->
    <el-dialog title="增加赛事" :close-on-click-modal="false" :visible.sync="addContestVisible" width="40%" @closed="addContestDialogClosed">
      <div style="display:flex;justify-content:center">
        <div class="upload-container">
          <el-form :model="addContestForm" label-width="80px">
            <el-form-item label="赛事名称">
              <el-input v-model="addContestForm.name" show-word-limit maxlength="50"></el-input>
            </el-form-item>
            <el-form-item label="赛事描述">
              <el-input placeholder="如官网或比赛时间等" type="textarea" maxlength="200" show-word-limit autosize resize="none" v-model="addContestForm.desc"></el-input>
            </el-form-item>
            <el-form-item label="赛事规则">
              <el-select v-model="addContestForm.rule" placeholder="为赛事选择加分规则">
                <el-option v-for="item in rules" :key="item.uuid" :label="item.name" :value="item.uuid"></el-option>
              </el-select>
            </el-form-item>
            <el-button type="primary" style="width:100%;align-self:center;margin-top:10px" @click="submitAddContest">提交</el-button>
          </el-form>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        searchContent: '',
        items: [],
        pageConfig: {
          currentPage: 1,
          pageSize: 10
        },
        curType: [],
        curId: '',
        isLoading: false,
        contestDetailDialogVisible: false,
        contestDetail: {},
        addContestVisible: false,
        addContestForm: {
          name: '',
          desc: '',
        },
        total: 0,
        totalItems: 0,
        rules: []
      }
    },
    mounted() {
      this.getAllItems()
    },
    methods: {
      // 更改分页页数和每页数量
      handleSizeChange(size) {
        this.pageConfig.pageSize = size
        this.getAllItems()
      },
      handleCurrentChange(current) {
        this.pageConfig.currentPage = current
        this.getAllItems()
      },
      async getAllItems() {
        let {data: res} = await this.$http.post("/getAllContest", this.$qs.stringify(this.pageConfig))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        for (let i in res.data.items) {
          res.data.items[i].showb = res.data.items[i].show==0?false:true
        }
        this.items = res.data.items
        this.totalItems = res.data.total
      },
      delItem(id) {
        this.$confirm('确认删除此项?', '确定', '取消').then(async () => {
          let {data: res} = await this.$http.post("/deleteContest", this.$qs.stringify({id: id}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          for (let i in this.items) {
            if (this.items[i].id == id) {
              this.items.splice(i,1)
            }
          }
          this.$msg.success("删除成功")
          this.totalItems -= 1
        })
      },
      async openContestDetailDialog(id) {
        let {data: res } = await this.$http.post("/getContestDetailById", this.$qs.stringify({id: id}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.contestDetail = res.data
        this.curType = [this.contestDetail.typea, this.contestDetail.typeb]
        this.curId = id
        this.getRules()
        this.contestDetailDialogVisible = true
      },
      async checkPassed() {
        let {data: res} = await this.$http.post("/checkContestPassed", this.$qs.stringify(this.contestDetail))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.contestDetailDialogVisible = false
        this.handleContestDetailDialogClosed()
        this.getAllItems()
        return this.$msg.success("操作成功")
      },
      handleContestDetailDialogClosed() {
        this.contestDetail = {}
        this.curType = []
        this.curId = ''
      },
      // 关闭弹窗
      addContestDialogClosed() {
        this.addContestForm.name = ''
        this.addContestForm.desc = ''
        this.addContestForm.rule = ''
      },
      // 提交申请
      async submitAddContest() {
        if (this.addContestForm.name.trim().length == 0 || this.addContestForm.name.trim().length == 0) return this.$msg.error("请输入赛事名称或选择加分规则")
        let {data:res} = await this.$http.post('/addContestOfManager', this.$qs.stringify(this.addContestForm))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.$msg.success("增加赛事成功")
        this.getAllItems()
        this.addContestVisible = false
        this.addContestDialogClosed()
      },
      openAdd() {
        this.getRules()
        this.addContestVisible = true
      },
      async getRules() {
        let {data: res } = await this.$http.post('/getRule')
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.rules = res.data
      }
    }
  }
</script>

<style scoped>
input {
  height: 100%;
  line-height: 40px;
}
.rowsty {
  display: flex;
  justify-content: center;
  align-items: center;
}
.header {
  background-color: lightgray;
  line-height: 40px;
}
.child-col {
  height: 40px;
}
.score-input {
  display: block;
  width: 100%;
  height: 100%;
  background-color: transparent;
  outline: none;
  border: 0;
  font-size: 14px;
  text-align: center;
}
.contest-link {
  margin-top: 14px;
  display: flex;
  align-items: center;
}
.contest-link div:first-child {
  padding-right: 10px;
}
.contest-link div:last-child {
  flex: 1;
}
.upload-container {
    width: 80%;
    display: flex;
    flex-direction: column;
    font-size: 16px;
  }
</style>