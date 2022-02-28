<template>
  <a-drawer
    title="我的申请"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="true"
    @close="close"
    :visible="applicationInfoVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <p><a-icon type="right" /> 申请ID：{{applicationInfoData.applicationId}}</p>
    <p ><a-icon type="right" /> 申请人：{{applicationInfoData.userName}}</p>
    <p ><a-icon type="right" /> 所属部门：{{applicationInfoData.deptName}}</p>
    <p ><a-icon type="right" /> 职位：{{applicationInfoData.position}}</p>
    <p ><a-icon type="right" /> 请假类型：{{applicationInfoData.leaveType}}</p>
    <p ><a-icon type="right" /> 请假时长：{{applicationInfoData.days}}</p>
    <p ><a-icon type="right" /> 请假时间段：{{applicationInfoData.startTime}} 至 {{applicationInfoData.endTime}}</p>
    <p ><a-icon type="right" /> 请假原因：{{applicationInfoData.leaveReason}}</p>
    <p ><a-icon type="right" /> 申请状态：{{applicationInfoData.status}}</p>
    <p ><a-icon type="right" /> 申请阶段： {{applicationInfoData.stage}}</p>

    <a-divider/>
    <p style="font-size: 16px;font-weight:500">申请流程详情</p>
    <light-timeline :items='items'>
    </light-timeline>
  </a-drawer>
</template>
<script>
  export default {
    name: 'ApplicationInfo',
    props: {
      applicationInfoVisiable: {
        require: true,
        default: false
      },
      applicationInfoData: {
        require: true
      }
    },
    data () {
      return {
        items: [],
        loading: true,
      }
    },
    methods: {
      close () {
        this.$emit('close')
      }
    },
    watch: {
      applicationInfoVisiable() {
        if(this.applicationInfoVisiable){
          this.$get(`flow/${this.applicationInfoData.applicationId}`)
            .then((r)=>{
              this.items = r.data.data
            }).catch((e)=>{

          })
        }
      }
    }
  }
</script>
