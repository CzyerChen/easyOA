<template>
  <a-drawer
    title="公告信息"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="true"
    @close="close"
    :visible="noticeInfoVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <p><a-icon type="crown" />&nbsp;&nbsp;公告名称：{{noticeInfoData.title}}</p>
    <p><a-icon type="book" />&nbsp;&nbsp;公告描述：{{noticeInfoData.msgContent}}</p>
    <p><a-icon type="user" />&nbsp;&nbsp;发送者：{{noticeInfoData.sender}}</p>
    <p><a-icon type="clock-circle" />&nbsp;&nbsp;发送时间：{{noticeInfoData.sendTime}}</p>
    <p><a-icon type="clock-circle" />&nbsp;&nbsp;创建时间：{{noticeInfoData.createTime}}</p>
  </a-drawer>
</template>
<script>
  export default {
    name: 'NoticeInfo',
    props: {
      noticeInfoVisiable: {
        require: true,
        default: false
      },
      noticeInfoData: {
        require: true
      }
    },
    methods: {
      close () {
        this.$emit('close')
        this.checkedKeys = []
      }
    },
    watch: {
      noticeInfoVisiable () {
      }
    }
  }
</script>
