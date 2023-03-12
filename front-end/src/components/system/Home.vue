<template>
  <div>
    <!-- 导航栏 -->
    <div class="nav-container">
      <div class="nav">
        <!-- logo -->
        <div class="nav-logo" @click="toHome"><img src="../../assets/images/banner.png"></div>
        <!-- 主菜单 -->
        <div style="flex:1;margin-left:20px">
          <el-menu mode="horizontal" router :default-active="$route.path">
            <el-menu-item index="/sys_management" >账号管理</el-menu-item>
          </el-menu>
        </div>
        <!-- 个人信息 -->
        <div class="nav-user">
          <!-- <el-tag type="primary" style="margin-right:20px"><i class="el-icon-user-solid"></i>&nbsp;&nbsp;系统管理员</el-tag> -->
          <div @click="toMe" style="width:30px;height:30px;display:flex;align-items:center;justify-content:center;background-color:#f6f6f6;border-radius:4px"><img style="width:30px;height:30px" src="../../assets/images/avatar.jpg"></div>
        </div>
      </div>
    </div>
    
    <div class="main-container">
      <div class="main"><router-view></router-view></div>
    </div>

    <div class="foot-container"><div class="foot">CQES4CS @夸克逃逸 2020 © All Right Reserved</div></div>
    
  </div>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {
        role: ''
      },
    }
  },
  created() {
    // this.getUserInfoHome()
  },
  methods: {
    toHome() {
      this.$router.push('/sys_home')
    },
    toMe() {
      this.$router.push('/sys_me')
    },
    // 登陆后进入主页时获取用户的角色和头
    // async getUserInfoHome() {
    //   let {data: res} = await this.$http.post("/getUserInfoHome")
    //   if (!res) return
    //   if (res.code != 0) return this.$msg.error(res.message)
    // }
  }
}
</script>

<style lang="less" scoped>
.nav-container {
  width: 100%;
  height: 60px;
  display: flex;
  justify-content: center;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 999;
  -webkit-box-shadow: 0 1px 3px rgba(18,18,18,.1);
  box-shadow: 0 1px 3px rgba(18,18,18,.1);
  background: #fff;
}
.nav {
  width: auto;
  min-width: 1000px;
  max-width: 1156px;
  display: flex;
  justify-content: space-between;
  min-width: 1000px;
}
.nav-logo {
  height: 60px;
  overflow: hidden;
  display: flex;
  align-items: center;
  img {
    height: 50px;
    width: auto;
  }
}
.nav-user {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  .nav-user-message {
    margin-right: 30px;
  }
}
.main {
  width: 100%;
  margin-top: 70px;
  .main-row {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: center;
    min-width: 700px;
  }
}
.foot-container {
  width: 100%;
  height: 50px;
  background-color: inherit;
  display: flex;
  justify-content: center;
  margin-top: 50px;
}
.foot {
  width: 1000px;
  height: 50px;
  background-color: #000;
  color: #fff;
  text-align: center;
  line-height: 50px;
  font-size: 12px;
}
/* 主体区域 */
.main-container {
  width: 100%;
  display: flex;
  justify-content: center;
  background-color: #f6f6f6;
}
.main {
  width: 1000px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
</style>