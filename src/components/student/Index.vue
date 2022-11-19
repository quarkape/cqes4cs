<template>
  <div style="width:100%">
    <el-card shadow="hover">
      <div class="title">未读通知</div>
      <div>{{unReadNotifyCount}}</div>
    </el-card>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        unReadNotifyCount: 0
      }
    },
    mounted() {
      this.getUnreadNotifyCount()
    },
    methods: {
      // 登陆后获取未读通知数量并传值给父组件
      async getUnreadNotifyCount() {
        let {data: res} = await this.$http.post("/getUnreadNotifyCount")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.unReadNotifyCount = res.data.unReadCount
        this.$emit("getUnreadNotifyCount", res.data.unReadCount)
      },
    }
  }
</script>

<style lang="less" scoped>
.title {
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
  display: inline-block;
  font-weight: bold;
  &::before {
    content: "";
    position: absolute;
    width: 120%;
    height: 16px;
    background-color: #ffd500;
    left: 50%;
    bottom: -8px;
    transform: translateX(-50%);
    z-index: -1;
  }
}
</style>