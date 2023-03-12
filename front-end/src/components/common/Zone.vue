<template>
  <div style="width:100%">
    <div style="text-align:right;"><el-button type="primary" size="medium" @click="logout"><i class="el-icon-scissors"></i>&nbsp;&nbsp;退出登录</el-button></div>
    
    <!-- 基本信息 -->
    <el-card style="width:100%;margin-top:20px" shadow="hover" v-if="showManagerInfo">
      <div slot="header">
        <span>基本信息</span>
      </div>
      <div class="info_main">
        <div><span><i class="el-icon-"></i>姓名：</span><span>{{basicInfo.name}}</span></div>
        <div><span><i class="el-icon-"></i>专业：</span><span>{{basicInfo.major_name}}</span></div>
        <div><span><i class="el-icon-"></i>班级：</span><span>{{basicInfo.class}}</span></div>
      </div>
    </el-card>

    <el-card style="width:100%;margin-top:20px" shadow="hover">
      <div slot="header">
        <span>系统信息</span>
      </div>
      <el-form ref="modifyPwdRef" label-width="100px" :model="sysInfo">
        <el-form-item label="学号/工号：">
          <el-input :disabled="true" v-model="sysInfo.userid"></el-input>
        </el-form-item>
        <el-form-item label="密码：">
          <el-input :disabled="isModifyPwdDisabled" type="password" clearable v-model="sysInfo.password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码：" v-if="!isModifyPwdDisabled">
          <el-input :disabled="isModifyPwdDisabled" type="password" clearable v-model="sysInfo.ensurepwd"></el-input>
        </el-form-item>
      </el-form>
      <div style="display:flex;flex-direction:row;justify-content:flex-end">
        <el-button style="margin-right:10px;color:#E6A23C" type="text" v-if="!isModifyPwdDisabled" @click="cancelModifyPwd">取消修改</el-button>
        <el-button type="text" @click="dealModifyPwd">{{isModifyPwdDisabled?'修改密码':'确认修改'}}</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      basicInfo: {},
      sysInfo: {},
      isEditDisabled: true,
      isModifyPwdDisabled: true,
      isShowBasicInfo: false,
      currentRole: '',
      showManagerInfo: false
    }
  },
  created() {
    this.getBasicInfo()
  },
  methods: {
    async logout() {
      let {data: res} = await this.$http.post("/logOut")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.messgae)
      // this.$msg.success("退出登陆成功")
      this.$router.push("/login")
    },
    async getBasicInfo() {
      let {data: res} = await this.$http.post("/getUserBasicInfo")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      // 这里也要注意深浅拷贝
      if (!res.data) return
      this.isShowBasicInfo = true
      this.basicInfo = res.data
      this.sysInfo = res.data
      if (res.data.role == null) { // 是学生
        this.showManagerInfo = true
      }
    },
    cancelModifyPwd() {
      this.isModifyPwdDisabled = true
    },
    async dealModifyPwd() {
      if (!this.isModifyPwdDisabled) {
        if (!this.sysInfo.password || this.sysInfo.password === '') return this.$msg.error("请填写密码")
        if (!this.sysInfo.ensurepwd || this.sysInfo.ensurepwd === '') return this.$msg.error("请填写确认密码")
        if (this.sysInfo.password != this.sysInfo.ensurepwd) return this.$msg.error("密码前后不一致")
        let {data: res} = await this.$http.post("/modifyPassword", this.$qs.stringify({password: this.sysInfo.password}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        window.localStorage.removeItem("ULI")
        this.$msg.error("登录信息更新,请重新登录")
        this.$router.push('/login')
      }
      this.isModifyPwdDisabled = !this.isModifyPwdDisabled
    }
  }
}
</script>

<style scoped>
.edu-info > div {
  line-height: 40px;
  font-size: 14px;
}
.edu-info > div > span:first-child {
  padding-right: 12px;
  display: inline-block;
  width: 88px;
  text-align: right;
}
.info_main {
  /* margin-top:20px; */
  font-size: 14px;
}
.info_main > div {
  padding: 6px 0;
}
</style>