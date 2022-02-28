<template>
  <div class="container">
    <div class="content">
      <div class="top">
        <div class="header">
          <img alt="logo" class="logo" src="static/img/logo.png" />
          <span class="title">{{systemName}}</span>
        </div>
        <div class="desc"></div>
      </div>
      <component :is="componentName" @regist="handleRegist" @reset="handleReset" class="main-content"></component>
     <!-- <component :is="componentName" @regist="handleReset" class="main-content"></component>-->
    </div>

    <global-footer :copyright="copyright" />
  </div>
</template>

<script>
import GlobalFooter from '../common/GlobalFooter'
import Login from './Login'
import Regist from './Regist'
import Reset from './Reset'

export default {
  name: 'Common',
  components: {GlobalFooter, Login, Regist,Reset},
  data () {
    return {
      componentName: 'Login'
    }
  },
  computed: {
    systemName () {
      return this.$store.state.setting.systemName
    },
    copyright () {
      return this.$store.state.setting.copyright
    }
  },
  methods: {
    handleRegist (val) {
      this.componentName = val
    },
    handleReset(val){
      this.componentName = val
    }
  }
}
</script>

<style lang="less" scoped>

  .container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    overflow: auto;
    /*background: #f0f2f5 url('https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg') no-repeat center 110px;
    background-size: 100%;*/
    /*background-color: #ffffff;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 100 100'%3E%3Cg stroke='%23CCC' stroke-width='0' %3E%3Crect fill='%23F5F5F5' x='-60' y='-60' width='110' height='240'/%3E%3C/g%3E%3C/svg%3E");
   */
    background: url(/static/img/back.svg) no-repeat;
    background-size: 60%;
    background-position: left;
    .content {
      padding: 0px 0;
      flex: 1;
      @media (min-width: 768px){
        padding: 10px 0 10px;
      }
      .top {
        text-align: left;
        .header {
          height: 50px;
          line-height: 50px;
          padding: 0px 0;
          a {
            text-decoration: none;
          }
          .logo {
            width: 80px;
            height: 60px;
            vertical-align: center;
            margin-right: 10px;
            margin-left: 15px;
          }
          .title {
            font-size: 20px;
            color: rgba(0,0,0,.85);
            font-family: Helvetica, Tahoma, Arial, "PingFang SC", "Hiragino Sans GB", "Heiti SC", "Microsoft YaHei", "WenQuanYi Micro Hei",sans-serif;//'Myriad Pro', 'Helvetica Neue', Arial, Helvetica, sans-serif;
            font-weight: 500;
            position: relative;
            top: 6px;
          }
        }
        .desc {
          font-size: 14px;
          color: rgba(0, 0, 0, 0.05);
          margin-top: 12px;
          margin-bottom: 40px;
        }
      }
      .main-content {
        width: 400px;
        margin: 50px 200px;
        float:right;
        background: #fafafa;
        @media screen and (max-width: 576px) {
          width: 95%;
        }
        @media screen and (max-width: 320px) {
          .captcha-button{
            font-size: 14px;
          }
        }
      }
    }
  }
</style>
