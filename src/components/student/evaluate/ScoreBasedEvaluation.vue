<template>
  <div style="width:100%">
    <div class="trend">
      <div>
        <span style="font-size:14px;padding-right:20px">学年:</span>
        <el-select v-model="curYear" placeholder="请选择" @change="handleYearChange">
          <el-option v-for="(item,index) in years" :key="index" :label="item" :value="item"></el-option>
        </el-select>
      </div>
    </div>

    <!-- 通过一定的分析公式，计算出当前的学生的综合素质总分 -->
    <el-divider content-position="left"><i class="el-icon-s-opportunity"></i>&nbsp;综合得分</el-divider>
    <el-card shadow="hover">
      <div class="general" v-if="hasResult">
        <div class="general-item" v-for="(item, index) in generalData" :key="item.name">
          <span :style="'color:#'+colorsA[index]">{{item.score}}</span>
          <span>{{item.name}}</span>
        </div>
      </div>
      <div class="no-data-sty" v-else>本学年暂无数据</div>
    </el-card>

    <!-- 展示加分材料，一般是直接从加分证明材料中获取展示 -->
    <el-divider content-position="left"><i class="el-icon-s-opportunity"></i>&nbsp;加分详情</el-divider>
    <el-card shadow="hover">
      <!-- <div style="margin-bottom:10px;font-size:14px;color:#999999;text-align:right"><i class="el-icon-info"></i>可在学分管理中查看学分申请的详细信息</div> -->
      <div style="margin-bottom:10px;font-size:14px;color:#999999;text-align:right"><i class="el-icon-info"></i>驳回后需要再次提出异议请联系管理员</div>
      <el-table :data="addScoreTable" style="width: 100%" border stripe>
        <el-table-column label="#" width="60" type="index"></el-table-column>
        <el-table-column prop="name" label="类型"></el-table-column>
        <el-table-column prop="score" label="加分" width="100"></el-table-column>
        <el-table-column label="异议状态" width="120">
          <template slot-scope="props">
            <el-tag size="medium" :type="props.row.action==0?'success':(props.row.action==1?'warning':(props.row.action==2?'primary':'danger'))">{{props.row.action==0?'正常':(props.row.action==1?'待审核':(props.row.action==2?'通过':'驳回'))}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="异议操作" width="120">
          <template slot-scope="props">
            <el-button v-if="props.row.action===0" icon="el-icon-s-flag" type="warning" size="mini" @click="submitAgainst(props.row.id, props.$index)" title="向教学管理员提出异议"></el-button>
            <el-button v-if="props.row.action===1" type="danger" icon="el-icon-remove" size="mini" @click="recallAgainst(props.row.id, props.$index)" title="撤回异议申请"></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      colorsA: ["f37b1d", "fbbd08", "8dc63f", "1cbbb4", "9c26b0", "a5673f", "39b54a", "0081ff"],
      generalData: [
        {
          name: "学业得分",
          score: 0
        },
        {
          name: "其他得分",
          score: 0
        },
        {
          name: "手动加分",
          score: 0
        },
        {
          name: "手动扣分",
          score: 0
        },
        {
          name: "综合得分",
          score: 0
        }
      ],
      addScoreTable: [],
      rewardsAssignment: [],
      rewardsAssignmentDialogVisible: false,
      qualifiedRewards: [],
      qualifiedRewardsDialogVisible: false,
      types: [],
      years: [],
      curYear: '',
      hasResult: false,
      fames: []
    }
  },
  created() {
    this.getYears();
    this.getAddScoreMaterialDetail();
    this.getFinalGrade()
    // this.getFameList()
  },
  methods: {
    // 获取当前年份
    getYears() {
      let d = new Date();
      let y = d.getFullYear()
      let m = d.getMonth()
      if (m < 2) {
        y = y - 1
      }
      for (let i=0;i<4;i++) {
        this.years.push((y - i) + '-' + (y - i + 1))
      }
      this.curYear = y + '-' + (y + 1)
    },
    // 获取所有加分申请
    async getAddScoreMaterialDetail() {
      let {data: res} = await this.$http.post("/getAddScoreMaterialDetail", this.$qs.stringify({year: this.curYear}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      // console.log(res.data)
      this.addScoreTable = res.data
    },
    // 获取综合成绩和学业成绩及其排名
    async getFinalGrade() {
      let {data: res} = await this.$http.post("/getFinalGradeById", this.$qs.stringify({year: this.curYear}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      if (res.data == null) {
        this.hasResult = false
        return;
      }
      this.hasResult = true
      this.generalData[0].score = res.data.score_main
      this.generalData[1].score = res.data.score_other
      this.generalData[2].score = res.data.score_plus
      this.generalData[3].score = res.data.score_sub
      this.generalData[4].score = res.data.score_final
    },
    // 获取当前的奖学金相关设置
    async getFameList() {
      let {data: res} = await this.$http.post("/getFameList")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.fames = res.data
    },
    // 提交学分异议
    submitAgainst(id, index) {
      this.$prompt('请简要阐述理由', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(async ({ value }) => {
          if (value === null || value.trim().length === 0) {
            return this.$msg.warning('请简要阐述理由')
          }
          let {data: res} = await this.$http.post("/submitAgainst", this.$qs.stringify({id: id, desc: value}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          this.addScoreTable[index].action = 1
          return this.$msg.success("操作成功，请耐心等待审核")
        }).catch(() => {return});
    },
    // 撤销学分申请异议
    recallAgainst(id, index) {
      this.$confirm('是否确认撤回?', '确定', '取消',).then(async () => {
          let {data: res} = await this.$http.post("/recallAgainst", this.$qs.stringify({id: id}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          this.addScoreTable[index].action = 0
          return this.$msg.success("操作成功!")
        }).catch((error) => {
          return     
        });
    },
    async showHonorNumConfig() {
      let {data: res} = await this.$http.post("/getHonorNumConfig")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      // console.log(res.data)
      this.rewardsAssignment = res.data
      this.rewardsAssignmentDialogVisible = true
    },
    applyRewards() {
      this.$confirm('是否确认提交申请?', '确定', '取消').then(async () => {
          this.qualifiedRewardsDialogVisible = false
          return this.$msg.success("申请提交成功!")
        })
    },
    handleYearChange(item) {
      this.getAddScoreMaterialDetail()
      this.getFinalGrade()
    }
  }
}
</script>

<style lang="less" scoped>
h3 {
  margin: 0;
}
.trend {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 10px;
}
.general {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  height: 60px;
  .general-item {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
    width: 50%;
    height: 60px;
    > span:first-child {
      font-size: 30px;
      font-weight: bold;
      > span {
        font-size: 16px;
      }
    }
    > span:last-child {
      font-size: 14px;
      color: #606266;
    }
  }
}
.header-container {
  width: 100%;
  height: 130px;
  overflow: hidden;
}
.header {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 130px;
  div:first-child {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 6px;
  }
}
.avatar {
  text-align: center;
}
.avatar img {
  width: 100px;
  height: auto;
}
.eduexp {
  font-size: 15x;
  margin-top: 6px;
}
.divider {
  width: 100%;
  height: 2px;
  background-color: #333;
  margin: 2px 0;
}
.area-title {
  margin: 0;
}
.sides {
  float: right;
}
.items {
  line-height: 26px;
}
.no-data-sty {
  font-size: 14px;
  color: #999999;
  text-align: center;
}
</style>