<template>
  <div class="container">
    <div class="container-blur"></div>
    <!-- 左边信息区 -->
    <div class="container-left">
      <div class="container-left-foot">
        <div>comprehensive quality evaluate system for college student</div>
        <div>copyright &copy; 2021 quarkape. all rights reserved.</div>
        <div>渝ICP备2021012014号</div>
      </div>
    </div>
    <!-- 右边表单区域 -->
    <div class="container-right">
      <el-form ref="loginFromRef" :rules="loginFormRules" class="container-right-form" :model="loginForm">
        <el-form-item prop="userid">
            <el-input prefix-icon="el-icon-user" maxlength="20" placeholder="账号" v-model="loginForm.userid"></el-input>
        </el-form-item>
        <el-form-item prop="password">
            <el-input prefix-icon="el-icon-lock" maxlength="50" show-password placeholder="密码" v-model="loginForm.password" @keyup.enter.native="login"></el-input>
        </el-form-item>
        <el-form-item style="margin-bottom: 0">
            <el-button type="primary" style="width: 100%" @click="login">登录</el-button>
        </el-form-item>
        <div class="container-right-form-btns">
          <div><el-checkbox v-model="isRememberPwd">记住密码</el-checkbox></div>
          <div><el-tooltip effect="dark" content="请联系学校管理员重置密码" placement="bottom-end"><el-link :underline="false">忘记密码</el-link></el-tooltip></div>
        </div>
        <div style="margin-top:10px;font-size:12px;color:#999999;">建议使用谷歌浏览器、火狐浏览器或者高版本的其他浏览器</div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loginForm: {
        userid: '',
        password: ''
      },
      checked: true,
      forgetPasswordDialogVisible: false,
      loginFormRules: {
        userid: [
          { required: true, message: "请输入账号", trigger: "blur" }
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" }
        ]
      },
      isRememberPwd: false
    }
  },
  mounted() {
    this.fillUserInfo();
  },
  methods: {
    fillUserInfo() {
      let savedUli = localStorage.getItem('ULI')
      if (!savedUli) return
      let uli = window.atob(savedUli)
      this.loginForm.userid = uli.split(';')[1].split('=')[1]
      this.loginForm.password = uli.split(';')[2].split('=')[1]
      this.isRememberPwd =  uli.split(';')[0].split('=')[1]=='0'?false:true
    },
    login() {
      this.$refs.loginFromRef.validate(async valid => {
        if (!valid) return
        let serviceId = window.btoa("userid:" + this.loginForm.userid + ";password:" + this.loginForm.password + ";timestamp:" + (new Date()).getTime())
        let {data: res} = await this.$http.post('checkLogin', this.$qs.stringify({serviceId: serviceId}))
        if (!res) return
        if (res.code != 0) {
          this.loginForm.password = ''
          window.localStorage.removeItem('ULI')
          this.$msg.error(res.message)
        }
        // set a token
        localStorage.setItem("token", res.data.token)
        localStorage.setItem("authToken", res.data.authToken)
        // set remember pwd
        if (this.isRememberPwd) {
          let rememberPwd = this.isRememberPwd?'isRemember=1':'isRemember=0'
          let userLoginInfo = rememberPwd+';userid='+this.loginForm.userid+';password='+this.loginForm.password+';timestamp='+(new Date).getTime()
          let uli = window.btoa(userLoginInfo)
          localStorage.setItem("ULI", uli)
        } else {
          window.localStorage.removeItem('ULI')
        }
        switch(res.data.role) {
          case "system": this.$router.push('/sys_home');break; // 管理员角色
          case "student": this.$router.push('/stu_home');break; // 学生角色
          case "manager": this.$router.push('/mag_addscore_check');break; // 教师角色
            default: break;
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
.container {
  width: 100%;
  height: 100%;
  background: url("../assets/images/ibg.jpg") fixed;
  background-repeat: no-repeat;
  background-size: cover;
  position: relative;
  display: flex;
  z-index: 0;
  .container-blur {
    position: absolute;
    width: 700px;
    height: 100%;
    top: 0;
    right: 0;
    filter: blur(4px);
    background: inherit;
    z-index: 0;
  }
}
.container-left {
  position: relative;
  flex: 1;
  color: #fff;
  .container-left-foot {
    border-top: 1px solid #fff;
    position: absolute;
    padding-top: 4px;
    left: 50px;
    bottom: 30px;
    font-size: 10px;
  }
}
.container-right {
  width: 700px;
  height: 100%;
  min-width: 700px;
  background-color: rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  .container-right-form {
    background-color: #fff;
    width: 400px;
    padding: 20px 20px;
    .container-right-form-btns {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      margin-top: 4px;
    }
  }
}
</style>