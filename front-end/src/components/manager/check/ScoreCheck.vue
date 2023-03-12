<template>
  <div style="width:100%">
    <el-card style="margin-top:10px" shadow="hover">
      <!-- 进行类型的选择 -->
      <div style="margin-bottom:10px">
        <div style="font-size:14px;color:#999999;text-align:right;margin-bottom:10px"><i class="el-icon-info"></i>如果通过审核后需要修改，请前往学生管理-操作-管理该生学分处进行编辑或者删除</div>
        <el-select v-model="curChoice" placeholder="请选择状态" @change="handleChoiceChange">
          <el-option v-for="(item,index) in operations" :key="index" :label="item.name" :value="item.state"></el-option>
        </el-select>
        <el-select style="margin-left:20px" v-model="curYear" placeholder="请选择年份" @change="changeCurYear">
          <el-option v-for="(item,index) in years" :key="index" :label="item" :value="item"></el-option>
        </el-select>
        <el-select style="margin-left:20px" v-model="major" placeholder="请选择专业"  @change="changeCurClass">
          <el-option v-for="(item,index) in classes" :key="index" :label="item.major_name" :value="item.major_code"></el-option>
        </el-select>
      </div>
      <el-table :data="items" style="width:100%" stripe border>
        <el-table-column label="姓名" prop="applier"></el-table-column>
        <el-table-column label="学号" width="140">
          <template slot-scope="props">{{props.row.id.substring(13)}}</template>
        </el-table-column>
        <el-table-column label="申请日期" width="120">
          <template slot-scope="props">{{props.row.id.substring(0,13) | dateFormat}}</template>
        </el-table-column>
        <el-table-column label="类型">
          <template slot-scope="props">{{props.row.name}}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="props">
            <el-tag size="mini" :type="props.row.state==0?'warning':(props.row.state==1?'success':'primary')">{{props.row.state==0?'尚未审核':(props.row.state==1?'审核通过':'修改通过')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核人" prop="checker" width="100"></el-table-column>
        <el-table-column label="审核日期" width="120">
          <template slot-scope="props">{{props.row.state==0?'-':$options.filters.dateFormat(props.row.checktime)}}</template>
        </el-table-column>
        <el-table-column label="查看" width="70">
          <template slot-scope="props">
            <el-button icon="el-icon-edit" circle size="mini" title="查看、编辑与审核" @click="showMaterialDetail(props.row.id)"></el-button>
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
          <el-select :disabled="!showBtn" v-model="materialDetail.ruleid" @change="handleRuleChange" placeholder="请选择">
            <el-option v-for="item in rules" :key="item.uuid" :label="item.name" :value="item.uuid"></el-option>
          </el-select>
        </el-form-item>
        <div>
          <el-form-item label="加分等级">
            <el-cascader :disabled="!showBtn" :key="curKey" v-model="contestLevel" :options="curContestConfig" :props="contestLevelConfigProps" @change="handleContestLevelChange"></el-cascader>
          </el-form-item>
        </div>
        <el-form-item label="材料图片">
          <img style="width:100px; height:100px" :src="materialDetail.imgurl" @click="showFullImage(materialDetail.imgurl)" title="点击查看图片">
        </el-form-item>
        <el-form-item label="加分数值">
          <el-input :disabled="!showBtn" @input="change($event)" type="number" placeholder="请输入分值" clearable v-model="materialDetail.score" style="width:200px"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button v-if="showBtn" type="primary" @click="checkAuthenticationPass" style="width:200px">审核/修改通过</el-button>
          <el-button v-if="showBtn" type="warning" style="width:200px" @click="checkAuthenticationNotPass">审核不通过</el-button>
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
      typeAB: '',
      curChoice: 0,
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
      originalItem: '',
      classes: [],
      curClass: '040104',
      operations: [
        {
          name: '未审核',
          state: 0
        }, {
          name: '审核通过',
          state: 1
        }, {
          name: '修改通过',
          state: 2
        }, {
          name: '全部',
          state: 3
        }
      ],
      major: '',
      rules: [],
      curKey: 0,
      levelstr: '',
      showBtn: true
    }
  },
  watch: {
    curContestConfig(newVal) {
      this.curKey++
    }
  },
  mounted() {
    this.getCurYears()
    this.getAllClasses()
  },
  methods: {
    // 获取当前年
    getCurYears() {
      let y = new Date().getFullYear()
      let m = new Date().getMonth()
      if (m < 2) {
        y -= 1
      }
      for (let i = 0;i<4;i++) {
        this.years.push(y - i + "-" + (y + 1 - i))
      }
      this.curYear = y + "-" + (y + 1)
    },
    // 更换年份
    changeCurYear(item) {
      this.curYear = item
      this.getAllScoreApplications(this.curClass)
    },
    // 切换类型
    handleChoiceChange() {
      // 切换类型之前先检查专业是否选择了
      this.pageConfig.currentPage = 1
      this.pageConfig.pageSize = 10
      this.getAllScoreApplications()
    },
    // 更改分页页数和每页数量
    handleSizeChange(size) {
      this.pageConfig.pageSize = size
      this.getAllScoreApplications(this.curClass)
    },
    // 更改页数
    handleCurrentChange(current) {
      this.pageConfig.currentPage = current
      this.getAllScoreApplications(this.curClass)
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
    // 获取相关的申请
    async getAllScoreApplications() {
      let params = {
        curPage: this.pageConfig.currentPage,
        pageSize: this.pageConfig.pageSize,
        state: this.curChoice,
        year: this.curYear,
        major: this.major
      }
      this.isLoading = true
      let {data: res} = await this.$http.post("/getAllAuthentications", this.$qs.stringify(params))
      this.isLoading = false
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.items = res.data.items
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
      this.materialDetail.name = i
      for (let i in this.rules) {
        if (this.rules[i].uuid == item) {
          this.materialDetail.name = this.rules[i].name
        }
      }
    },
    // 获取某一个材料的详细内容
    async showMaterialDetail(id) {
      this.curId = id
      let {data: res} = await this.$http.post("/getCertainAuthenticationById", this.$qs.stringify({id: id}))
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
      this.materialDetail.score = this.getScore(JSON.parse(res.data.config), levelstr)
      if (res.data.item.state == 1 || res.data.item.state == 2) {
        this.showBtn = false
      }
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
      this.materialDetail.ruleid = ''
      this.materialDetail.imgurl = ''
      this.materialDetail.score = ''
      this.curId = ''
      this.contestLevel = ''
      this.curContestConfig = []
      this.showBtn = true
    },
    // 审核通过材料
    async checkAuthenticationPass() {
      if (this.contestLevel.length <= 0) return this.$msg.error("请选择加分等级")
      // 修改材料信息，然后设置分数
      let originalItemJSON = JSON.parse(this.originalItem)
      originalItemJSON.score = this.materialDetail.score
      if (JSON.stringify(originalItemJSON) == JSON.stringify(this.materialDetail)) {
        this.materialDetail.isModify = 0 // 没有修改，审核通过
      } else {
        this.materialDetail.isModify = 1 // 修改过，修改通过
      }
      let {data: res} = await this.$http.post("/checkAuthenticationPass", this.$qs.stringify(this.materialDetail))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      for (let i=0;i<this.items.length;i++) {
        if (this.items[i].id == this.materialDetail.id) {
          this.items.splice(i, 1)
          break
        }
      }
      this.materialDetailVisible = false
      this.materialDetailDialogClosed()
      return this.$msg.success("审核通过操作成功!")
      // 设置分数
    },
    // 审核材料不通过
    checkAuthenticationNotPass() {
      this.$prompt('删除后无法找回。请输入简短的原因或者提示，系统会自动向学生发送通知。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          closeOnClickModal: false
        }).then(async ({ value }) => {
          if (value === null || value.trim().length === 0) {
            return this.$msg.warning('请输入简短的原因或者提示')
          }
          let {data: res} = await this.$http.post("/checkAuthenticationNotPass", this.$qs.stringify({id: this.curId, content: value}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          for (let i=0;i<this.items.length;i++) {
            if (this.items[i].id == this.curId) {
              this.items.splice(i, 1)
              break
            }
          }
          this.materialDetailVisible = false
          this.materialDetailDialogClosed()
          return this.$msg.success("删除操作成功!")
        }).catch(() => {
          return     
        });
    },
    // 点击图片新建一个窗口展示图片
    showFullImage(imgurl) {
      window.open(imgurl)
    },
    toAddContest() {
      let routerJump = this.$router.resolve({path: '/mag_contest_management',query: {}})
      window.open(routerJump.href, "_blank")
    },
    toRuleConfig() {
      let routerJump = this.$router.resolve({path: '/modify_index_config',query: {}})
      window.open(routerJump.href, "_blank")
    },
    // 更改当前班级
    async changeCurClass(item) {
      this.getAllScoreApplications(item)
    },
    // 获取当前的所有class
    async getAllClasses() {
      let {data: res} = await this.$http.post("/getAllClasses")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.classes = res.data
      this.major = this.classes[0].major_code;
      this.getAllScoreApplications();
    },
    change(){
      this.$forceUpdate();
    }
  }
}
</script>

<style scoped>

</style>