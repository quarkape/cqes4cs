<template>
  <div style="width:100%">
    <div class="trend">
      <el-upload action="javascript:void(0)" :auto-upload="false" :show-file-list="false" :on-change="chooseFile" accept=".xls,.xlsx" :multiple="false" :file-list="gradeFiles">
        <el-button size="small" type="primary">
          <span>上传学业成绩</span>
        </el-button>
      </el-upload>
      <el-button size="small" type="warning" style="margin-left:20px" @click="downloadExampleFile()">
          <span>下载学业成绩模板</span>
      </el-button>
      <el-button size="small" type="success" style="margin-left:20px" @click="openEvaluateFile()">
          <span>查看综评文件</span>
      </el-button>
      <el-button size="small" type="danger" style="margin-left:20px" @click="exportFile()">
        <span>导出本页内容</span>
      </el-button>
    </div>

    <el-card shadow="hover" style="margin-top:10px">
      <el-select v-model="wholeCurYear" placeholder="请选择学年" style="margin-bottom:10px" @change="changeWholeCurYear">
        <el-option v-for="(item,index) in years" :key="index" :label="item" :value="item">
        </el-option>
      </el-select>
      <span style="margin-left:20px;">
        <el-select v-model="curClass" placeholder="请选择专业" @change="changeCurClass">
          <el-option v-for="(item,index) in classes" :key="index" :label="item.major_name" :value="item.major_code"></el-option>
        </el-select>
      </span>
      <el-table :data="scoreRank" style="width: 100%" border class="table">
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
        <el-table-column prop="id" label="学号" width="150"></el-table-column>
        <el-table-column prop="score_main" label="学业成绩" width="90"></el-table-column>
        <el-table-column prop="score_other" label="其他学分" width="90"></el-table-column>
        <el-table-column prop="score_plus" label="手动加分/扣分" width="120">
          <template slot-scope="props">
            <span>{{props.row.score_plus}}/{{props.row.score_sub}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="score_final" label="综合成绩" width="100"></el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="props">
            <el-button size="mini" circle type="success" icon="el-icon-s-operation" @click="openModifyScoreDialog(props.row.id)" title="新增手动加分/扣分"></el-button>
            <el-button size="mini" circle type="primary" icon="el-icon-s-marketing" @click="openManageScoreDialog(props.row.id)" title="管理学分"></el-button>
            <el-button size="mini" circle type="normal" icon="el-icon-s-management" @click="getScoreManually(props.row.id)" title="管理加分扣分"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="font-size:14px;color:#666;margin-top:10px"><i class="el-icon-info"></i>&nbsp;&nbsp;学业成绩和其他学分都是加权之后的结果</div>
      <div style="font-size:14px;color:#666;margin-top:4px"><i class="el-icon-info"></i>&nbsp;&nbsp;以综合成绩排名；综合成绩相同时，以学业成绩排名。学业成绩相同时，以学号降序排序。</div>
      <div style="font-size:14px;color:#666;margin-top:4px"><i class="el-icon-info"></i>&nbsp;&nbsp;学生学业成绩导入后，才能生成综合成绩</div>
    </el-card>
    <el-dialog title="手动加分/扣分" :visible.sync="modifyScoreDialog" width="30%" @closed="handleModifyScoreClose" :close-on-click-modal="false">
      <el-form label-position="right">
        <el-form-item label="操作分数(分数将直接参与最终分数计算，没有加分比例)">
          <el-input type="number" placeholder="请输入分数" v-model="modifyScore"></el-input>
        </el-form-item>
        <el-form-item label="操作类型">
          <div>
            <el-radio v-model="curType" label="0" border size="small">扣分(-)</el-radio>
            <el-radio v-model="curType" label="1" border size="small">加分(+)</el-radio>
          </div>
        </el-form-item>
        <el-form-item label="操作学年">
          <el-select v-model="curYear" placeholder="请选择">
            <el-option v-for="(item,index) in years" :key="index" :label="item" :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="desc" placeholder="说明加分/扣分具体原因" maxlength="50"></el-input>
        </el-form-item>
        <el-button style="width:100%;margin-top:20px" type="primary" size="medium" @click="modifyScoreManually">加分/扣分</el-button>
      </el-form>
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
        <el-table-column width="180" label="编辑">
          <template slot-scope="props">
            <el-button size="mini" icon="el-icon-edit" type="primary" plain title="编辑" @click="editScoreManually(props.row)"></el-button>
            <el-button size="mini" icon="el-icon-delete" type="warning" plain title="删除该项" @click="deleteScoreManually(props.row.uid)"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-dialog append-to-body class="history-container" :close-on-click-modal="false" title="编辑" width="40%" :visible.async="editScoreManuallyDialogVisible" @close="editScoreManuallyDialogClosed">
        <el-form label-position="right">
          <el-form-item label="分数">
            <el-input type="number" placeholder="请输入分数" v-model="modifyData.score"></el-input>
          </el-form-item>
          <el-form-item label="操作类型">
              <el-radio v-model="modifyData.type" label="0" border size="small">扣分(-)</el-radio>
              <el-radio v-model="modifyData.type" label="1" border size="small">加分(+)</el-radio>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="modifyData.desc" placeholder="说明加分/扣分具体原因" maxlength="50"></el-input>
          </el-form-item>
          <el-button style="width:100%;margin-top:20px" type="primary" size="medium" @click="submitScoreManuallyModify">加分/扣分</el-button>
        </el-form>
      </el-dialog>
    </el-dialog>

    <!-- 管理学生学分页面 -->
    <scoremanage :dialogVisible.sync="manageScoreDialogVisible" v-if="manageScoreDialogVisible" @closeDialog="manageScoreDialogVisible=false" :curId.sync="curId"></scoremanage>

  </div>
</template>

<script>
  import {download} from '../../../plugins/download.js'
  import ScoreManagementApplication from '../component/ScoreManagementApplication.vue'
  export default {
    components: {'scoremanage': ScoreManagementApplication},
    data() {
      return {
        scoreRank: [],
        scholarshipDialog: false,
        fileList: [],
        selectedFile: null,
        // 可申请列表弹窗
        dialogVisible: false,
        manageScoreDialogVisible: false,
        manageMaterialDialogVisible: false,
        curId: '',
        modifyScoreDialog: false,
        modifyScore: 1,
        curType: "0",
        years: [],
        curYear: "",
        wholeCurYear: "",
        curClass: '',
        classes: [],
        desc: '',
        openScoreManuallyDialog: false,
        scoreManuallyVisible: false,
        scoreManuallyTable: [],
        modifyData: {},
        editScoreManuallyDialogVisible: false,
        gradeFiles: [],
        showIns: false,
        curClassName: ''
      }
    },
    mounted() {
      this.getCurYears(),
      // this.getAllStudentScore(),
      this.getAllClasses()
    },
    methods: {
      getCurYears() {
        let year = new Date().getFullYear()
        let month = new Date().getMonth()
        // 判断当前年份
        if (month<2) {
          year = year - 1
        }
        for (let i =0;i<4;i++) {
          this.years.push((year - i) + "-" + (year - i + 1))
        }
        this.wholeCurYear = this.years[0]
        this.curYear = this.years[0]
      },
      changeWholeCurYear(val) {
        this.wholeCurYear = val
        this.getAllStudentScore()
      },
      openManageScoreDialog(id) {
        this.manageScoreDialogVisible = true
        this.curId = id
      },
      openManageMaterialDialog(id) {
        this.manageMaterialDialogVisible = true
        this.curId = id
      },
      openModifyScoreDialog(id) {
        this.modifyScoreDialog = true
        this.curId = id
      },
      // 获取所有学生综评成绩
      async getAllStudentScore() {
        let {data: res} = await this.$http.post("/getAllStudentScore", this.$qs.stringify({year: this.wholeCurYear,major: this.curClass}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.scoreRank = res.data
      },
      // 打开综评文件
      openEvaluateFile() {
        window.open(this.$furl)
      },
      // 更新上传文件
      handleChange(file, fileList) {
        let allowedTypes = ['application/pdf']
        if (allowedTypes.indexOf(file.raw.type.toLowerCase()) == -1) {
          this.fileList = []
          return this.$msg.error("文件格式不正确")
        }
        if (file.size/1024/1024 > 5) {
          this.fileList = []
          return this.$msg.error("图片大小超出限制(5M)")
        }
        this.selectedFile = file.raw
        let fileName = file.raw.name
        this.$confirm('你已选择pdf文件：'+fileName+'。确认上传后将替换原文件，此操作不可恢复，确认更新上传？', '确定', '取消').then(async () => {
            let formData = new FormData()
            formData.append('multipartFile', this.selectedFile)
            let {data:res} = await this.$http.post('/changeEvaluateFile', formData)
            if (!res) return
            if (res.code != 0) {
              this.fileList = []
              this.selectedFile = null
              return this.$msg.error(res.message)
            }
            return this.$msg.success("文件更新成功!")
          }).catch(()=>{
            this.fileList = []
            this.selectedFile = null
          })
      },
      openScholarshipDialog() {
        this.dialogVisible = true
      },      
      infoApplication() {
        this.$confirm('将向所有符合条件的学生发送申请通知，是否继续?','确定','取消')
        .then(() => {
          console.log("主动发送通知")
        })
      },
      async modifyScoreManually(id, type) {
        let params ={}
        params.id = this.curId
        params.year = this.curYear
        params.type = this.curType
        params.score = this.modifyScore
        params.desc = this.desc
        let {data: res} = await this.$http.post("/modifyScoreManually", this.$qs.stringify(params))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.$msg.success("操作成功!请刷新查看更新后的分数!")
        this.modifyScoreDialog = false
      },
      handleModifyScoreClose() {
        //关闭加分弹窗
        this.curId = ''
        this.modifyScore = 1
        this.curType = "0"
        this.curYear = this.years[0]
        this.wholeCurYear = this.years[0]
        this.desc = ''
      },
      // 获取当前的所有class
      async getAllClasses() {
        let {data: res} = await this.$http.post("/getAllClasses")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.classes = res.data
        this.curClass = this.classes[0].major_code
        this.getAllStudentScore()
      },
      async changeCurClass(item) {
        for (let i in this.classes) {
          if (item == this.classes[i].major_code) {
            this.curClassName = this.classes[i].major_name
          }
        }
        this.scoreRank = []
        this.getAllStudentScore()
      },
      // 获取某学生的手动加分项 
      async getScoreManually(userid) {
        let {data: res} = await this.$http.post("/getScoreManually", this.$qs.stringify({userid: userid,year:this.wholeCurYear}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.scoreManuallyTable = res.data
        this.scoreManuallyVisible = true
      },
      // 弹出编辑弹窗
      editScoreManually(data) {
        this.modifyData = JSON.parse(JSON.stringify(data))
        this.modifyData.type = this.modifyData.type.toString()
        this.editScoreManuallyDialogVisible = true
      },
      // 提交手动扣分修改
      async submitScoreManuallyModify() {
        let {data: res} = await this.$http.post("modifyScoreManuallyInfo", this.$qs.stringify(this.modifyData))
        if(!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        for (let i in this.scoreManuallyTable) {
          if (this.scoreManuallyTable[i].uid == this.modifyData.uid) {
            this.scoreManuallyTable[i] = this.modifyData
            this.scoreManuallyTable[i].type = parseInt(this.scoreManuallyTable[i].type)
            break
          }
        }
        console.log(this.scoreManuallyTable)
        this.editScoreManuallyDialogVisible = false
        this.$msg.success("编辑成功!")
      },
      // 关闭弹窗
      scoreManuallyDialogClosed() {
        this.scoreManuallyTable = []
      },
      // 关闭修改加分项弹窗
      editScoreManuallyDialogClosed() {
        this.modifyData = {}
        this.editScoreManuallyDialogVisible = false
      },
      // 删除某项加分项 
      async deleteScoreManually(uid) {
        let {data: res} = await this.$http.post("/deleteScoreManually", this.$qs.stringify({uid:uid}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        for (let i in this.scoreManuallyTable) {
          if (this.scoreManuallyTable[i].uid = uid) {
            this.scoreManuallyTable.splice(i, 1)
            break
          }
        }
        this.$msg.success("删除成功!")
      },
      // 选择成绩文件
      chooseFile(file, fileList) {
        this.$confirm('您已选择文件【' + file.name + '】，上传后不能撤销，是否确认？' ,'确认', '取消').then(async () => {
          this.$notify({
            title: '提示',
            message: '正在处理中...',
            showClose: false,
            duration: 0,
            iconClass: 'el-icon-loading'
          });
          let fd = new FormData()
          fd.append("file", file.raw)
          let {data: res} = await this.$http.post("/readXLSFiles", fd)
          if (!res) {
            this.$notify.closeAll()
            return
          }
          if (res.code != 0) {
            this.$notify.closeAll()
            return this.$msg.error(res.message)
          }
          this.$notify.closeAll()
          this.$notifySimple("操作成功", 3000, "el-icon-check")
        }).catch(()=> {
          this.fileList = []
        })
      },
      // 导出excel，下载文件
      async exportFile() {
        if (this.scoreRank.length == 0) {
          return this.$msg.warning("当前页没有数据");
        }
        let {data: res} = await this.$http.post("getEvaluateResult", this.$qs.stringify({year: this.wholeCurYear,major: this.curClass}))
        if (!res) return 
        if (res.code != 0) return this.$msg.error(res.message)
        let fileName = res.data
        download(this.$eurl + fileName,`${this.wholeCurYear}学年_${this.curClassName}专业综合素质评价结果.xls`)
      },
      downloadExampleFile() {
        download(this.$gurl, '学业成绩上传模板');
      }
    }
  }
</script>

<style lang="less" scoped>
  .trend {
    height: 40px;
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    align-items: center;
  }
</style>