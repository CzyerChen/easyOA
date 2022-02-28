<template>
  <a-range-picker
    :key="id"
    ref="rangeDate"
    :format="dateFormat"
    @change="onChange" style="width: 100%"></a-range-picker>
</template>

<script>
  import moment from 'moment';

  export default {
    name: 'RangeDate',
    components: {moment},
    data() {
      return {
        dateFormat: 'YYYY-MM-DD',
        startDay: '',
        endDay: '',
        id: +new Date()
      }
    },
    props:{
      value:{
        type:Array,
        default(){
          let range=[]
          //获取当前时间
          let date = new Date();
          //获取当前月的第一天
          let monthStart = date.setDate(1);
          //获取当前月
          let currentMonth = date.getMonth();
          //获取到下一个月
          let nextMonth = ++currentMonth;
          //获取到下个月的第一天
          let nextMonthFirstDay = new Date(date.getFullYear(), nextMonth, 1);
          //一天时间的毫秒数
          let oneDay = 1000 * 60 * 60 * 24;

          range[0] = moment(monthStart);
          range[1] = moment(nextMonthFirstDay - oneDay);
          return range;
        }
      }
    },
    methods: {
      onChange(date, dateString) {
        this.$emit('change', dateString)
      },
      reset() {
        this.id = +new Date()
      }
    },
    mounted() {
    }
  }
</script>
