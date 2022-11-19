<template>
  <div style="width:100%">
    <!-- 其他按钮 -->
    <div class="trend">
        <el-button type="primary" size="medium" icon="el-icon-s-order"  @click="dialogVisible=true">学分申请</el-button>
        <el-button type="warning" size="medium" icon="el-icon-s-order"  @click="openEvaluateFile()">查看综评文件</el-button>
    </div>
    
    <el-card shadow="hover" style="margin-top:20px;">
      <div>
        <span style="padding-right:20px;font-size:14px">请选择学年:</span>
        <el-select v-model="curYear" placeholder="请选择" @change="handleYearChange">
          <el-option v-for="(item,index) in years" :key="index" :label="item" :value="item"></el-option>
        </el-select>
      </div>
      
      <!-- 具体的材料表格 -->
      <el-table :data="items" border style="margin-top:20px;">
        <el-table-column type="index" label="#"></el-table-column>
        <el-table-column prop="name" label="名称">
          <template slot-scope="props">
            {{props.row.name}}
          </template>
        </el-table-column>
        <el-table-column prop="date" label="状态" width="120">
          <template slot-scope="props">
            <el-tag size="medium" :type="props.row.state==0?'warning':'success'">{{props.row.state==0?'未审':'已审'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="日期" width="160">
          <template slot-scope="props">{{props.row.id.substring(0,13) | dateFormat}}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="props">
            <el-button circle type="primary" size="mini" @click="showMaterialDetail(props.row.id)" title="查看"><i class="el-icon-view"></i></el-button>
            <el-button v-if="props.row.state===0" circle type="danger" size="mini" @click="deleteMaterial(props.row.id)" title="撤回"><i class="el-icon-delete"></i></el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :hide-on-single-page="true" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageConfig.curPage" :page-sizes="[5, 10, 20, 50]" :page-size="pageConfig.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalScores" background style="margin-top: 20px;text-align: center;"></el-pagination>
    </el-card>

    <!-- 查看申请详情的dialog -->
    <el-dialog class="detail-dialog" :close-on-click-modal="false" title="申请加分详情" :visible.sync="applicationDetailVisible" width="40%" @closed="applicationDetailClosed">
      <div>名称：{{detailItem.name}}</div>
      <div>提交时间：{{detailItem.id.substring(0,13) | dateFormat}}</div>
      <div>审核时间：{{detailItem.checktime==null?'尚未审核':$options.filters.dateFormat(detailItem.checktime)}}</div>
      <div>状态：{{detailItem.state==0?'未审':(detailItem.state==1?'审核通过':'修改通过')}}</div>
      <div>年份：{{detailItem.year}}</div>
      <div>
        <span>材料图片:</span>
      </div>
      <div>
        <el-image style="width: 100px; height: 100px" :src="detailItem.imgurl" @click="showFullImage(detailItem.imgurl)"></el-image>
      </div>
    </el-dialog>

    <!-- 加分的dialog -->
    <!-- <scoreapp :dialogVisible.sync="dialogVisible" @closeDialog="dialogVisible=false"></scoreapp> -->
    <scoreapp :dialogVisible.sync="dialogVisible" @addNewItem="addNewItem" @closeDialog="closeDialogFunc"></scoreapp>
  </div>
</template>

<script>
  import ScoreApplication from '../components/ScoreApplication.vue'
  export default {
    components: {'scoreapp': ScoreApplication},
    data() {
      return {
        years: [],
        curYear: '',
        items: [],
        materialDialogVisiable: false,
        pageConfig: {
          curPage: 1,
          pageSize: 10
        },
        totalScores: 0,
        applicationDetailVisible: false,
        detailItem: {
          name: '',
          level: '',
          id: '',
          checktime: ''
        },
        dialogVisible: false
      }
    },
    mounted() {
      this.getYears()
      this.getAuthenticationsById()
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
      addNewItem(list, callback) {
        // 如果成功提交申请，直接插入一条新纪录
        // 防止插入两次
        if (list.length !== 0) {
          let newItem = {
            'name': list[1],
            'id': list[0],
            'state': 0
          }
          this.items.unshift(newItem)
        }
        this.dialogVisible = false
        callback('done');
      },
      closeDialogFunc() {
        this.dialogVisible = false
      },
      applicationDetailClosed() {
        this.detailItem = {
          name: '',
          level: '',
          id: '',
          checktime: ''
        }
      },
      // 获取该生的所有加分申请
      async getAuthenticationsById() {
        let params = {
          curPage: this.pageConfig.curPage,
          pageSize: this.pageConfig.pageSize,
          year: this.curYear
        }
        let {data: res} = await this.$http.post("/getAuthenticationsById", this.$qs.stringify(params))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.items = res.data.items
        this.totalScores = res.data.total
      },
      // 根据id获取某一个申请的详细内容
      async showMaterialDetail(id) {
        let {data: res} = await this.$http.post("/getAuthenticationById", this.$qs.stringify({id: id}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.detailItem = res.data
        this.detailItem.imgurl = this.$surl + this.detailItem.imgurl
        console.log(this.detailItem.imgurl)
        this.applicationDetailVisible = true
      },
      // 删除尚未审核的学分申请
      async deleteMaterial(id) {
        this.$confirm('该申请尚未审核，是否确认删除?', '确定', '取消',).then(async () => {
          // 学生自己删除一项学分申请
          let {data: res} = await this.$http.post("/checkAuthenticationNotPassStudent", this.$qs.stringify({id: id}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          for (let i=0;i<this.items.length;i++) {
            if (this.items[i].id == id) {
              this.items.splice(i, 1)
              break
            }
          }
          return this.$msg.success("删除操作成功!")
        }).catch((error) => {
          return     
        });
      },
      // 处理分页
      handleSizeChange(size) {
        this.pageConfig.pageSize = size
        this.getAuthenticationsById();
      },
      handleCurrentChange(page) {
        this.pageConfig.curPage = page
        this.getAuthenticationsById();
      },
      showFullImage(imgurl) {
        window.open(imgurl)
      },
      handleYearChange(item) {
        this.pageConfig.curPage = 1
        this.pageConfig.pageSize = 10
        this.getAuthenticationsById()
      },
      openEvaluateFile() {
        window.open(this.$furl)
      }
    }
  }
</script>

<style lang="less" scoped>
  .trend {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    margin-top: 10px;
  }
  .material-img {
    width: 60%;
    height: auto;
    img {
      width: 100%;
      height: auto;
    }
  }
  .detail-dialog div {
    line-height: 30px;
  }
</style>