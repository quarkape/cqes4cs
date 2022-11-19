<template>
  <div style="width:100%">
    <el-button style="float:right" type="primary" size="medium" @click="logout"><i class="el-icon-scissors"></i>&nbsp;&nbsp;退出登录</el-button>
    
    <!-- 基本信息 -->
    <!-- <el-card style="width:100%;margin-top:50px" shadow="hover">
      <div slot="header">
        <span>基本信息</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="dealModifyInfo">{{isEditDisabled?'修改资料':'保存修改'}}</el-button>
      </div>
      <el-form ref="basicInfoRef" :rules="basicInfoRules" label-width="100px" :model="basicInfo">
        <el-form-item label="姓名：">
          <el-input :disabled="true" v-model="basicInfo.name"></el-input>
        </el-form-item>
        <el-form-item label="电话：" prop="tel">
          <el-input :disabled="isEditDisabled" v-model="basicInfo.tel"></el-input>
        </el-form-item>
        <el-form-item label="邮箱：" prop="email">
          <el-input :disabled="isEditDisabled" v-model="basicInfo.email"></el-input>
        </el-form-item>
      </el-form>
    </el-card> -->

    <!-- 修改密码 -->
    <!-- <el-card style="width:100%;margin-top:20px" shadow="hover">
      <div slot="header">
        <span>系统信息</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="dealModifyPwd">{{isModifyPwdDisabled?'修改密码':'确认修改'}}</el-button>
      </div>
      <el-form ref="modifyPwdRef" :rules="modifyPwdRules" label-width="100px" :model="sysInfo">
        <el-form-item label="用户名：">
          <el-input :disabled="true" v-model="sysInfo.username"></el-input>
        </el-form-item>
        <el-form-item label="密码：" prop="password">
          <el-input :disabled="isModifyPwdDisabled" type="password" v-model="sysInfo.password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码：" v-if="!isModifyPwdDisabled" prop="ensurepwd">
          <el-input :disabled="isModifyPwdDisabled" type="password" v-model="sysInfo.ensurepwd"></el-input>
        </el-form-item>
      </el-form>
    </el-card> -->
  </div>
</template>

<script>
export default {
  data() {
    return {
      fileList: [],
      basicInfo: {},
      sysInfo: {
        username: '222017321092008',
        password: '',
        ensurepwd: ''
      },
      isEditDisabled: true,
      newPwd: '',
      basicInfoRules: {
        tel: [{required: true, message: '请输入联系方式', trigger: 'blur'}],
        email: [{required: true, message: '请输入邮箱', trigger: 'blur'}]
      },
      isModifyPwdDisabled: true,
      modifyPwdRules: {
        password: [{required:true, message:'请输入新密码', trigger: 'blur'}],
        ensurepwd: [{required:true, message:'请确认新密码', trigger: 'blur'}]
      }
    }
  },
  created() {
    // this.getBasicInfo()
  },
  methods: {
    async logout() {
      let {data: res} = await this.$http.post("/logOut")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.messgae)
      this.$msg.success("退出登陆成功")
      this.$router.push("/login")
    },
    async getBasicInfo() {
      let {data: res} = await this.$http.post("/getBasicInfoTch")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      this.basicInfo = res.data
      this.sysInfo.username = this.basicInfo.userid
    },
    dealModifyInfo() {
      if (!this.isEditDisabled) {
        this.$refs.basicInfoRef.validate(async valid => {
          if (!valid) return
          let {data: res} = await this.$http.post("/modifyBasicInfo", this.$qs.stringify(this.basicInfo))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          this.getBasicInfo()
          this.$msg.success("资料修改成功")
        })
      }
      this.isEditDisabled = !this.isEditDisabled
      this.$refs.basicInfoRef.resetFields()
    },
    dealModifyPwd() {
      if (!this.isModifyPwdDisabled) {
        this.$refs.modifyPwdRef.validate(async valid => {
          if (!valid) return
          if (this.sysInfo.password != this.sysInfo.ensurepwd) return this.$msg.error("密码前后不一致")
          let {data: res} = await this.$http.post("/modifyPassword", this.$qs.stringify({password: this.sysInfo.password}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          window.localStorage.removeItem("ULI")
          this.$msg.error("登录信息更新,请重新登录")
          this.$router.push('/login')
        })
      }
      this.isModifyPwdDisabled = !this.isModifyPwdDisabled
      this.$refs.modifyPwdRef.resetFields()
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
</style>