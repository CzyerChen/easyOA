<template>
  <div style="width:100%">
    <div class="vacation-all">
      <div>
        <p class="vacation">常规假期</p>
      </div>
      <a-row :gutter="24">
        <a-col :span="12">
          <a-card title="年休假" :bordered=false :hoverable=true>
            <!-- <p class="vacation-data">{{vacation.annualLeaveTotal}}</p> -->
            <template>
  <a-steps progressDot :current="this.stage" size="small">
    <a-step title="去年留存" v-if="moment().isBefore(moment().year()+'-03-01')" :description= this.restAnnualLeave />
    <a-step title="上半年可休" :description=this.annualLeave1 />
    <a-step title="下半年可休" :description=this.annualLeave2 />
  </a-steps>
</template>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card title="带薪病假" :bordered=false :hoverable=true>
            <p class="vacation-data">{{vacation.sickLeave}}</p>
          </a-card>
        </a-col>
      <!--  <a-col :span="8">
          <a-card title="事假" :bordered=false :hoverable=true>
            <p class="vacation-data">{{vacation.casualLeave}}</p>
          </a-card>
        </a-col>-->
      </a-row>
    </div>
<!--
    <div class="vacation-all" >
      <div>
        <p class="vacation">特殊假期</p>
      </div>
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card title="婚假" :bordered=false :hoverable=true>
            <p class="vacation-data-spec">{{vacation.marriageLeave}}</p>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card title="丧假" :bordered=false :hoverable=true>
            <p class="vacation-data-spec">{{vacation.funeralLeave}}</p>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card title="产假" :bordered=false :hoverable=true>
            <p class="vacation-data-spec">{{vacation.maternityLeave}}</p>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card title="陪产假" :bordered=false :hoverable=true>
            <p class="vacation-data-spec">{{vacation.paternityLeave}}</p>
          </a-card>
        </a-col>
      </a-row>
    </div>-->
    <a-divider/>
    <div>
      <p id="log-p">近期申请日志</p>
      <light-timeline :items='items'>
        <template slot='tag' slot-scope='{ item }'>
          {{item.tag}}
        </template>
        <template slot='content' slot-scope='{ item }'>
          {{item.content}}
        </template>
      </light-timeline>
    </div>
  </div>
  <!--  <div id="mycharts" style="width: 600px;height:400px;"></div>-->

</template>

<script>
  import echarts from 'echarts';
  import {mapState, mapMutations} from 'vuex';
  import moment from 'moment'
  moment.locale('zh-cn')

  export default {
    name: "Vacation",
    components: {echarts},
    data() {
      return {
        seriesData: [],
        loading:false,
        items: [],
        vacation:{},
        restAnnualLeave: '0 天',
        annualLeave1: '0 天',
        annualLeave2: '0 天',
        stage: 1, 
      };
    },
    created() {
    },
    computed:{
      ...mapState({
        currentUser: state => state.account.user
      })
    },
    mounted() {
      this.getTimeline();
      this.getUserVacation();
      if(moment().isBefore('2020-03-01')){
        this.stage = 1;
      }else if(moment().isBefore('2020-07-01')){
        this.stage = 0;
      }else{
        this.stage =1;
      }
    },
    watch: {
      seriesData(val, oldVal) {
        this.setOptions(val);
      }
    },
    methods: {
      moment,
      ...mapMutations({
        setUser: 'account/setUser'
      }),
      getUserVacation(){
        this.$get(`vacation/user/${this.currentUser.userId}`).then((r)=>{
          let data = r.data.data;
           this.vacation = data
           if(data.restAnnualLeave){
               this.restAnnualLeave = data.restAnnualLeave+' 天';
           }

           if(data.annualShould){
             this.annualLeave2 = data.annualShould /2 +' 天';
           }
           if(data.annualLeave){
             this.annualLeave1 = data.annualShould/2  -(data.annualShould - data.annualLeave) + ' 天';
           }
        }).catch((e)=>{})
      },
      initCharts() {
        this.chart = this.$echarts.init(document.getElementById("mycharts"));
        this.setOptions();
      },
      getTimeline(){
        this.loading=true
        this.$get(`flow/list/${this.currentUser.userId}`).then((r)=>{
          this.items = r.data.data
          this.loading = false
        }).catch((e) =>{
          this.$message("申请日志获取异常")
          this.loading=false
        })
      },
      setOptions(series) {
        this.$get("/pie").then((r) => {
          this.pieData = r.data
          console.log(this.pieData)

          this.chart.setOption(
            {
              title: {
                text: '测试echarts',
                subtext: '纯属虚构',
                x: 'center'
              },
              tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
              },
              legend: {
                orient: 'vertical',
                x: 'left',
                data: ['销量']
              },
              series: series
            });

        })
      }
    }
  };
</script>
<style lang="less" scoped>
  .mycharts {
    height: 400px;
  }
  .vacation {
    font-size: .9rem;
    font-weight: 600;
  }
  .vacation-data {
    text-align: center;
    font-size: 2rem;
    font-weight: 300;
    margin: 0 0 0 0;

  }
  .vacation-data-spec{
    text-align: center;
    font-size: 1.5rem;
    font-weight: 300;
    margin:0 0 0 0;
  }

  .line-container{
    color: #606c76;
    font-size: 14px;
    font-family: "Helvetica Neue For Number", "Chinese Quote", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial, sans-serif;
    box-sizing: border-box;
    position: relative;
    list-style: none;
    margin-left: 10rem;

  }

  .line-container .item-tag {
    position: absolute;
    left: -10rem;
    width: 87px;
    text-align: center;
    color: #949fa8;
    font-size: 11.66667px;
  }
  .log-p{
    font-size: .9rem;
    font-weight: 600
  }
  .vacation-all{
    background-color: rgba(0,0,0,0.04);
    padding: 24px;
    width:100%
  }

</style>
