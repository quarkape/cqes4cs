<template>
  <div style="width:100%">
    <el-card style="margin-top:20px" shadow="hover">
      <span style="font-size: 14px;color:#999;">当前学年：</span>
      <el-select v-model="curYear" style="margin-bottom:20px" placeholder="请选择" @change="handleYearChange">
        <el-option v-for="(item,index) in years" :key="index" :label="item" :value="item"></el-option>
      </el-select>
      <el-table :data="evaluateResult" style="width: 100%" border stripe>
        <el-table-column prop="rank" label="排名" width="60">
          <template slot-scope="props">
            <span :style="(props.row.rank<4?'font-weight:bolder':'')+';color:'+(props.row.rank==1?'#FE2D46':(props.row.rank==2?'#F60':(props.row.rank==3?'#FAA90E':'')))">{{props.row.rank}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名">
          <template slot-scope="props">
            <span :style="(props.row.rank<4?'font-weight:bolder':'')+';color:'+(props.row.rank==1?'#FE2D46':(props.row.rank==2?'#F60':(props.row.rank==3?'#FAA90E':'')))">{{props.row.name}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="学号" width="160"></el-table-column>
        <el-table-column prop="score_main" label="学业得分" width="90"></el-table-column>
        <el-table-column prop="score_other" label="其他加分" width="90"></el-table-column>
        <el-table-column prop="score_plus" label="手动加分/扣分" width="120">
          <template slot-scope="props">
            <span>{{props.row.score_plus}} / {{props.row.score_sub}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="score_final" label="综合得分" width="100"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="props">
            <el-button icon="el-icon-view" type="primary" circle size="mini" @click="showAuthenticationDetail(props.row.id)" title="查看加分内容"></el-button>
            <el-button icon="el-icon-plus" type="warning" circle size="mini" @click="getScoreManually(props.row.id)" title="查看手动加分/减分项"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="tips"><i class="el-icon-info" style="color:#333"></i>&nbsp;&nbsp;综合得分相同时，按照学业成绩得分排序，学业成绩得分相同时，按照科创能力得分排序</div>
    </el-card>

    <el-dialog class="history-container" :close-on-click-modal="false" title="加分项" width="50%" :visible.sync="authenticationDetailDialogVisible" @closed="closeAuthenticationDetailDialog">
      <el-table :data="authentications" style="width: 100%" border stripe>
        <el-table-column label="#" width="60" type="index"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="score" label="加分" width="80"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="props">
            <el-button icon="el-icon-s-flag" plain type="primary" size="mini" @click="openAuthenticationImg(props.row.imgurl)" title="查看图片">查看图片</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:10px;color:#999;font-size:14px;"><i class="el-icon-info"></i>&nbsp;“加分”一项是赋权之后的计算结果</div>
      </el-dialog>
    </el-dialog>

    <el-dialog class="history-container" :close-on-click-modal="false" title="手动加分/减分项" width="50%" :visible.sync="scoreManuallyVisible" @closed="scoreManuallyDialogClosed">
      <el-table :data="scoreManuallyTable" border strip>
        <el-table-column type="index" width="80"></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="props">
            <el-tag size="mini" :type="props.row.type==0?'warning':'success'">{{props.row.type==0?'扣分':'加分'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="100"></el-table-column>
        <el-table-column prop="desc" label="描述"></el-table-column>
      </el-table>
    </el-dialog>
    
  </div>
</template>

<script>
  export default {
    data() {
      return {
        years: [],
        curYear: "",
        evaluateResult: [],
        currentStu: '尚未获得该生资料',
        authenticationDetailDialogVisible: false,
        authentications: [],
        types: ['思想品德', '身心发展', '学业成绩', '专业技能', '科创能力', '生活实践'],
        scoreManuallyVisible: false,
        scoreManuallyTable: []
      }
    },
    mounted() {
      this.getCurYears()
      this.getAllStudentScore()
    },
    methods: {
      getCurYears() {
        let y = new Date().getFullYear()
        let m = new Date().getMonth()
        if (m < 2) {
          y = y -1
        }
        for (let i=0;i<4;i++) {
          this.years.push(y - i + "-" + (y - i + 1))
        }
        this.curYear = y + '-' + (y + 1)
      },
      // 获取所有学生的学分成绩并进行排名显示
      async getAllStudentScore() {
        let {data: res} = await this.$http.post("/getAllStudentScoreThemselves", this.$qs.stringify({year: this.curYear}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.evaluateResult = res.data
      },
      // 展开某个学生的所有加分材料
      async showAuthenticationDetail(id) {
        this.authenticationDetailDialogVisible = true
        let {data: res} = await this.$http.post("/getStudentAllScoreMaterials", this.$qs.stringify({id:id,year:this.curYear}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        for (let i in res.data) {
          res.data[i].imgurl = this.$surl + res.data[i].imgurl
        }
        this.authentications = res.data
      },
      // 获取某学生的手动加分项
      async getScoreManually(userid) {
        let {data: res} = await this.$http.post("/getScoreManually", this.$qs.stringify({userid: userid,year:this.curYear}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.scoreManuallyTable = res.data
        this.scoreManuallyVisible = true
      },
      // 在新的标签页打开证明材料图片
      openAuthenticationImg(url) {
        window.open(url);
      },
      // 关闭查看学生详细资料的弹窗
      closeAuthenticationDetailDialog() {
        this.authentications = []
      },
      // 年份发生改变时
      handleYearChange() {
        this.getAllStudentScore()
      },
      // 关闭查看手动加分弹窗
      scoreManuallyDialogClosed() {
        this.scoreManuallyTable = []
      }
    }
  }
</script>

<style scoped>
  .auth_details {
    display: flex;
    justify-content: flex-start;
  }
  .auth_detail {
    margin-left: 30px;
  }
  .auth_detail > div {
    line-height: 30px;
  }
  .tips {
    text-align: left;
    font-size: 14px;
    color: #999999;
    margin-top: 10px;
  }
</style>