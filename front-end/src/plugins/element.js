import Vue from 'vue'
import { Button, Form, FormItem, Input, Row, Col, Link,
Dialog, Tooltip, Message, Menu, MenuItem, Submenu, Avatar, Badge,
Carousel, CarouselItem, Table, TableColumn, Tag, Container, Option, Select,
PageHeader, Tabs, TabPane, Pagination, Card, Divider, Steps, Step,
Timeline, TimelineItem, Upload, Image, Slider, Popover, CheckboxGroup,
Checkbox, MessageBox, Radio, RadioGroup, RadioButton, Aside, Header, Main,
Footer, Switch, Popconfirm, InputNumber, Loading, Tree, Autocomplete,
Cascader, Notification} from 'element-ui'

Vue.use(Button)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Input)
Vue.use(Row)
Vue.use(Col)
Vue.use(Link)
Vue.use(Dialog)
Vue.use(Tooltip)
Vue.use(Menu)
Vue.use(MenuItem)
Vue.use(Submenu)
Vue.use(Avatar)
Vue.use(Badge)
Vue.use(Carousel)
Vue.use(CarouselItem)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(Tag)
Vue.use(Container)
Vue.use(Option)
Vue.use(Select)
Vue.use(PageHeader)
Vue.use(Tabs)
Vue.use(TabPane)
Vue.use(Pagination)
Vue.use(Card)
Vue.use(Divider)
Vue.use(Steps)
Vue.use(Step)
Vue.use(Timeline)
Vue.use(TimelineItem)
Vue.use(Upload)
Vue.use(Image)
Vue.use(Slider)
Vue.use(Popover)
Vue.use(CheckboxGroup)
Vue.use(Checkbox)
Vue.use(Radio)
Vue.use(RadioGroup)
Vue.use(RadioButton)
Vue.use(Aside)
Vue.use(Header)
Vue.use(Main)
Vue.use(Footer)
Vue.use(Switch)
Vue.use(Popconfirm)
Vue.use(InputNumber)
Vue.use(Loading)
Vue.use(Tree)
Vue.use(Autocomplete)
Vue.use(Cascader)
// 不要用下面这一行代码，因为会默认在页面刷新的时候会出现一个空的默认通知栏
// Vue.use(Notification)

// Vue.prototype.$msg = Message
// Vue.prototype.$confirm = MessageBox.confirm

// 重写MessageBox.confirm方法，使之不能通过点击遮罩层关闭弹窗
Vue.prototype.$confirm = (message, confirmButtonText, cancelButtonText) => {
  return new Promise((resolve, reject) => {
    MessageBox.confirm(message, '提示', {
      confirmButtonText: confirmButtonText,
      cancelButtonText: cancelButtonText,
      closeOnClickModal: false,
      type: 'warning'
    }).then(() => {
      resolve(true)
    }).catch(() => {
      reject(false)
    })
  })
}

Vue.prototype.$prompt = MessageBox.prompt

// 这一段代码不能少
Vue.prototype.$msg = function(msg){
  return Message({
    message:msg,
    duration:100
  })
}
// 分别对success等样式进行设置
Vue.prototype.$msg.success = function (msg) {
  return Message.success({
    message: msg,
    duration: 1500
  })
}

Vue.prototype.$msg.warning = function (msg) {
  return Message.warning({
    message: msg,
    duration: 1500
  })
}

Vue.prototype.$msg.error = function (msg) {
  return Message.error({
    message: msg,
    duration: 1500
  })
}

// Vue.prototype.$notify = Notification
Vue.prototype.$notifySimple = function (message, duration, iconClass) {
  return Notification.info({
    message: message,
    duration: duration,
    iconClass: iconClass
  })
}

Vue.prototype.$notify = Notification

Vue.prototype.$alert = MessageBox.alert