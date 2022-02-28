<template>
  <a-drawer
    title="通知详情"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="true"
    @close="close"
    :visible="myNotesInfoVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form-item
      label="通知标题"
      :labelCol="{span: 3}"
      :wrapperCol="{span: 18}"
    >
      {{myNotesInfoData.title}}
    </a-form-item>
    <a-form-item
      label="发送人"
      :labelCol="{span: 3}"
      :wrapperCol="{span: 18}"
    >
      {{myNotesInfoData.sender}}
    </a-form-item>
    <a-form-item
      label="发送时间"
      :labelCol="{span: 3}"
      :wrapperCol="{span: 18}"
    >
      {{myNotesInfoData.sendTime}}
    </a-form-item>
    <a-form-item
      label="是否已读"
      :labelCol="{span: 3}"
      :wrapperCol="{span: 18}"red
      v-if ="myNotesIntoType===0"
    >
      <a-tag  v-if="myNotesInfoData.checked === true" color="blue">已读</a-tag>
      <a-tag  v-if="myNotesInfoData.checked === false" color="red">未读</a-tag>
    </a-form-item>
    <a-form-item
      label="优先等级"
      :labelCol="{span: 3}"
      :wrapperCol="{span: 18}"
    >
      <a-tag  v-if="myNotesInfoData.priority === 'L'" color="blue">一般消息</a-tag>
      <a-tag  v-if="myNotesInfoData.priority === 'M'" color="orange">重要消息</a-tag>
      <a-tag  v-if="myNotesInfoData.priority === 'H'" color="red">紧急消息</a-tag>
    </a-form-item>
    <a-form-item
      label="消息内容"
      :labelCol="{span: 3}"
      :wrapperCol="{span: 18}"
    >
      {{myNotesInfoData.msgContent}}
    </a-form-item>


  </a-drawer>
</template>
<script>
  export default {
    name: 'MyNotesInfo',
    props: {
      myNotesInfoVisiable: {
        require: true,
        default: false
      },
      myNotesInfoData: {
        require: true
      },
      myNotesIntoType:{
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
        this.$put('notice/check/' ,{
          noticeIds:this.myNotesInfoData.noticeId
        }).then(() => {
          this.$emit('close')
        }).catch((r)=>{
          this.$emit('close')
        })
      },
    },
    watch: {
      myNotesInfoVisiable () {
      }
    }
  }
</script>
