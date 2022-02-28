<template>
  <a-drawer
    title="规则详情"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="true"
    @close="close"
    :visible="ruleInfoVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <p><a-icon type="heart" />规则名称：{{ruleInfoData.ruleName}}</p>
    <p ><a-icon type="clock-circle" />最大允许请假时长：{{ruleInfoData.maxPermitDay}}</p>
    <p><a-icon type="calculator" />最小请假天数：{{ruleInfoData.leaveDaysFrom}}</p>
    <p><a-icon type="calculator" />最大请假天数(不包含)：{{ruleInfoData.leaveDaysTo}}</p>
    <p><a-icon type="calculator" />提前请假时间：{{ruleInfoData.forwardDays}}</p>
    <p><a-icon type="calculator" />年龄要求：{{ruleInfoData.age}}</p>
    <p><a-icon type="calculator"/>  最小工龄：{{ruleInfoData.workYearsFrom}}</p>
    <p><a-icon type="calculator"/>  最大工龄（包含）：{{ruleInfoData.workYearsTo}}</p>
    <p><a-icon type="profile"/> 需要文件证明：{{ruleInfoData.needUpload}}</p>
    <p :title="ruleInfoData.fileRequired"><a-icon type="profile"/>  证明文件要求：{{ruleInfoData.fileRequired}}</p>
    <p :title="ruleInfoData.notice"><a-icon type="profile"/>  说明：{{ruleInfoData.notice}}</p>
    <p><a-icon type="clock-circle"/>  创建时间：{{ruleInfoData.createTime}}</p>
    <p><a-icon type="clock-circle"/>  更新时间：{{ruleInfoData.updateTime}}</p>
  </a-drawer>
</template>
<script>
  export default {
    name: 'RuleInfo',
    props: {
      ruleInfoVisiable: {
        require: true,
        default: false
      },
      ruleInfoData: {
        require: true
      }
    },
    data () {
      return {
        loading: true,
      }
    },
    methods: {
      close () {
        this.$emit('close')
      }
    },
    watch: {
      ruleInfoVisiable () {
      }
    }
  }
</script>
