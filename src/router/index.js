import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../components/Login.vue'

// 公共页面
import NotifyStu from '../components/common/Notify.vue'
import ZoneStu from '../components/common/Zone.vue'

import NotifyMag from '../components/common/Notify.vue'
import ZoneMag from '../components/common/Zone.vue'

// 学生端系统界面
import Stu_Home from '../components/student/Home.vue'
import Stu_Index from '../components/student/Index.vue'
import ScoreBasedEvaluation from '../components/student/evaluate/ScoreBasedEvaluation.vue'
import EvaluationResult from '../components/student/evaluate/EvaluationResult.vue'
import ScoreManagement from '../components/student/management/ScoreManagement.vue'

// 管理员（教师）端系统界面
import Mag_Home from '../components/manager/Home.vue'
import Mag_StuManagement from '../components/manager/management/Stumanagement.vue'
import Mag_ContestManagement from '../components/manager/management/ContestManagement.vue'
import Mag_AddScoreCheck from '../components/manager/check/ScoreCheck.vue'
import Mag_AgainstCheck from '../components/manager/check/AgainstCheck.vue'
import Mag_StudentEvaluation from '../components/manager/evaluate/EvaluateOnScore.vue'
import ManageConfiguration from '../components/manager/config/ManageConfiguration.vue'
import Analyse from '../components/manager/analyse/Analyse.vue'

// 系统管理员系统界面
import Sys_Home from '../components/system/Home.vue'
import Sys_Management from '../components/system/Management.vue'
import Sys_MeHome from '../components/system/me/Home.vue'

Vue.use(VueRouter)
const routes = [  
  { path: '/', redirect: '/login'},
  { path: '/login', component: Login, meta: {title: '登录'} },
  { path: '/stu_home',
    component: Stu_Home,
    redirect: '/stu_index',
    children: [
      { path: '/stu_index', component: Stu_Index, meta: {title: '主页'} },
      { path: '/eonscore', component: ScoreBasedEvaluation, meta: {title: '学分评价'} },
      { path: '/eshowcase', component: EvaluationResult, meta: {title: '评价公示'} },
      { path: '/sapplication', component: ScoreManagement, meta: {title: '学分管理'} },
      { path: '/zone_stu', component: ZoneStu, meta: {title: '我的'} },
      { path: '/notify_stu', component: NotifyStu, meta: {title: '通知'} }
    ]
  },
  { path: '/mag_home',
    component: Mag_Home,
    redirect: '/mag_addscore_check',
    children: [
      { path: '/mag_stu_management', component: Mag_StuManagement, meta: {title: '学生管理'} },
      { path: '/notify_mag', component: NotifyMag, meta: {title: '通知'} },
      { path: '/mag_contest_management', component: Mag_ContestManagement, meta: {title: '赛事管理'} },
      { path: '/mag_stu_evaluation', component: Mag_StudentEvaluation, meta: {title: '学生评价'} },
      { path: '/zone_mag', component: ZoneMag, meta: {title: '我的'} },
      { path: '/mag_addscore_check', component: Mag_AddScoreCheck, meta: {title: '加分审核'} },
      { path: '/mag_against_check', component: Mag_AgainstCheck, meta: {title: '异议处理'} },
      { path: '/modify_index_config', component: ManageConfiguration, meta: {title: '修改配置'} },
      { path: '/analyse', component: Analyse, meta: {title: '分析'} }
    ]
  },
  { path: '/sys_home',
    component: Sys_Home,
    redirect: '/sys_management',
    children: [
      { path: '/sys_management', component: Sys_Management, meta: {title: '账号管理'} },
      { path: '/sys_me', component: Sys_MeHome, meta: {title: '我的'} }
    ]
  }
]
const router = new VueRouter({ routes })

// 解决重复点击路由报错
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch((err) => err)
}

router.beforeEach((to, from, next) => {
  // 如果前往登录界面直接就放行
  if (to.path == '/login') return next();
  // 如果不是前往登录页面，先看有没有token
  var token = localStorage.getItem("token");
  // 如果token不存在则返回到登陆界面
  if (!token) return next('/login');
  // 如果token存在则放行
  next();
})

export default router