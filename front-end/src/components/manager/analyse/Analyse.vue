<template>
  <div style="width:100%">
    <el-card shadow="hover">
      <div>
        <el-select v-model="curYear" placeholder="选择学年" @change="handleYearChange">
          <el-option v-for="(item,index) in years" :key="index" :label="item" :value="item"></el-option>
        </el-select>
        <el-select style="margin-left:24px" v-model="curMajor" placeholder="选择专业" @change="handelMajorChange">
          <el-option v-for="item in classes" :key="item.uuid" :label="item.major_name" :value="item.major_code"></el-option>
        </el-select>
      </div>
      <div v-show="hasData" style="margin-top:36px">
        <div id="myChart" :style="{width:'100%',height:'300px'}"></div>
      </div>
    </el-card>
  </div>
</template>

<script>
  // 引入基本模板
let echarts = require('echarts/lib/echarts')
// 引入折线图组件
require('echarts/lib/chart/bar')
// 引入提示框和title组件
require('echarts/lib/component/tooltip')
require('echarts/lib/component/title')
export default {
  data() {
    return {
      years: [],
      curYear: '',
      classes: [],
      curMajor: '',
      indexOriginal: [],
      indexs: [],
      table: [],
      myChart: null,
      hasData: true
    }
  },
  mounted() {
    this.getCurYears()
    this.getAllClasses()
    this.getIndexs()
    this.initChart()
  },
  methods: {
    initChart() {
      this.myChart = echarts.init(document.getElementById('myChart'))
    },
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
    // 获取当前的所有class
    async getAllClasses() {
      let {data: res} = await this.$http.post("/getAllClasses")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.classes = res.data
    },
    async getIndexData() {
      let {data: res} = await this.$http.post("/getIndexData", this.$qs.stringify({year: this.curYear,code:this.curMajor}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      if (res.data.length == 0) {
        this.hasData = false
        this.$msg.warning("暂无数据")
        return
      } else {
        this.hasData = true
      }
      let indexsTemp = JSON.parse(JSON.stringify(this.indexOriginal))
      let indexLength = Object.keys(res.data).length
      for (let i in indexsTemp) {
        let c = 1
        for (let j in res.data) {
          if (indexsTemp[i].uuid == res.data[j].uuid) {
            this.table[i] = res.data[j].count
            break
          }
          if (c == indexLength) {
            this.table[i] = 0
          }
          c++
        }
      }
      this.drawLine()
    },
    handelMajorChange() {
      this.getIndexData()
    },
    handleYearChange() {
      this.getIndexData()
    },
    drawLine() {
      let options = {
        grid: {
            top: '5%',
            left: '0', 
            right: '0',
            bottom: '5%',
            containLabel: true
        },
        xAxis:{
          axisLine: {
            lineStyle: {
              color:  '#606266' ,
            }
          },
          data: this.indexs
        },
        yAxis:{
          type: 'value'
        },
        series:[{
          name:'数量',
          type:'bar',
          data:this.table,
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180, 180, 180, 0.2)'
          },
          barWidth: 46,
          itemStyle: {
            normal: {
              color: '#555666'
            }
          }
        }],
        tooltip : {
          trigger: 'axis',
        }
      }
      this.myChart.setOption(options)
    },
    async getIndexs() {
      let {data: res} = await this.$http.post("/getIndexs")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.indexOriginal = res.data
      for (let i in res.data) {
        this.indexs.push(res.data[i].name)
      }
    }
  }
}
</script>

<style lang="less" scoped>
.null-data {
  width: 100%;
  margin: 40px 0;
  text-align: center;
  font-size: 14px;
  color: #555666;
}
</style>