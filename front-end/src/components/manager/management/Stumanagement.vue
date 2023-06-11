<template>
  <div style="width:100%;">
    <div class="trend">
      <el-upload action="javascript:void(0)" :auto-upload="false" :show-file-list="false" :on-change="chooseFile" accept=".xls,.xlsx" :multiple="false" :file-list="gradeFiles">
        <el-button size="medium" type="primary">上传学生名单</el-button>
      </el-upload>
      <el-button size="medium" type="warning" style="margin-left:20px" @click="showUploadFileIllustration()">下载学生名单模板</el-button>
    </div>
    <div style="width:100%;display:flex;flex-direction:row;align-items:flex-start;margin-top:10px">
      <el-card class="sider" shadow="hover">
        <div style="margin-bottom:14px;font-size:14px;">专业列表:</div>
        <div v-for="(item) in classes" :key="item.major_code" @click="changeMajor(item.major_code)"><el-button style="margin-bottom:10px" size="small" :type="curMajor==item.major_code?'primary':'normal'">{{item.major_name}}</el-button></div>
      </el-card>
      <div class="mainbody">
        <el-card shadow="hover">
          <div style="font-size:14px;color:#999999;text-align:right;margin-bottom:10px"><i class="el-icon-info"></i>学分管理页面仅显示已经审核过的内容</div>
          <div style="font-size:14px;color:#999999;text-align:right;margin-bottom:10px"><i class="el-icon-info"></i>添加专业请前往规则配置--配置专业列表</div>
          <el-table :data="studentLists" style="width: 100%" stripe v-loading="loading" border>
            <el-table-column prop="name" label="姓名" width="140"></el-table-column>
            <el-table-column prop="userid" label="学号" width="180"></el-table-column>
            <el-table-column prop="class" label="班级"></el-table-column>
            <el-table-column prop="role" label="角色" width="100">
              <template slot-scope="props">
                <el-tag size="mini" :type="props.row.role=='student'?'primary':'danger'">{{props.row.role=='student'?'学生':'助理'}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="props">
                <!-- 重置系统密码为随机密码、管理该生提交的所有材料 -->
                <el-button icon="el-icon-unlock" circle size="mini" plain title="重置为随机密码" @click="resetPassword(props.row.userid)"></el-button>
                <el-button icon="el-icon-data-analysis" type="primary" plain circle size="mini" title="管理该生学分" @click="openManageScoreDialog(props.row.userid)"></el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="text-align:center;margin-top:20px">
            <el-pagination :hide-on-single-page="true" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="curPage" :page-sizes="[10]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalNum"></el-pagination>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 管理学生学分页面 -->
    <scoremanage :dialogVisible.sync="manageScoreDialogVisible" v-if="manageScoreDialogVisible" @closeDialog="manageScoreDialogVisible=false" :curId.sync="curId"></scoremanage>
    <materialmanage :dialogVisible.sync="manageMaterialDialogVisible" v-if="manageMaterialDialogVisible" @closeDialog="manageMaterialDialogVisible=false" :curId.sync="curId"></materialmanage>
  </div>
</template>

<script>
  import {download} from '../../../plugins/download.js'
  import ScoreManagementApplication from '../component/ScoreManagementApplication.vue'
  export default {
    components: {'scoremanage': ScoreManagementApplication},
    data() {
      return {
        classes: [],
        defaultProps: {
          children: 'children',
          label: 'label'
        },
        studentLists: [],
        loading: false,        
        manageScoreDialogVisible: false,
        manageMaterialDialogVisible: false,
        curId: '',
        totalNum: 0,
        curPage: 1,
        pageSize: 10,
        curMajor: '040104',
        gradeFiles: []
      }
    },
    mounted() {
      this.getAllClasses()
      this.getAllStudentList('040104')
    },
    methods: {
      openManageScoreDialog(id) {
        this.manageScoreDialogVisible = true
        this.curId = id
      },
      openManageMaterialDialog(id) {
        this.manageMaterialDialogVisible = true
        this.curId = id
      },
      changeMajor(major) {
        this.curMajor = major
        this.getAllStudentList(major)
        this.curPage = 1
        this.pageSize = 10
      },
      async getAllStudentList(code) {
        this.loading = true
        let {data: res} = await this.$http.post("/getAllStudentList", this.$qs.stringify({major: code, curPage: this.curPage, pageSize: this.pageSize}))
        if (!res) {
          this.loading = false
          return
        }
        if (res.code != 0) {
          this.loading = false
          return this.$msg.error(res.message)
        }
        this.studentLists = res.data.item
        this.totalNum = res.data.total
        this.loading = false
      },
      resetPassword(id) {
        this.$confirm('重置后生成6位随机密码，确认重置密码?','确定','取消').then(async () => {
            let {data: res} = await this.$http.post("/resetPassword", this.$qs.stringify({id: id}))
            if (!res) return
            if (res.code != 0) return this.$msg.error(res.message)
            this.$notify({
              title: '提示',
              duration: 0,
              dangerouslyUseHTMLString: true,
              message: '密码重置成功，重置后的随机密码为: <strong><span style=\"color:#0081ff\;text-decoration:underline;">' + res.data + '</span></strong>(区分大小写)。请告知学生尽快修改密码。'
            });
          })
      },
      // 设置班级助理
      // setAssistant(id) {
      //   this.$confirm('确认设置/取消助手权限?','确定','取消').then(async () => {
      //     let {data: res} = await this.$http.post("/setAssistant", this.$qs.stringify({userid: id}))
      //     if (!res) return
      //     if (res.code != 0) return this.$msg.error(res.message)
      //     for (let i in this.studentLists) {
      //       if (id == this.studentLists[i].userid) {
      //         this.studentLists[i].role = this.studentLists[i].role=='student'?'assistant':'student'
      //         break
      //       }
      //     }
      //     this.$msg.success("修改评价助手权限成功!")
      //   })
      // },
      async getAllClasses() {
        let {data: res} = await this.$http.post("/getAllClasses")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.classes = res.data
      },
      // 更改分页页数和每页数量
      handleSizeChange(size) {
        this.pageSize = size
        this.getAllStudentList(this.curMajor)
      },
      // 更改页数
      handleCurrentChange(current) {
        this.curPage = current
        this.getAllStudentList(this.curMajor)
      },
      // 选择学生列表文件
      chooseFile(file, fileList) {
        this.$confirm('您已选择文件【' + file.name + '】，上传后不能撤销，是否确认？' ,'确认', '取消').then(async () => {
          let fd = new FormData()
          fd.append("file", file.raw)
          let {data: res} = await this.$http.post("/addStudents", fd)
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          this.$notifySimple("操作成功!", 3000, "el-icon-check")
        }).catch(()=> {
          this.fileList = []
        })
      },
      showUploadFileIllustration() {
        download(this.$lurl, '学生名单上传模板');
      }
    }
  }
</script>

<style scoped>
  .current_apartment {
    margin: 10px 0;
    font-size: 16px;
    color: #666;
  }
  .sider {
    min-width: 200px;
    /* padding: 20px 10px; */
    min-height: 100px;
    background-color: #fff;
    margin-right: 10px;
  }
  .mainbody {
    flex: 1;
    /* height: 800px; */
  }
  .trend {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
  }
</style>