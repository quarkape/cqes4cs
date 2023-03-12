import {Message} from 'element-ui'
import axios from 'axios'
import Vue from 'vue'
import router from '../../router'

// 进度条
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
NProgress.configure({ showSpinner: false })

axios.defaults.baseURL = "http://localhost:8003/"

// 配置全局的消息提示持续时间
let Messagee = function(msg) {
  return Message({
    message: msg,
    duration:1500
  })
}

Messagee.error = function (msg) {
  return Message.error({
    message: msg,
    duration: 1500
  })
}

axios.defaults.headers['Content-Type'] = 'application/x-www-form-urlencoded'
axios.interceptors.request.use(
  config => {
    NProgress.start();
    // 给每个请求都加上token请求头 || config.url === '/checkLogin' && (localStorage.getItem('token') != null)
    if (config.url !== 'checkLogin') {
      if (localStorage.getItem('token')) {
        config.headers.token = localStorage.getItem('token');
        config.headers.authToken = localStorage.getItem('authToken');
      } else {
        this.$router.push('/');
      }
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

//异步请求后，判断token是否过期
axios.interceptors.response.use(
  response => {
    NProgress.done();
    return response;
  },
  error => {
    NProgress.done();
    if (error && error.response) {
			switch (error.response.status) {
				case 400: Messagee.error('请求错误，请先退出登录后再试'); 
					break;
        case 401: Messagee.error('当前用户不存在');
          break;
				case 402:
          router.replace({
            path: '/login'
          })
          Message.error('会话过期，请重新登录')
          break;
				case 403: Messagee.error('密码错误');
					break;
				case 404: Messagee.error('请求出错');
					break;
				case 408: Messagee.error('请求超时');
					break;
				case 500: Messagee.error('服务器错误');
					break;
				case 501: Messagee.error('服务未实现');
					break;
				case 502: Messagee.error('网络错误');
					break;
				case 503: Messagee.error('服务不可用');
					break;
				case 504: Messagee.error('网络超时');
					break;
				case 505: Messagee.error('HTTP版本不受支持');
          break;
        case 601: Messagee.error('无权进行当前操作');
					break;
				case 602: 
          router.replace({
            path: '/login'
          })
          Message.error('会话过期，请重新登录')
					break;
				default: Messagee.error('出错，请联系管理员');
			}
		}else{
			error.message ='连接服务器失败!'
		}
    return error;
  }
)
Vue.prototype.$http = axios