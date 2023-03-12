import Vue from 'vue'
import App from './App.vue'
import router from './router'
import './plugins/element.js'
import './assets/css/global.css'

import './assets/configs/AxiosConfig.js'

import './assets/configs/Urls.js'

// 字体图标
import './assets/fonts/iconfont.css'

// 导入序列化
import qs from 'qs'
Vue.prototype.$qs = qs
Vue.config.productionTip = false

/* 路由发生变化修改页面title */
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

// 过滤器，可以修改时间格式
Vue.filter('dateFormat', function(originValue) {
  let date = new Date(parseInt(originValue));
  let y = date.getFullYear();
  let m = date.getMonth()<9?('0'+(date.getMonth()+1)):(date.getMonth()+1)
  let d = date.getDate()<10?('0'+date.getDate()):date.getDate()
  return `${y}-${m}-${d}`
})

// 生成uuid，在修改赛事加分配置的时候用
Vue.prototype.$uuid = () => {
    let d = new Date().getTime();
    if (window.performance && typeof window.performance.now === "function") {
        d += performance.now(); //use high-precision timer if available
    }
    let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      var r = (d + Math.random() * 16) % 16 | 0;
      d = Math.floor(d / 16);
      return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return uuid;
}

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
