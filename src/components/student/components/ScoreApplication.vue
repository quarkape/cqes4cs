<template>
  <div>
    <!-- 申请加分 -->
    <el-dialog :close-on-click-modal="false" title="申请加分" :visible.sync="scoreDialogVisible" width="50%" @closed="applicationDialogClosed(['123'])">
      <el-steps :space="200" :active="applicationStep" finish-status="success" simple>
        <el-step icon="el-icon-edit" title="选择类型"></el-step>
        <el-step icon="el-icon-edit" title="选择赛事"></el-step>
        <el-step icon="el-icon-trophy" title="选择等级"></el-step>
        <el-step icon="el-icon-picture-outline" title="上传凭证"></el-step>
      </el-steps>
      <!-- 加分的形式 -->
      <div style="display:flex;justify-content:center" v-if="applicationStep==0">
        <div class="step-container">
          <div>赛事是指常规的比赛等，其他是指社会任职等</div>
          <div>
            <el-radio border v-model="contestForm" label="contest" size="medium" style="margin-bottom:10px">赛事</el-radio>
            <el-radio border v-model="contestForm" label="normal" size="medium">其他</el-radio>
          </div>
          <el-button type="primary" style="width:100%;align-self:center" @click="toNext">下一步</el-button>
        </div>
      </div>
      <!-- 赛事名称 -->
      <div style="display:flex;justify-content:center" v-if="applicationStep==1">
        <div class="step-container">
          <div v-if="contestForm=='contest'">
            <div>输入赛事名称，系统自动搜索，从搜索结果中选择后会自动进入下一步</div>
            <el-autocomplete style="margin-top:16px;width:100%" v-if="contestForm!='normal'" v-model="contestName" :fetch-suggestions="querySearchAsync" placeholder="输入赛事关键字以搜索" @select="handleSelect"></el-autocomplete>
          </div>
          <div v-else>
            <div>选择对应的加分类别，选择后会自动进入下一步</div>
            <el-select v-model="stepForm.name" value-key="uuid"  @change="handleTypeChange" placeholder="请选择" style="margin-top:16px">
              <el-option v-for="item in rules" :key="item.uuid" :label="item.name" :value="item"></el-option>
            </el-select>
          </div>
          
        </div>
      </div>
      <!-- 获奖等级 -->
      <div v-if="applicationStep==2" style="display:flex;justify-content:center">
        <div class="step-container">
          <div style="margin-top:16px">
            <span>选择等级：</span>
            <el-cascader v-model="selectedLevel" :options="curLevelConfig" :props="contestClassProps" @change="handleChange"></el-cascader>
          </div>
          <div><el-button type="primary" style="width:100%;align-self:center" @click="toNext">下一步</el-button></div>
        </div>
      </div>
      <!-- 证明图片 -->
      <div v-if="applicationStep==3" style="display:flex;justify-content:center">
        <div class="step-container">
          <div><i class="el-icon-caret-right"></i>&nbsp;请上传获奖证书照片或者电子版或者截图，每次只能选择一张图片</div>
          <el-upload :on-change="choosePic" :file-list="stepForm.image" :auto-upload="false" action="javascript:void(0)" :limit="1" :on-remove="handleRemove" list-type="picture">
            <el-button size="small" type="primary">上传证明材料电子档</el-button>
          </el-upload>
          <div><el-button type="primary" style="width:100%;align-self:center" @click="submitAddScore">提交申请</el-button></div>
          <el-button type="warning" style="width:100%;align-self:center" @click="applicationStep-=1">上一步</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    props: ['dialogVisible'],
    data() {
      return {
        scoreDialogVisible: this.dialogVisible,
        // 赛事申请步骤表
        stepForm: {
          name: '',
          image: [],
          levelstr: '',
          ruleid: '',
          rankid: ''
        },
        applicationStep: 0,
        // 选中赛事后确认不会被修改
        selectedContest: {
          id: '',
          name: '',
          level: ''
        },
        selectedLevel: '',
        selectedImageA: "",
        // 对赛事等级级联选择器的配置
        contestClassProps: {
          expandTrigger: 'hover',
          value: 'uuid',
          label: 'name',
          children: 'children'
        },
        curLevelConfig: [],
        contestForm: 'contest',
        rules: [],
        contestName: '',
        newItem: []
      }
    },
    watch:{
      dialogVisible(val){
        this.scoreDialogVisible = val
      }
    },
    methods: {
      // 本来这个回调函数是监听图片多种状态的但是因为是自定义上传，所以这里只用来监听选择文件
      choosePic(file, fileList) {
        let allowedTypes = ['image/jpeg', 'image/jpg', 'image/png']
        if (allowedTypes.indexOf(file.raw.type.toLowerCase()) == -1) {
          this.stepForm.image = []
          return this.$msg.error("文件格式不正确")
        }
        if (file.size/1024/1024 > 3) {
          this.stepForm.image = []
          return this.$msg.error("图片大小超出限制")
        }
        this.selectedImageA = file.raw
      },
      // 申请学分界面点击 下一步
      async toNext() {
        let step = this.applicationStep
        switch (step) {
          case 0:
            if (this.contestForm == 'normal') this.getRules()
            break;
          case 1:
            break;
          case 2:
            if (this.stepForm.rankid == '') return this.$msg.error("请选择获奖等级")
            break;
          default: break;
        }
        this.applicationStep += 1
      },
      // 删除已经选中的图片
      handleRemove(file, fileList) {
        this.selectedImageA = ""
      },
      // 关闭弹窗
      applicationDialogClosed() {
        console.log('enter')
        this.stepForm.name = ''
        this.stepForm.image = []
        this.stepForm.levelstr = ''
        this.stepForm.ruleid = ''
        this.stepForm.rankid = ''
        this.selectedImageA = ""
        this.applicationStep = 0
        this.selectedContest = {}
        this.selectedLevel = ''
        this.contestName = ''
        this.$emit('addNewItem', this.newItem, val => {
          if (val === 'done') {
            this.newItem = [];
          }
        })
        
      },
      // 提交申请
      async submitAddScore() {
        if (this.selectedImageA == "") return this.$msg.error("请选择证明材料图片")
        let formData = new FormData()
        formData.append('name', this.stepForm.name)
        formData.append('image', this.selectedImageA)
        formData.append('ruleid', this.stepForm.ruleid)
        formData.append('levelstr', this.stepForm.levelstr)
        let {data:res} = await this.$http.post('/createScore', formData)
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        // 获得学分申请id，直接添加至列表，用户不必自行刷新
        this.$msg.success("提交成功，请等待审核")
        this.applicationVisible = false
        this.newItem[0] = res.data
        this.newItem[1] = this.stepForm.name
        this.applicationDialogClosed()
      },
      // 根据输入的比赛关键字进行搜索呈现
      async querySearchAsync(queryString, cb) {
        if (!queryString) return cb([])
        let {data: res} = await this.$http.post("/getContestSearchResultByWords", this.$qs.stringify({words: queryString}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        if (res.data.length == 0) return cb([])
        let results = res.data
        cb(results)
      },
      async handleTypeChange(item) {
        this.stepForm.name = item.name
        let {data: res} = await this.$http.post('/getContestConfigByUuid', this.$qs.stringify({uuid: item.uuid}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.stepForm.ruleid = item.uuid
        this.curLevelConfig = JSON.parse('[' + res.data + ']')
        this.toNext()
      },
      handleChange(value) {
        let level = {}
        for (let i in value) {
          level[i] = value[i]
        }
        this.stepForm.levelstr = JSON.stringify(level)
        this.stepForm.rankid = value[value.length-1]
      },
      // 选择某个比赛后搜索比赛的相关配置
      async handleSelect(item) {
        let {data: res} = await this.$http.post('/getContestConfigById', this.$qs.stringify({id: item.id}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.curLevelConfig = JSON.parse('[' + res.data.classconfig + ']')
        this.stepForm.ruleid = res.data.uuid
        this.stepForm.name = res.data.name
        this.toNext()
      },
      async getRules() {
        let {data: res} = await this.$http.post("/getRule")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.rules = res.data
      }
    }
  }
</script>

<style scoped>
  .step-container {
    width: 80%;
    display: flex;
    flex-direction: column;
    font-size: 16px;
  }
  .step-container > div {
      padding: 8px 0;
  }
  .upload-container {
    width: 80%;
    display: flex;
    flex-direction: column;
    font-size: 16px;
  }
</style>