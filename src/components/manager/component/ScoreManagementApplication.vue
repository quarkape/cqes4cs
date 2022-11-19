<template>
  <div>
    <el-dialog title="管理学生学分" class="history-container" :visible.sync="manageScoreDialogVisible" width="50%" @closed="handleManageScoreDialogClose" :close-on-click-modal="false">
      <el-table :data="scoreApps" style="width:100%" border v-loading="loading">
        <el-table-column prop="name" label="申请名称"></el-table-column>
        <el-table-column label="日期" width="120">
          <template slot-scope="props">{{props.row.id.substring(0,13) | dateFormat}}</template>
        </el-table-column>
        <el-table-column prop="state" label="状态" width="120">
          <template slot-scope="props">
            <el-tag :type="props.row.state==1?'success':'warning'">{{props.row.state==0?'未审核':(props.row.state==1?'审核通过':'修改通过')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template slot-scope="props">
            <el-button type="primary" size="mini" title="编辑" plain icon="el-icon-edit" @click="openCertainScoreManageDialog(props.row.id)"></el-button>
            <el-button type="danger" size="mini" title="删除" plain icon="el-icon-delete" @click="checkAuthenticationNotPass(props.row.id)"></el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-dialog class="history-container" width="40%" title="学分材料编辑页面" :visible.sync="certainManageScoreDialogVisible" @close="handleCertainManageScoreDialogClose" append-to-body :close-on-click-modal="false">
        <el-form :model="materialDetail" label-width="80px">
          <el-form-item label="加分类型">
            <el-select v-model="materialDetail.ruleid" @change="handleRuleChange" placeholder="请选择">
              <el-option v-for="item in rules" :key="item.uuid" :label="item.name" :value="item.uuid"></el-option>
            </el-select>
          </el-form-item>
          <div>
            <el-form-item label="加分等级">
              <el-cascader :key="curKey" v-model="contestLevel" :options="curContestConfig" :props="contestLevelConfigProps" @change="handleContestLevelChange"></el-cascader>
            </el-form-item>
          </div>
          <el-form-item label="材料图片">
            <img style="width:100px; height:100px" :src="materialDetail.imgurl" @click="showFullImage(materialDetail.imgurl)" title="点击查看图片">
          </el-form-item>
          <el-form-item label="加分数值">
            <el-input @input="change($event)" type="number" placeholder="请输入分值" clearable v-model="materialDetail.score" style="width:200px"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="checkAuthenticationPassModify" style="width:200px">确认修改</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: ["dialogVisible", "curId"],
  data() {
    return {
      manageScoreDialogVisible: this.dialogVisible,
      id: this.curId,
      scoreApps: [],
      loading: false,
      // 管理某个学分申请弹窗
      certainManageScoreDialogVisible: false,
      // 某个学分申请的具体内容
      certainAuth: {},
      sloading: false,
      materialDetail: {},
      rules: [],
      contestLevel: [],
      curKey: '',
      contestLevel: '',
      curContestConfig: [],
      contestLevelConfigProps: {
        expandTrigger: 'hover',
        value: 'uuid',
        children: 'children',
        label: 'name'
      },
      state: 0,
      innerId: ''
    }
  },
  watch: {
    dialogVisible(val) {
      this.manageScoreDialogVisible = val
    },
    curId(val) {
      this.id = val
    },
    curContestConfig(newVal) {
      this.curKey++
    }
  },
  mounted() {
    this.getAllItems()
  },
  methods: {
    // 打开某个学生的学分管理弹窗
    async getAllItems() {
      this.loading = true
      let {data: res} = await this.$http.post("/getStudentAuthenticationsById", this.$qs.stringify({userid: this.id}))
      this.loading = false
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.scoreApps = res.data
    },
    // 打开某个特定的学分申请管理页面
    async openCertainScoreManageDialog(id) {
      this.innerId = id
      this.certainManageScoreDialogVisible = true
      this.sloading = true
      this.showMaterialDetail()
    },
    // 关闭学分管理页面
    handleManageScoreDialogClose() {
      this.scoreApps = []
      this.innerId = ''
      this.$emit("closeDialog")
    },
    // 关闭某个学分管理页面
    handleCertainManageScoreDialogClose() {
      this.certainAuth = {}
      this.curContestConfig = []
    },    
    // 更换赛事类型
    handleSpecialContestTypeChange (item) {
      this.certainAuth.typea = item[0]
      this.certainAuth.typeb = item[1]
    },
    // 获取某一个材料的详细内容
    async showMaterialDetail() {
      let {data: res} = await this.$http.post("/getCertainAuthenticationByIdWithScore", this.$qs.stringify({id: this.innerId}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.materialDetail = res.data.item
      // 设置初始originalItem
      this.materialDetail.imgurl = this.$surl + this.materialDetail.imgurl
      this.materialDetail.score = 0
      this.originalItem = JSON.stringify(this.materialDetail)
      this.curContestConfig.push(JSON.parse(res.data.config))
      // 获取规则列表
      this.getRules()
      // 生成等级路径
      let levelstr = JSON.parse(res.data.item.levelstr)
      this.contestLevel = this.getPathArr(JSON.parse(res.data.config), levelstr)
      // 查找分值
      this.materialDetail.score = res.data.score
      this.state = res.data.state
    },
    // 审核通过材料
    async checkAuthenticationPassModify() {
      if (this.contestLevel.length <= 0) return this.$msg.error("请选择加分等级")
      // 修改材料信息，然后设置分数
      let originalItemJSON = JSON.parse(this.originalItem)
      originalItemJSON.score = this.materialDetail.score
      if (JSON.stringify(originalItemJSON) == JSON.stringify(this.materialDetail)) {
        if (this.state == 0) {
          this.materialDetail.isModify = 3 // 没有修改，审核通过
        } else {
          this.materialDetail.isModify = 1
        }
      } else {
        this.materialDetail.isModify = 1
      }
      let {data: res} = await this.$http.post("/checkAuthenticationPassModify", this.$qs.stringify(this.materialDetail))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      // if (this.items != undefined || this.items != null) {
      //   for (let i=0;i<this.items.length;i++) {
      //     if (this.items[i].id == this.materialDetail.id) {
      //       this.items.splice(i, 1)
      //       break
      //     }
      //   }
      // }
      this.certainManageScoreDialogVisible = false
      this.materialDetailDialogClosed()
      return this.$msg.success("操作成功!请刷新查看!")
      // 设置分数
    },
    // 删除一项学分申请
    checkAuthenticationNotPass(id) {
      this.$prompt('删除后无法找回。请输入简短的原因或者提示，系统会自动向学生发送通知。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          closeOnClickModal: false
        }).then(async ({ value }) => {
          if (value === null || value.trim().length === 0) {
            return this.$msg.warning('请输入简短的原因或者提示')
          }
          let {data: res} = await this.$http.post("/checkAuthenticationNotPass", this.$qs.stringify({id: id, content: value}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          for (let i=0;i<this.scoreApps.length;i++) {
            if (this.scoreApps[i].id == id) {
              this.scoreApps.splice(i, 1)
              break
            }
          }
          // if (this.items != undefined || this.items != null) {
          //   for (let i=0;i<this.items.length;i++) {
          //     if (this.items[i].id == this.materialDetail.id) {
          //       this.items.splice(i, 1)
          //       break
          //     }
          //   }
          // }
          // this.materialDetailVisible = false
          // this.materialDetailDialogClosed()
          return this.$msg.success("删除操作成功!")
        }).catch((error) => {
          return     
      });
  },
    // 关闭材料详情页面的时候将所有数据清空
    materialDetailDialogClosed() {
      this.materialDetail.ruleid = ''
      this.materialDetail.imgurl = ''
      this.materialDetail.score = ''
      this.curId = ''
      this.contestLevel = ''
      this.state = 0
      this.curContestConfig = []
    },
    openFullImage(img) {
      window.open(img)
    },
    async handleRuleChange(item) {
      let {data: res} = await this.$http.post("/getContestConfigByUuid", this.$qs.stringify({uuid: item}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.curContestConfig = []
      this.curContestConfig.unshift(JSON.parse(res.data))
      this.contestLevel = []
      this.materialDetail.name = i
      for (let i in this.rules) {
        if (this.rules[i].uuid == item) {
          this.materialDetail.name = this.rules[i].name
        }
      }
    },
    // 更换赛事类型，先获取响应的类型的配置，然后根据配置和当前的赛事等级等重新自动计分
    async handleContestLevelChange (item) {
      this.materialDetail.score = this.getScore(this.curContestConfig[0], item)
      let jstr = {}
      for (let i in item) {
        jstr[i] = item[i]
      }
      this.materialDetail.levelstr = JSON.stringify(jstr)
    },
    // 获取规则
    async getRules() {
      let {data: res} = await this.$http.post("/getRule")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.rules = res.data
    },
    // 获取分值
    getScore(oobj, fobj) {
      let score = 0
      let order = 1
      function findScore(obja, str) {
        if (obja.children != null) {
          for (let i in obja.children) {
            if (obja.children[i].uuid == str) {
              order+=1
              return findScore(obja.children[i], fobj[order])
            }
          }
        } else {
          score = obja.score
        }
      }
      findScore(oobj, fobj[order])
      return score
    },
    // 获取等级路径
    getPathArr(oobj, fobj) {
      let farr = []
      let order = 1
      function findPath(obja, str) {
        if (obja.children != null) {
          for (let i in obja.children) {
            if (obja.children[i].uuid == str) {
              farr.push(obja.uuid)
              order+=1
              return findPath(obja.children[i], fobj[order])
            }
          }
        } else {
          farr.push(obja.uuid)
        }
      }
      findPath(oobj, fobj[order])
      return farr
    },
    // 点击图片新建一个窗口展示图片
    showFullImage(imgurl) {
      window.open(imgurl)
    },
    change(){
      this.$forceUpdate();
    }
  }
}
</script>

<style scoped></style>