<template>
  <div style="width:100%">
    <!-- 每一个板块的得分 -->
    <!-- 相比去年，每一个板块提高了多少分 -->
    <!-- 每一个板块在班级或者专业的排名 -->
    <!-- 新增了多少个资格证书，新增了多少个社会实践 -->
    <div style="text-align:right;margin-bottom:10px">
      <el-button type="primary" size="medium" @click="getPdff()"><i class="el-icon-s-order"></i>&nbsp;&nbsp;导出报告</el-button>
      <el-button type="success" size="medium" @click="getPdff()"><i class="el-icon-s-order"></i>&nbsp;&nbsp;编辑简历</el-button>
    </div>
    <el-card id="pdfDom" shadow="hover" class="mark_bg">
      <div class="mark_bgg"></div>
      <div style="z-index:1;">
        <div class="logo">个人简历</div>
        <div class="header">
          <div>10000000001</div>
          <div>王浩</div>
          <div>教育技术学</div>
          <div>第三学年</div>
        </div>

        <div class="area">
          <div class="title"><i class="el-icon-data-analysis"></i>&nbsp;&nbsp;得分情况</div>
          <div class="divider"></div>
          <div v-for="(item, key, index) in scores">
            你本学年的{{typesConfig[key]}}分数为：<b>{{item}}</b>
          </div>
        </div>
        
        <div class="area">
          <div class="title"><i class="el-icon-guide"></i>&nbsp;&nbsp;前后对比</div>
          <div class="divider"></div>
          <div v-for="(item, key, index) in scoresLastYear">
            你上个学年的{{typesConfig[key]}}分数为：<b>{{item}}</b>，今年{{scoreContrast[key]>=0?"提升":"降低"}}了&nbsp;<b>{{scoreContrast[key]>=0?scoreContrast[key]:-scoreContrast[key]}}&nbsp;</b>分
            <!-- 你上个学年的&nbsp;{{typesConfig[key]}}&nbsp;分数为：{{item}}，今年&nbsp;{{item>=3?"降低":"提升"}} -->
          </div>
        </div>
        
        <div class="area">
          <div class="title"><i class="el-icon-trophy"></i>&nbsp;&nbsp;排名情况</div>
          <div class="divider"></div>
          <div v-for="(item, key, index) in areaRank">
            你本学年的{{typesConfig[key]}}得分在本年级本专业排名第&nbsp;<b>{{item}}</b>&nbsp;，相比上一学年上升&nbsp;<b>{{areaRankLastYear[key]-item}}</b>&nbsp;个名次
          </div>
        </div>
        
        <div class="area">
          <div class="title"><i class="el-icon-cpu"></i>&nbsp;&nbsp;综测情况</div>
          <div class="divider"></div>
          <div>你的综合素质的分为<b>&nbsp;{{totalScore}}</b>，在专业排名<b>&nbsp;第{{totalRank}}&nbsp;</b>，相比去年<b>&nbsp;{{totalIncrease>=0?'增加':'降低'}}&nbsp;</b>了<b>&nbsp;{{totalIncrease>=0?totalIncrease:-totalIncrease}}&nbsp;</b>分，{{totalIncrease>=0?'上升':'下降'}}了<b>&nbsp;{{totalRankIncrease>=0?totalRankIncrease:-totalRankIncrease}}&nbsp;</b>个名次</div>
        </div>

        <div class="area">
          <div class="title"><i class="el-icon-magic-stick"></i>&nbsp;&nbsp;其他情况</div>
          <div class="divider"></div>
          <div><i class="el-icon-caret-right"></i>&nbsp;你在今年考取了资格证书<b>&nbsp;2&nbsp;</b>个：教师资格证、英语六级证书</div>
          <div><i class="el-icon-caret-right"></i>&nbsp;你在今年参加了各类赛事<b>&nbsp;4&nbsp;</b>项：全国大学生数学建模大赛，西南大学数学建模比赛，全国大学生计算机设计大赛，西沃杯名师互动比赛</div>
          <div><i class="el-icon-caret-right"></i>&nbsp;你在今年没有违反校纪校规记录</div>
        </div>
        <div class="footer">报告生成时间:{{dateNow}}</div>
      </div>
    </el-card>
  </div>
</template>

<script>
  import {getPdf} from '../../../plugins/html2Pdf.js'
  export default {
    data() {
      return {
        typesConfig: {
          cqa: '思想品德',
          cqb: '身心素质',
          cqc: '学业成绩',
          cqd: '专业技能',
          cqe: '创新创业',
          cqf: '生活实践'
        },
        // 今年每一项综合素质的分数
        scores: {},
        // 去年每一项综合素质的分数
        scoresLastYear: {},
        // 每一项素质得分相比去年增加的值
        scoreContrast: [],
        // 综测成绩
        totalScore: 0,
        // 综测成绩增加
        totalIncrease: 0,
        // 综测成绩排名
        totalRank: 1,
        // 综测成绩排名增加
        totalRankIncrease: 0,
        // 今年的每一项综合素质的专业排名
        areaRank: {
          cqa: 12,
          cqb: 6,
          cqc: 1,
          cqd: 1,
          cqe: 1,
          cqf: 6
        },
        // 去年的每一项综合素质的专业排名
        areaRankLastYear: {
          cqa: 14,
          cqb: 7,
          cqc: 1,
          cqd: 1,
          cqe: 1,
          cqf: 4
        },
        htmlTitle: '评价报告-王浩',
        dateNow: null
      }
    },
    mounted() {
      // this.getStudentComprehensiveQualityScores()
      this.generateExportDate();
    },
    methods: {
      generateExportDate() {
        this.dateNow = new Date().toLocaleString();
        window.alert("简历功能正在建设中，当前功能并不实际可用~")
      },
      getPdff() {
        // 修改报告导出实践
        this.dateNow = new Date().toLocaleString();
        let a = document.getElementById("pdfDom")
        window.scrollTo(0, 0);
        getPdf(this.htmlTitle);
      },
      // 获取学生各个综合素质分数
      async getStudentComprehensiveQualityScores() {
        let {data: res} = await this.$http.post("/getStudentComprehensiveQualityScores")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.scores = res.data
        for (let i in this.scores) {
          this.totalScore += this.scores[i]
        }
        this.getStudentComprehensiveQualityScoresLastYear()
      },
      // 获取学生去年的各个综合素质分数
      async getStudentComprehensiveQualityScoresLastYear() {
        let {data: res} = await this.$http.post("/getStudentComprehensiveQualityScoresLastYear")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.scoresLastYear = res.data
        let totalScoreLastYear = 0
        for (let i in this.scoresLastYear) {
          totalScoreLastYear += this.scoresLastYear[i]
        }
        // 计算两年的总分差
        this.totalIncrease = (this.totalScore - totalScoreLastYear).toFixed(2)
        // 计算每一项素质的差
        for (let i in this.scores) {
          this.scoreContrast[i] = (this.scores[i] - this.scoresLastYear[i]).toFixed(2)
        }
      },
      // 获取学生的综合素质分数总排名
      async getTotalRank() {
        let {data: res} = await this.$http.post("/getTotalRank")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.totalRank = res.data
      }
    }
  }
</script>

<style scoped>
  .mark_bg {
    position: relative;
    padding: 20px;
    border:none;
    min-height:1381.43px
  }
  .mark_bgg {
    position: absolute;
    width: 200%;
    height: 200%;
    content: '';
    background-image: url(../../../assets/images/evaluate_report_mark.png);
    /* background-repeat: no-repeat; */
    background-size: 500px auto;
    left: -50%;
    top: -50%;
    transform: rotatez(-30deg);
    transform-origin: center center;
  }
  .logo {
    font-size: 40px;
    font-weight: bolder;
    text-align: center;
    margin-bottom: 10px;
  }
  .header {
    display: flex;
    flex-direction: row;
    justify-content: center;
    margin: 10px 0 20px 0;
    height: 30px;
  }
  .header > div {
    padding: 0 20px;
    font-weight: 400;
    position: relative;
    height: 30px;
    line-height: 30px;
  }
  .header > div::after {
    position: absolute;
    content: '';
    height: 16px;
    width: 1px;
    background-color: #000;
    right: 0;
    top: 7px;
  }
  .header > div:last-child {
    margin-right: 0;
  }
  .area {
    margin-bottom: 10px;
  }
  .area > div {
    line-height: 26px;
  }
  .title {
    font-weight: bolder;
    font-size: 20px;
    margin: 10px 0 4px 0;
  }
  .divider {
    width: 100%;
    height: 1px;
    background-color: #000;
  }
  .footer {
    text-align: right;
    font-size: 14px;
    margin-top: 50px;
    color: #999;
  }
</style>