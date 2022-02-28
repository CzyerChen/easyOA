<template>
  <div>
    <a-form style="max-width: 500px; margin: 40px auto 0;">
      <a-alert
        :closable="true"
        message="确认提交后，您的申请流程正式开始，请再次确认表单内容。"
        style="margin-bottom: 24px;"
      />
      <a-form-item
        label="请假类别"
        :labelCol="{span: 5}"
        :wrapperCol="{span: 19}"
        class="stepFormText"
      >
        {{data.leaveType}}
      </a-form-item>
      <a-form-item
        label="请假时间"
        :labelCol="{span: 5}"
        :wrapperCol="{span: 19}"
        class="stepFormText"
      >
        {{data.leaveTimeFrom}} - {{data.leaveTimeTo}}
      </a-form-item>
      <a-form-item
        label="请假原因"
        :labelCol="{span: 5}"
        :wrapperCol="{span: 19}"
        class="stepFormText"
      >
        {{data.leaveReason}}
      </a-form-item>
      <a-form-item
        label="上传文件"
        :labelCol="{span: 5}"
        :wrapperCol="{span: 19}"
        class="stepFormText"
      >
        {{data.uploadFile === '' ? '未上传' : '已上传' }}
      </a-form-item>
      <a-form-item :wrapperCol="{span: 19, offset: 5}">
        <a-button :loading="loading" type="primary" @click="nextStep">提交</a-button>
        <a-button style="margin-left: 8px" @click="prevStep">上一步</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
  import db from 'utils/localstorage'
  export default {
    name: "Step2",
    component:[db],
    data () {
      return {
        loading: false,
        data: {
          leaveReason:'',
          leaveTimeFrom:'',
          leaveTimeTo:'',
          leaveType:'',
          uploadFile:'',
          userId:''
        }
      }
    },
    mounted(){
     this.data = db.get("APPLY_INFO")
    },
    methods: {
      nextStep () {
        this.loading = true
        this.$post('apply', {
          ...this.data
        }).then((r)=>{
          this.loading= false
          let feedBack = r.data.data
          localStorage.setItem("FEED_BACK",JSON.stringify(feedBack))
          this.$emit('success')
          this.$emit('nextStep')
        }).catch((e)=>{
          this.loading=false
          this.$message.error("提交失败，请重试")
        })
      },
      prevStep () {
        this.$emit('prevStep')
      }
    }
  }
</script>

<style lang="less" scoped>
  .stepFormText {
    margin-bottom: 24px;

    .ant-form-item-label,
    .ant-form-item-control {
      line-height: 22px;
    }
  }

</style>
