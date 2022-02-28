<template>
  <div>
    <a-form style="margin: 40px auto 0;">
      <result title="操作成功" :is-success="true" description="您的申请已开始被受理">
        <div class="information">
          <a-row>
            <a-col :sm="8" :xs="24">申请ID：</a-col>
            <a-col :sm="16" :xs="24">{{feedBack.applicationId}}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">申请内容：</a-col>
            <a-col :sm="16" :xs="24">{{feedBack.content}}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">申请时间：</a-col>
            <a-col :sm="16" :xs="24">{{feedBack.leaveRange}}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">申请天数：</a-col>
            <a-col :sm="16" :xs="24">{{feedBack.days}}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">申请状态：</a-col>
            <a-col :sm="16" :xs="24">{{feedBack.status}}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">申请受理人：</a-col>
            <a-col :sm="16" :xs="24">{{feedBack.assignee}}</a-col>
          </a-row>
        </div>
        <!--<div slot="action">-->
          <!--<a-button type="primary" @click="back">返回列表</a-button>-->
          <!--<a-button style="margin-left: 8px" @click="toOrderList">查看详情</a-button>-->
        <!--</div>-->
      </result>
    </a-form>
  </div>
</template>

<script>
  import Result from '../../result/Result'
  import db from 'utils/localstorage'

  export default {
    name: "Step3",
    components: {
      Result,
      db
    },
    data () {
      return {
        feedBack:{},
        loading: false,
        applicationDetail:{
          visible: false
        }
      }
    },
    mounted(){
      this.feedBack = db.get("FEED_BACK")
    },
    methods: {
      back () {
        localStorage.removeItem("FEED_BACK")
        this.feedBack={}
        this.$emit('success')
      },
      toOrderList () {
        localStorage.removeItem("FEED_BACK")

        //this.$router.push('/home/first')
// 对象
        this.$router.push({ path: '/application/submission' })
// 命名的路由
        //this.$router.push({ name: 'Application', params: {}})

        this.feedBack = {}
      }
    }
  }
</script>
<style lang="less" scoped>
  .information {
    line-height: 22px;

    .ant-row:not(:last-child) {
      margin-bottom: 24px;
    }
  }
  .money {
    font-family: "Helvetica Neue",sans-serif;
    font-weight: 500;
    font-size: 20px;
    line-height: 14px;
  }
</style>
