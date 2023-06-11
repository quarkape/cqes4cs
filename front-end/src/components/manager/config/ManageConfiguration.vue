<template>
  <div style="width:100%;margin-top:10px">
    <div class="config_desc">
      <el-button type="danger" size="medium" @click="showIllustration=true">配置须知</el-button>
      <el-button type="primary" size="medium" @click="showExportDialog=true">导出配置</el-button>
    </div>
    <el-divider content-position="left">修改学业成绩比例</el-divider>
    <el-card shadow="hover">
      <div class="gradem">
        <el-input style="width:240px" v-model="gradePercent" placeholder="输入学业成绩占比" type="number"></el-input>
        <el-button type="primary" icon="el-icon-s-promotion" size="small" @click="submitGrade">提交修改</el-button>
      </div>
    </el-card>

    <el-dialog title="提示" :visible.sync="showIllustration" width="50%">
      <div style="font-size:14px;">
        <p>1.学业成绩比例设置为0时不会将学业成绩计算在内</p>
        <p>2.请先配置评价指标再在加分规则中绑定</p>
        <p>3.更改评价指标名称不会影响加分规则的绑定</p>
        <p>4.删除评价指标时，如果加分规则绑定有该指标，需要重新修改指标，否则无法识别</p> 
        <p>5.请在学生提交学分申请之前确定配置好所有规则，可以在后续增加规则，但是不要尝试修改规则和删除规则</p>
        <p>6.因为学分申请依赖于配置好的规则，删除规则会导致已有的学分申请数据出错</p>
      </div>
    </el-dialog>

    <el-divider content-position="left">评价指标配置</el-divider>
    <el-card shadow="hover">
      <div class="index-item" v-for="item in showIndexs" :key="item.uuid">
        <div>{{item.name}}</div>
        <div class="item-opts">
          <el-button style="color:#E6A23C" type="text" size="mini" @click="() => modifyIndex(item.uuid)">改名</el-button>
          <el-button style="color:#F56C6C" type="text" size="mini" @click="() => deleteIndex(item.uuid)">删除</el-button>
        </div>
      </div>
      <div class="add-one" @click="addIndex"><el-button type="primary" plain size="small"><i class="el-icon-plus"></i>增加一项</el-button></div>
    </el-card>

    <el-divider content-position="left">配置加分规则</el-divider>
    <el-card shadow="hover">
      <div style="text-align:right;margin-bottom:20px">
        <el-button type="warning" icon="el-icon-s-promotion" size="small">检查配置(正在建设)</el-button>
        <el-button type="danger" icon="el-icon-s-promotion" size="small" @click="submit">提交修改</el-button>
      </div>
       <el-tree :data="table" :props="defaultProps" node-key="uuid" :expand-on-click-node="false">
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span>{{data.name}}{{data.tag=='root'?(' - ' + data.score):''}}<el-tag v-if="data.tag=='top' && indexj[data.indexid] != undefined" size="mini" type="info" style="margin-left: 24px">{{indexj[data.indexid]}}</el-tag></span>
          <span class="option">
            <el-button v-if="data.tag!='root'" style="color:#67C23A" type="text" size="mini" @click="() => append(data)">新增</el-button>
            <el-button style="color:#909399" type="text" size="mini" @click="() => rename(data)">改名</el-button>
            <el-button style="color:#F56C6C" type="text" size="mini" @click="() => setroot(node, data)">{{data.tag=='root'?'取消':''}}设为叶子节点</el-button>
            <el-button v-if="data.tag=='top'" style="color:#409EFF" type="text" size="mini" @click="() => mofidyrule(data.uuid, data.indexid)">修改指标</el-button>
            <el-button v-if="data.tag=='root'" style="color:#67C23A" type="text" size="mini" @click="() => rescore(data)">改分</el-button>
            <el-button style="color:#E6A23C" type="text" size="mini" @click="() => remove(node, data)">删除</el-button>
          </span>
        </span>
      </el-tree>
      <div class="add-one" @click="addRule"><el-button type="primary" plain size="small"><i class="el-icon-plus"></i>增加一项</el-button></div>
    </el-card>

    <!-- 配置专业信息 -->
    <el-divider content-position="left">配置专业列表</el-divider>
    <el-card shadow="hover">
      <div class="index-item" v-for="item in majors" :key="item.uuid">
        <div>{{item.major_name}}(专业代码：{{item.major_code}})</div>
        <div class="item-opts">
          <el-button style="color:#F56C6C" type="text" size="mini" @click="() => modifyMajorName(item.uuid, item.major_code)">修改名称</el-button>
          <el-button style="color:#67C23A" type="text" size="mini" @click="() => modifyMajorCode(item.uuid, item.major_name)">修改专业代码</el-button>
          <el-button style="color:#409EFF" type="text" size="mini" @click="() => deleteMajor(item.uuid)">删除</el-button>
        </div>
      </div>
      <div class="add-one" @click="addMajorVisible=true"><el-button type="primary" plain size="small"><i class="el-icon-plus"></i>增加一项</el-button></div>
    </el-card>

    <!-- 修改指标 -->
    <el-dialog title="选择所属指标" :visible.sync="modifyIndexVisible" width="30%" @close="closeModifyDialog">
      <el-select v-model="curChoice" placeholder="请选择" @change="handleIndexChange">
        <el-option v-for="item in indexs" :key="item.uuid" :label="item.name" :value="item.uuid"></el-option>
      </el-select>
    </el-dialog>

    <!-- 新增一项专业 -->
    <el-dialog title="新增专业" :visible.sync="addMajorVisible" width="30%">
      <el-input v-model="majorName" placeholder="请输入专业名称" maxlength="10"></el-input>
      <el-input style="margin-top: 16px" v-model="majorCode" placeholder="请输入专业代码" type="number" maxlength="10"></el-input>
      <el-button type="primary" style="margin-top:16px" size="medium" @click="addMajor()">增加</el-button>
    </el-dialog>

    <!-- 导出配置对话框 -->
    <el-dialog title="提示" :visible.sync="showExportDialog" width="30%" @close="exportConfigDialogClosed()">
      <el-checkbox border size="small" v-model="indicatorBox">指标体系</el-checkbox>
      <el-checkbox border size="small" v-model="ruleBox">加分规则</el-checkbox>
      <el-checkbox border size="small" v-model="majorBox">专业列表</el-checkbox>
      <div>
        <el-button type="primary" size="small" style="margin-top:20px" @click="exportConfigs()">导出</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {download} from '../../../plugins/download.js'
  export default {
    data() {
      return {
        table:  [],
        defaultProps: {
          children: 'children',
          label: 'name'
        },
        tempConfig: [],
        change: [],
        gradePercent: 0,
        modifyIndexVisible: false,
        indexs: [],
        curChoice: '',
        curRule: '',
        showIndexs: [],
        indexj: {},
        majors: [],
        addMajorVisible: false,
        majorName: '',
        majorCode: '',
        showIllustration: false,
        showExportDialog: false,
        indicatorBox: false,
        ruleBox: false,
        majorBox: false
      }
    },
    created() {
      this.getIndexs()
      this.getContestConfig()
      this.getGradePercent()
      this.getMajors()
    },
    methods: {
      // 获取赛事加分规则配置并渲染
      async getContestConfig() {
        let {data: res} = await this.$http.post("/getContestConfig")
        for (let i in res.data) {
          this.table.push(JSON.parse(res.data[i].classconfig))
          this.tempConfig.push(JSON.parse(res.data[i].classconfig))
        }
      },
      append(data) {
        let newChild = { uuid: this.$uuid(), name: '默认', tag: 'normal', children: [] }
        if (!data.children) {
          this.$set(data, 'children', [])
        }
        data.children.push(newChild)
      },
      remove(node, data) {
        this.$confirm('确认删除?','确定','取消').then(() => {
          let parent = node.parent;
          let children = parent.data.children || parent.data;
          let index = children.findIndex(d => d.uuid === data.uuid);
          children.splice(index, 1);
        })
      },
      rescore(data) {
        if (!(data.tag == 'root')) return this.$msg.error("只有叶子节点才能修改分值")
        this.$prompt('请输入名称', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          if (isNaN(value)) return this.$msg.error("请输入正确的数值")
          data.score = value
        })
      },
      setroot(node, data) {
        // 取消设置为叶子节点
        if (data.tag == 'root') {
          let parent = node.parent
          let newChild = { uuid: data.uuid, name: data.name, children: [] }
          let children = parent.data.children || parent.data
          let index = children.findIndex(d => d.uuid === data.uuid)
          children.splice(index, 1)
          if (!parent.data.children) {
            this.$set(parent.data, 'children', [])
          }
          parent.data.children.push(newChild)
          return
        }
        // 设置叶子节点
        if (data.children.length > 0) return this.$msg.error("存在下一级的时候不能设置为叶子节点")
        let temp = {
          uuid: data.uuid,
          name: data.name,
          tag: 'root',
          score: 0
        }
        this.$prompt('请为叶子节点设置分值，最多保留两位小数', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          if (isNaN(value) || value.trim().length == 0) return this.$msg.error("请输入正确的数值")
          temp.score = value
          let parent = node.parent
          let children = parent.data.children || parent.data
          let index = children.findIndex(d => d.uuid === data.uuid)
          children.splice(index, 1)
          parent.data.children.push(temp)
        }).catch(()=>{return this.$msg.error("输入加分分值才能设置为叶子节点")})
      },
      addRule() {
        let newOne = {
          uuid: this.$uuid(),
          name: '默认',
          children: [],
          tag: 'top',
          indexid: ''
        }
        this.table.push(newOne)
      },
      rename(data) {
        this.$prompt('请输入名称', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          data.name = value
        })
      },
      async submit() {
        let names = []
        for (let i in this.table) {
          names.push(this.table[i].name)
        }
        for (let i in this.table) {
          if (names.indexOf(this.table[i].name) != i) return this.$msg.error("父节点名称不能相同")
        }
        let fd = new FormData()
        let nc = {}
        let it = {}
        for (let i in this.table) {
          fd.append(this.table[i].uuid, JSON.stringify(this.table[i]))
          nc[this.table[i].uuid] = this.table[i].name
          it[this.table[i].uuid] = this.table[i].indexid
        }
        fd.append('name', JSON.stringify(nc))
        fd.append('index', JSON.stringify(it))
        console.log(fd);
        let {data: res} = await this.$http.post('/modifyContestConfig', fd)
        if (!res) return
        if(res.code != 0) return this,$msg.error(res.message)
        this.$msg.success("配置成功!")
        location.reload()
      },
      async getGradePercent() {
        let {data: res} = await this.$http.post("/getGradePercent")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.gradePercent = res.data
      },
      async submitGrade() {
        let {data: res} = await this.$http.post("/modifyGradePercent", this.$qs.stringify({percent: this.gradePercent}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.$msg.success("提交修改成功")
      },
      async mofidyrule(uuid, indexid) {
        if (this.indexs.length == 0) {
          let {data: res} = await this.$http.post("/getIndexs")
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          this.indexs = res.data
        }
        this.modifyIndexVisible = true
        this.curRule = uuid
        this.curChoice = indexid
      },
      async getIndexs() {
        let {data: res} = await this.$http.post("/getIndexs")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        for (let i in res.data) {
          this.indexj[res.data[i].uuid] = res.data[i].name
        }
        this.showIndexs = res.data
      },
      async modifyIndex(uuid) {
        this.$prompt('请输入新得指标名称', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(async ({ value }) => {
          // let newOne = {
          //   uuid: uuid,
          //   name: value
          // }
          if (value.trim().length == 0) return this.$msg.error("请输入正确的指标名称")
          let {data: res} = await this.$http.post("/modifyIndex", this.$qs.stringify({uuid: uuid, name: value}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          for (let i in this.showIndexs) {
            if (this.showIndexs[i].uuid == uuid) {
              this.showIndexs[i].name = value
            }
          }
          return this.$msg.success("修改成功")
        })
      },
      async deleteIndex(uuid) {
        let {data: res} = await this.$http.post("/deleteIndex", this.$qs.stringify({uuid: uuid}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        for (let i in this.showIndexs) {
          if (this.showIndexs[i].uuid == uuid) {
            this.showIndexs.splice(i, 1)
          }
        }
        return this.$msg.success("删除成功")
      },
      closeModifyDialog() {
        this.indexs = []
        this.curRule = ''
      },
      addIndex() {
        this.$prompt('请输入指标名称', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(async ({ value }) => {
          let newOne = {
            uuid: this.$uuid(),
            name: value
          }
          if (value.trim().length == 0) return this.$msg.error("请输入正确的指标名称")
          let {data: res} = await this.$http.post("/addIndex", this.$qs.stringify({uuid: newOne.uuid, name: newOne.name}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          this.showIndexs.push(newOne)
          return this.$msg.success("增加成功")
        })
      },
      handleIndexChange(e) {
        for (let i in this.table) {
          if (this.table[i].uuid == this.curRule) {
            this.table[i].indexid = e
          }
        }
        this.$msg.warning("修改已保存，提交后才能生效")
        this.modifyIndexVisible = false
        this.closeModifyDialog()
        this.curChoice = ''
      },
      async getMajors() {
        let {data: res} = await this.$http.post("/getMajors")
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        this.majors = res.data
      },
      async addMajor() {
        if (this.majorName.trim().length == 0 || this.majorCode.trim().length == 0) return this.$msg.error("请输入正确的专业名称和代码")
        let uuid = this.$uuid()
        let {data: res} = await this.$http.post("/addMajor", this.$qs.stringify({uuid: uuid,name:this.majorName,code:this.majorCode}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        let newOne = {
          uuid: uuid,
          major_name: this.majorName,
          major_code: this.majorCode
        }
        this.majors.push(newOne)
        this.majorName = ''
        this.addMajorVisible = false
        this.$msg.success("添加成功")
      },
      async modifyMajorName(uuid, code) {
        this.$prompt('请输入专业名称', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(async ({ value }) => {
          if (value.trim().length == 0) return this.$msg.error("请输入正确的专业名称")
          let {data: res} = await this.$http.post("/modifyMajor", this.$qs.stringify({uuid: uuid, name: value, code: code}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          for (let i in this.majors) {
            if (this.majors[i].uuid == uuid) {
              this.majors[i].major_name = value
            }
          }
          this.$msg.success("修改成功!")
        })
      },
      async modifyMajorCode(uuid, name) {
        this.$prompt('请输入专业代码', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(async ({ value }) => {
          if (value.trim().length == 0) return this.$msg.error("请输入正确的专业代码")
          let {data: res} = await this.$http.post("/modifyMajor", this.$qs.stringify({uuid: uuid, name: name, code: value}))
          if (!res) return
          if (res.code != 0) return this.$msg.error(res.message)
          this.$msg.success("修改成功!")
          for (let i in this.majors) {
            if (this.majors[i].uuid == uuid) {
              this.majors[i].major_code = value
            }
          }
        })
      },
      async deleteMajor(uuid) {
        let {data: res} = await this.$http.post("/deleteMajor", this.$qs.stringify({uuid: uuid}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        for (let i in this.majors) {
          if (this.majors[i].uuid == uuid) {
            this.majors.splice(i, 1)
          }
        }
        this.$msg.success("删除成功")
      },
      async exportConfigs() {
        if (!this.indicatorBox && !this.ruleBox && !this.majorBox) {
          return this.$msg.warning("请至少选择一项需要导出的配置!")
        }
        let indicator = 0
        let rule = 0
        let major = 0
        if (this.indicatorBox) indicator = 1
        if (this.ruleBox) rule = 1
        if(this.majorBox) major = 1
        let {data: res} = await this.$http.post("/exportConfig", this.$qs.stringify({indicator:indicator,rule:rule,major:major}))
        if (!res) return
        if (res.code != 0) return this.$msg.error(res.message)
        if (indicator == 1) download(this.$curl + res.data.indicator, '指标体系配置_导出.xls')
        if (rule == 1) download(this.$curl + res.data.rule, '加分规则配置_导出.xls')
        if (major == 1) download(this.$curl + res.data.major, '专业列表_导出.xls')
        this.$msg.success("配置导出下载成功！")
        this.showExportDialog = false
      },
      exportConfigDialogClosed() {
        this.indicatorBox = false
        this.ruleBox = false
        this.majorBox = false
      }
    }
  }
</script>

<style lang="less" scoped>
.gradem {
  display: flex;
  justify-content: space-between;
}
.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-top: 10px !important;
    padding-bottom: 10px !important;
    padding-right: 8px;
    line-height: 26px;
    &:hover {
      .option {
        display: block;
      }
    }
    .option {
      display: none;
      height: 26px;
      button {
        padding: 0;
        line-height: 26px;
        border: none !important;
      }
    }
  }
  .add-one {
    font-size: 14px;
    margin-top: 16px;
    text-align: center;
  }
  .index-item {
    line-height: 24px;
    padding: 12px 0 12px 8px;
    font-size: 14px;
    position: relative;
    &:hover {
      background-color: #F5F7FA;
      .item-opts {
        display: block;
      }
    }
    .item-opts {
      display: none;
      position: absolute;
      top: 12px;
      right: 16px;
      font-size: 14px;
    }
  }
  .config_desc {
    text-align: right;
  }
</style>