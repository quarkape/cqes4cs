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
            <el-menu-item index="/mag_addscore_check">审核</el-menu-item>
            <el-menu-item index="/mag_against_check">异议处理</el-menu-item>
            <el-menu-item index="/mag_stu_evaluation">学生评价</el-menu-item>
            <el-menu-item index="/mag_contest_management">赛事管理</el-menu-item>
            <el-menu-item index="/mag_stu_management">学生管理</el-menu-item>
            <el-menu-item index="/modify_index_config">配置</el-menu-item>
            <el-menu-item index="/analyse">分析</el-menu-item>
          </el-menu>
        </div>
        <!-- 个人信息 -->
        <div class="nav-user">
          <!-- <el-tag type="primary" style="margin-right:20px"><i class="el-icon-user-solid"></i>&nbsp;&nbsp;{{userInfo.roleName}}</el-tag> -->
          <el-badge class="nav-user-message" :is-dot="unReadNotifyCount>0">
            <i @click="toNotify" class="el-icon-message-solid" style="font-size:30px;color:#909399"></i>
          </el-badge>
          <!-- <div @click="toMe"><el-avatar shape="square" size="medium" :src="userInfo.avatar"></el-avatar></div> -->
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
        avatar: '',
        roleName: '默认'
      },
      unReadNotifyCount: 0,
      pathNameMenu: {
        '/mag_contestcheck':'赛事审核',
        '/mag_contest_management':'赛事管理'
      }, 
      pathNameMenuA: {
        '/mag_stu_evaluation':'学生评价',
        '/mag_stu_management':'学生管理'
      },
      pathNameMenuB: {
        '/mag_config':'查看规则',
        '/modify_index_config':'修改规则'
      }
    }
  },
  methods: {
    // 登陆后获取未读通知数量并传值给父组件
    async getUnreadNotifyCount() {
      let {data: res} = await this.$http.post("/getUnreadNotifyCount")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      let curTitle = document.title
      document.title = '(' + res.data.unReadCount + '条通知未读) ' + curTitle
      this.unReadNotifyCount = res.data.unReadCount
    },
    toHome() {
      return
    },
    toMe() {
      this.$router.push('/zone_mag')
    },
    toNotify() {
      this.$router.push('/notify_mag')
    }
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
    height: 40px;
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
  width: 100%;
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