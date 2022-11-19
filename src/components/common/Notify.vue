<template>
  <div style="width:100%">
    <div style="margin-bottom:10px;text-align:right" v-show="showDeleteBtn">
      <el-link @click="allRead" type="primary" style="margin-right:20px">全部标记已读</el-link>
      <el-link @click="allDelete" type="primary">删除全部通知</el-link>
    </div>
    <div class="data-null" v-show="!showDeleteBtn">
      <div><img src="../../assets/images/data-null.png"></div>
      <div>暂无通知</div>
    </div>
    <el-card shadow="hover" class="notifys" :style="item.state==0?'border-left: 10px solid #50bfff':''" v-for="item in notifys" :key="item.id">
      <span class="title">{{item.title}}</span>
      <div class="date"><i class="el-icon-date"></i>&nbsp;&nbsp;{{item.id.substring(0,13) | dateFormat}}</div>
      <div class="mark-read">
        <span class="state" v-if="item.state==0" @click="readNotify(item.id)">标记已读</span>
        <span class="delete" @click="deleteNotify(item.id)">删除此条</span>
      </div>
      <div class="detail">{{item.detail}}</div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return{
      notifys: [],
      showDeleteBtn: false
    }
  },
  mounted() {
    this.getAllNotifys()
  },
  methods: {
    async getAllNotifys() {
      let {data: res} = await this.$http.post("/getAllNotifys")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      if (res.data.length != 0) this.showDeleteBtn = true
      this.notifys = res.data
    },
    async readNotify(id) {
      for (let i in this.notifys) {
        if (this.notifys[i].id == id) {
          if (this.notifys[i].state == 1) return
        }
      }
      let {data: res} = await this.$http.post("/readNotify", this.$qs.stringify({id: id}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      for (let i in this.notifys) {
        if (this.notifys[i].id == id) {
          this.notifys[i].state = 1
        }
      }
    },
    async allRead() {
      // 如果所有的通知都是已读状态，则不需要再发送请求
      let curNum = 0
      for (let i in this.notifys) {
        if (this.notifys[i].state == 0) {
          // 只要存在一个未读的消息，就跳出循环
          break
        }
        curNum += 1
      }
      if (curNum == this.notifys.length) return
      let {data: res} = await this.$http.post("/allRead")
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      for (let i in this.notifys) {
        this.notifys[i].state = 1
      }
    },
    allDelete() {
      this.$confirm('确认删除所有通知?', '确认', '取消').then(async () => {
        let {data: res} = await this.$http.post("/deleteAllNotify")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.notifys = []
        this.showDeleteBtn = false
        return this.$msg.success("全部删除成功")
      }).catch(() => {
        return;
      })
      
    },
    async deleteNotify(id) {
      let {data: res} = await this.$http.post("/deleteOneNotify", this.$qs.stringify({id: id}))
      if (!res) return
      if (res.code != 0) return this.$msg.error(res.message)
      for (let i in this.notifys) {
        if (this.notifys[i].id == id) {
          this.notifys.splice(i, 1)
        }
      }
      return this.$msg.success("删除成功")
    }
  }
}
</script>

<style scoped>
.notifys {
  margin: 8px 0;
  position: relative;
}
.title {
  font-weight: bolder;
}
.detail {
  color: #909399;
  margin-top: 6px;
  line-height: 20px;
  font-size: 14px;
}
.state {
  padding-right: 10px;
  font-size: 12px;
  cursor: pointer;
  color: #909399;
  background-color: rgb(240, 240, 240);
  border-radius: 4px;
  padding: 4px;
}
.date {
  font-size: 14px;
  color: #909399;
  margin: 6px 0;
}
.mark-read {
  position: absolute;
  right: 10px;
  top: 10px;
  display: none;
}
.delete {
  margin-left: 10px;
  padding-right: 10px;
  font-size: 12px;
  cursor: pointer;
  color: #909399;
  background-color: rgb(240, 240, 240);
  border-radius: 4px;
  padding: 4px;
}
.notifys:hover .mark-read {
  display: block;
}
.data-null {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.data-null img {
  width: 100px;
  height: 100px;
}
.data-null div:last-child {
  margin-top: 4px;
  color: #999;
  font-size: 14px;
}
</style>