<template>
  <a-drawer
    title="节日信息"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="true"
    @close="close"
    :visible="vacationInfoVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <p><a-icon type="heart" />&nbsp;&nbsp;节日名称：{{vacationInfoData.name}}</p>
    <p :title="vacationInfoData.festival"><a-icon type="clock-circle" />&nbsp;&nbsp;节日日期：{{vacationInfoData.festival}}</p>
    <p><a-icon type="calculator" />&nbsp;&nbsp;放假天数：{{vacationInfoData.days}}</p>
    <p><a-icon type="calculator" />&nbsp;&nbsp;开始日期：{{vacationInfoData.startDate}}</p>
    <p><a-icon type="calculator" />&nbsp;&nbsp;结束日期：{{vacationInfoData.endDate}}</p>
    <p><a-icon type="fire" />&nbsp; 具体日期：{{vacationInfoData.detail}}</p>
    <p><a-icon type="profile"/>  放假说明：{{vacationInfoData.description}}</p>
    <p :title="vacationInfoData.advice"><a-icon type="coffee" />&nbsp;&nbsp;建议：{{vacationInfoData.advice}}</p>
  </a-drawer>
</template>
<script>
  export default {
    name: 'VacationInfo',
    props: {
      vacationInfoVisiable: {
        require: true,
        default: false
      },
      vacationInfoData: {
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
      vacationInfoVisiable () {
        /*if (this.vacationInfo) {
          this.$get('vacation/'+this.vacationInfoData.vacationId).then((r) => {
            this.loading = false
            let data = r.data.data
            if(data != null){
             this.vacationDetail = data
            }
          })
        }*/
      }
    }
  }
</script>
