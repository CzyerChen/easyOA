<template>
  <a-drawer
    title="新增申请"
    :maskClosable="false"
    width=850
    placement="right"
    :closable="true"
    @close="onClose"
    :visible="applicationAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-card :bordered="false" style="width: 100%;">
      <a-steps class="steps" :current="currentTab">
        <a-step title="填写申请信息" style="width: 30%;margin-left: 10%"/>
        <a-step title="确认申请信息" style="width: 30%"/>
        <a-step title="完成申请提交" style="width: 30%"/>
      </a-steps>
      <div class="content">
        <step1 v-if="currentTab === 0" @nextStep="nextStep"/>
        <step2 v-if="currentTab === 1" @nextStep="nextStep" @prevStep="prevStep"/>
        <step3 v-if="currentTab === 2" @prevStep="prevStep" @finish="finish"/>
      </div>
    </a-card>
  </a-drawer>
</template>

<script>
  import Step1 from './Step1'
  import Step2 from './Step2'
  import Step3 from './Step3'

  const formItemLayout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 17 }
  }

  export default {
    name: "ApplicationAdd",
    props: {
      applicationAddVisiable: {
        default: false
      }
    },
    components: {
      Step1,
      Step2,
      Step3
    },
    data() {
      return {
        description: '按照流程指示，填写休假申请单',
        currentTab: 0,
        loading: false,
        formItemLayout,
        form: null,
      }
    },
    methods: {

      // handler
      nextStep() {
        if (this.currentTab < 2) {
          this.currentTab += 1
        }
      },
      prevStep() {
        if (this.currentTab > 0) {
          this.currentTab -= 1
        }
      },
      finish() {
        this.currentTab = 0
      },
      onClose () {
        this.reset()
        localStorage.removeItem("APPLY_INFO")
        localStorage.removeItem("FEED_BACK")
        this.$emit('close')
      },
      reset() {
        //树结构也需要清空
        this.loading = false
        this.currentTab=0
      },
    },
    watch:{
      applicationAddVisiable(){
        if(this.applicationAddVisiable){

        }
      }
    }
  }
</script>

<style lang="less" scoped>
  .steps {
    width: 100%;
    margin: 16px auto;
  }
</style>
