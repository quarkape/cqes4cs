<template>
  <div>
    <!-- 导航栏 -->
    <div class="nav-container">
      <div class="nav">
        <!-- logo -->
        <div class="nav-logo"><img src="../../assets/images/banner.png"></div>
        <!-- 主菜单 -->
        <div style="flex:1;margin-left:20px">
          <el-menu mode="horizontal" router :default-active="$route.path">
            <el-menu-item index="/stu_index" >首页</el-menu-item>
            <el-menu-item index="/eonscore">学分评价</el-menu-item>
            <el-menu-item index="/eshowcase">评价公示</el-menu-item>
            <el-menu-item index="/sapplication">学分管理</el-menu-item>
            <el-menu-item index="/myresume">我的简历</el-menu-item>
          </el-menu>
        </div>
        <!-- 个人信息 -->
        <div class="nav-user">
          <el-badge class="nav-user-message" :is-dot="unReadNotifyCount>0">
            <i @click="toNotify" class="el-icon-message-solid" style="font-size:30px;color:#909399"></i>
          </el-badge>
          <div @click="toMe" style="width:30px;height:30px;display:flex;align-items:center;justify-content:center;background-color:#f6f6f6;border-radius:4px"><img style="width:30px;height:30px" src="../../assets/images/avatar.jpg"></div>
        </div>
      </div>
    </div>
    
    <div class="main-container">
      <div class="main"><router-view @getUnreadNotifyCount="getUnreadNotifyCount"></router-view></div>
    </div>

    <div class="foot-container">
      <div class="foot foot1"><span>CQES4CS</span><span>@quarkape</span><span>2020-2021</span>© all rights reserved</div>
      <div class="foot foot2">try to contact by huewhom@gmail.com</div>
    </div>
    
  </div>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {
        role: '',
        roleName: '学生'
      },
      unReadNotifyCount: 0,
    }
  },
  created() {
    // this.getUserInfoHome()
  },
  methods: {
    getUnreadNotifyCount(val) {
      this.unReadNotifyCount = val
    },
    toMe() {
      this.$router.push('/zone_stu')
    },
    toNotify() {
      this.$router.push('/notify_stu')
    },
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
    margin-right: 40px;
  }
}
.main-container {
  min-height: 980px;
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
  flex-direction: column;
  align-items: center;
  margin-top: 50px;
  z-index: 999;
  -webkit-box-shadow: 0 1px 3px rgba(18,18,18,.1);
  box-shadow: 0 -1px 3px rgba(18,18,18,.1);
}
.foot {
  width: 1000px;
  color: #555666;
  text-align: center;
  font-size: 12px;
  background-color: #fff;
}
.foot1 {
  padding-top: 20px;
}
.foot2 {
  padding-bottom: 20px;
}
.foot span {
  padding-right: 8px;
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