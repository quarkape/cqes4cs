<template>
  <div style="width:100%">
    <el-card style="margin-top:10px" shadow="hover">
      <!-- 进行类型的选择 -->
      <div style="margin-bottom:10px">
        <el-select v-model="curChoice" placeholder="请选择状态" @change="handleChoiceChange">
          <el-option v-for="(item,index) in operations" :key="index" :label="item.name" :value="item.state"></el-option>
        </el-select>
      </div>
      <el-table :data="items" style="width:100%" stripe border>
        <el-table-column label="申请人" prop="name" width="140"></el-table-column>
        <el-table-column label="申请日期" width="120">
          <template slot-scope="props">{{props.row.id.substring(0,13) | dateFormat}}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template slot-scope="props"> 
            <el-tag size="medium" :type="props.row.action==1?'warning':(props.row.action==2?'success':'danger')">{{props.row.action==1?'待审':(props.row.action==2?'通过':'驳回')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请理由" prop="desca"></el-table-column>
        <el-table-column label="驳回理由" prop="descb"></el-table-column>
        <el-table-column label="操作" width="80">
          <template slot-scope="props">
            <el-button icon="el-icon-s-tools" type="success" circle size="mini" title="操作" @click="showMaterialDetail(props.row.id, props.$index)"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:center;margin-top:20px">
        <el-pagination :hide-on-single-page="true" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageConfig.currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pageConfig.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalApps"></el-pagination>
      </div>
    </el-card>

    <!-- 点击查看 -->
    <el-dialog class="history-container" title="学分申请详情" :visible.sync="materialDetailVisible" width="50%" @closed="materialDetailDialogClosed" :close-on-click-modal="false">
      <el-form ref="detailForm" :model="materialDetail" label-width="80px">
        <el-form-item label="加分类型">
          <el-select v-model="materialDetail.ruleid" @change="handleRuleChange" placeholder="请选择">
            <el-option v-for="item in rules" :key="item.uuid" :label="item.name" :value="item.uuid"></el-option>
          </el-select>
        </el-form-item>
        <div>
          <el-form-item label="加分等级">
            <el-cascader ref="originTemplatePath" :key="curKey" v-model="contestLevel" :options="curContestConfig" :props="contestLevelConfigProps" @change="handleContestLevelChange"></el-cascader>
          </el-form-item>
        </div>
        <el-form-item label="材料图片">
          <img style="width:100px; height:100px" :src="materialDetail.imgurl" @click="showFullImage(materialDetail.imgurl)" title="点击查看图片">
        </el-form-item>
        <el-form-item label="加分数值">
          <el-input @input="change($event)" type="number" placeholder="请输入分值" clearable v-model="materialDetail.score" style="width:200px"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="modifyAgainstPass" style="width:200px">修改并通过</el-button>
          <el-button type="danger" style="width:200px" @click="modifyAgainstDismiss">直接驳回</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      items: [],
      curChoice: 1,
      pageConfig: {
        currentPage: 1,
        pageSize: 10
      },
      totalApps: 0,
      isLoading: false,
      materialDetailVisible: false,
      materialDetail: {
        score: 0
      },
      // 动态修改弹窗的标题
      curId: '',
      // 加载材料的时候显示加载图标
      loading: false,
      curContestConfig: [],
      contestConfig: [],
      contestLevel: '',
      contestLevelConfigProps: {
        expandTrigger: 'hover',
        value: 'uuid',
        children: 'children',
        label: 'name'
      },
      autoScoreConfigs: [],
      autoScoreConfigNames: [],
      curRule: '默认',
      curYear: '',
      years: [],
      // 用来判断管理员是否对用户信息进行了修改
      originalItem: {},
      classes: [],
      curClass: '040104',
      operations: [
        {
          name: '待处理',
          state: 1
        }, {
          name: '已通过',
          state: 2
        }, {
          name: '已驳回',
          state: 3
        }, {
          name: '全部',
          state: 4
        }
      ],
      major: '',
      rules: [],
      curKey: 0,
      levelstr: ''
    }
  },
  mounted() {
    // this.getCurYears()
    this.getAgainstList()
    // this.getAllClasses()
  },
  watch: {
    curContestConfig(val) {
      this.curKey++
    }
  },
  methods: {
    // 切换类型
    handleChoiceChange() {
      this.pageConfig.currentPage = 1
      this.pageConfig.pageSize = 10
      this.getAgainstList()
    },
    // 更改分页页数和每页数量
    handleSizeChange(size) {
      this.pageConfig.pageSize = size
      this.getAgainstList(this.curClass)
    },
    // 更改页数
    handleCurrentChange(current) {
      this.pageConfig.currentPage = current
      this.getAgainstList(this.curClass)
    },
    // 更换赛事类型，先获取响应的类型的配置，然后根据配置和当前的赛事等级等重新自动计分
    async handleContestLevelChange (item) {
      // this.curContestConfig = {}
      this.materialDetail.score = this.getScore(this.curContestConfig[0], item)
      let jstr = {}
      for (let i in item) {
        jstr[i] = item[i]
      }
      this.materialDetail.levelstr = JSON.stringify(jstr)
    },
    // 获取相关的申请
    async getAgainstList() {
      let params = {
        curPage: this.pageConfig.currentPage,
        pageSize: this.pageConfig.pageSize,
        action: this.curChoice
      }
      this.isLoading = true
      let {data: res} = await this.$http.post("/getAgainstList", this.$qs.stringify(params))
      this.isLoading = false
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.items = res.data.list
      this.totalApps = res.data.total
    },
    // 修改规则
    async handleRuleChange(item) {
      let {data: res} = await this.$http.post("/getContestConfigByUuid", this.$qs.stringify({uuid: item}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.curContestConfig = []
      this.curContestConfig.unshift(JSON.parse(res.data))
      this.contestLevel = []
      // this.materialDetail.name = i
      for (let i in this.rules) {
        if (this.rules[i].uuid == item) {
          this.materialDetail.name = this.rules[i].name
        }
      }
    },
    // 获取某一个材料的详细内容
    async showMaterialDetail(id, index) {
      this.curId = id
      this.curIndex = index
      // let {data: res} = await this.$http.post("/getCertainAuthenticationById", this.$qs.stringify({id: id}))
      let {data: res} = await this.$http.post("/getCertainAuthenticationByIdWithScore", this.$qs.stringify({id: id}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.materialDetail = res.data.item
      // 设置初始originalItem
      this.materialDetail.imgurl = this.$surl + this.materialDetail.imgurl
      this.materialDetail.score = res.data.score
      this.originalItem.score = res.data.score
      // console.log(this.materialDetail)
      // console.log(this.originalItem)
      this.originalItem = JSON.stringify(this.materialDetail)
      this.curContestConfig.push(JSON.parse(res.data.config))
      // 获取规则列表
      this.getRules()
      // 生成等级路径
      let levelstr = JSON.parse(res.data.item.levelstr)
      this.contestLevel = this.getPathArr(JSON.parse(res.data.config), levelstr)
      // 查找分值
      // this.materialDetail.score = this.getScore(JSON.parse(res.data.config), levelstr)
      // 如果当前加分申请是直接通过或者修改通过的话，则不再提供审核通过按钮
      // if (res.data.item.state == 1 || res.data.item.state == 2) {
      //   this.showBtn = false
      // }
      // if (res.data.item.state == 2) {
      //   this.showBtn = false
      // }
      this.materialDetailVisible = true
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
    // 获取规则
    async getRules() {
      let {data: res} = await this.$http.post("/getRule")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.rules = res.data
    },
    // 关闭材料详情页面的时候将所有数据清空
    materialDetailDialogClosed() {
      this.materialDetail.ruleid = null
      this.materialDetail.imgurl = null
      this.materialDetail.score = 0 
      this.curId = null
      this.curIndex = 0
      this.contestLevel = []
      this.curContestConfig = []
      this.originalItem = {}
      // this.$refs.originTemplatePath.$refs.panel.clearCheckedNodes()
    },
    // 审核通过
    modifyAgainstDismiss() {
      this.$prompt('请简要阐述理由', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async ({ value }) => {
        if (value === null || value.trim().length === 0) {
          return this.$msg.warning('请简要阐述理由')
        }
        let {data: res} = await this.$http.post("/modifyAgainstDismiss", this.$qs.stringify({id: this.curId, descb: value, name: this.materialDetail.name}))
        if (!res || res.code != 0) {
          this.materialDetailVisible = false
          this.materialDetailDialogClosed()
          return res==null?false:this.$msg.error(res.message)
        }
        // if (res.code != 0) return this.$msg.error(res.message)
        this.items[this.curIndex].action = 3
        this.items[this.curIndex].descb = value
        this.materialDetailVisible = false
        this.materialDetailDialogClosed()
        return this.$msg.success("操作成功")
      }).catch((err) => {return;});
    },
    // 驳回
    async modifyAgainstPass(e) {
      if (this.contestLevel.length <= 0) return this.$msg.error("请选择加分等级")
      // 修改材料信息，然后设置分数
      let originalItemJSON = JSON.parse(this.originalItem)
      console.log(JSON.stringify(originalItemJSON))
      console.log(JSON.stringify(this.materialDetail))
      // return;
      if (JSON.stringify(originalItemJSON) == JSON.stringify(this.materialDetail)) {
        // this.materialDetail.isModify = 0 // 没有修改，审核通过
        return this.$msg.warning("不做修改时直接驳回即可")
      } else {
        // this.materialDetail.isModify = 1 // 修改过，修改通过
      }
      let {data: res} = await this.$http.post("/modifyAgainstPass", this.$qs.stringify(this.materialDetail))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      // for (let i=0;i<this.items.length;i++) {
      //   if (this.items[i].id == this.materialDetail.id) {
      //     this.items.splice(i, 1)
      //     break
      //   }
      // }
      this.items[this.curIndex].action = 2
      this.materialDetailVisible = false
      this.materialDetailDialogClosed()
      return this.$msg.success("操作成功!")
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

<style scoped>

</style>