<template>
  <a-popover
    trigger="click"
    placement="bottomRight"
    :autoAdjustOverflow="true"
    :arrowPointAtCenter="true"
    overlayClassName="header-notice-wrapper"
    @visibleChange="handleHoverChange"
    :overlayStyle="{ width: '300px', top: '50px' }">
    <template slot="content">
      <a-spin :spinning="loadding">
        <a-tabs>
          <a-tab-pane :tab="msg1Title" key="1">
            <a-list>
              <a-list-item :key="index" v-for="(record, index) in announcement1">
                <div style="margin-left: 5%;width: 80%">
                  <p><a @click="clickInfo(record)">标题：{{ record.title }}</a></p>
                  <p style="color: rgba(0,0,0,.45);margin-bottom: 0px">{{ record.createTime }} 发布</p>
                </div>
                <div style="text-align: right">
                  <a-tag @click="clickInfo(record)" v-if="record.priority === 'L'" color="blue">一般消息</a-tag>
                  <a-tag @click="clickInfo(record)" v-if="record.priority === 'M'" color="orange">重要消息</a-tag>
                  <a-tag @click="clickInfo(record)" v-if="record.priority === 'H'" color="red">紧急消息</a-tag>
                </div>
              </a-list-item>
              <div style="margin-top: 5px;text-align: center">
                <a-button  type="dashed" block @click="clickInfo(record)">更多[我的消息]</a-button><!--@click="toMyAnnouncement()"-->
              </div>
            </a-list>
          </a-tab-pane>
          <a-tab-pane :tab="msg2Title" key="2">
            <a-list>
              <a-list-item :key="index" v-for="(record, index) in announcement2">
                <div style="margin-left: 5%;width: 80%">
                  <p><a @click="showAnnouncement(record)">标题：{{ record.title }}</a></p>
                  <p style="color: rgba(0,0,0,.45);margin-bottom: 0px">{{ record.createTime }} 发布</p>
                </div>
                <div style="text-align: right">
                  <a-tag @click="showAnnouncement(record)" v-if="record.priority === 'L'" color="blue">一般消息</a-tag>
                  <a-tag @click="showAnnouncement(record)" v-if="record.priority === 'M'" color="orange">重要消息</a-tag>
                  <a-tag @click="showAnnouncement(record)" v-if="record.priority === 'H'" color="red">紧急消息</a-tag>
                </div>
              </a-list-item>
              <div style="margin-top: 5px;text-align: center">
                <a-button type="dashed" block @click="clickInfo(record)">更多[我的消息]</a-button>
              </div>
            </a-list>
          </a-tab-pane>
        </a-tabs>
      </a-spin>
    </template>
    <span @click="fetchNotice" class="header-notice">
      <a-badge :count="msgTotal">
        <a-icon style="font-size: 16px; padding: 4px" type="bell" />
      </a-badge>
    </span>
    <show-announcement ref="ShowAnnouncement" @ok="modalFormOk"></show-announcement>
  </a-popover>

</template>

<script>
  import ShowAnnouncement from '../application/notes/ShowAnnouncement'
  import {mapState} from 'vuex'

  export default {
    name:'HeaderNotice',
    components: {
      ShowAnnouncement,
    },
    data () {
      return {
        loadding: false,
        hovered: false,
        announcement1:[],
        announcement2:[],
        msg1Count:"0",
        msg2Count:"0",
        msg1Title:"通知",
        msg2Title:"系统通知",
      }
    },
    computed:{
      msgTotal () {
        return parseInt(this.msg1Count)+parseInt(this.msg2Count);
      },
      ...mapState({
        user: state => state.account.user
      }),
    },
    created() {
      this.loadNoticeData();
      this.loadSysNoticeData();
      this.timer();
    },
    methods: {
      clickInfo(record){
        this.hovered = false;
        if(record){
          this.$put(`notice/check/`,{
            noticeIds: record.noticeId
          }).then((r)=>{
            this.loadNoticeData();
            this.loadSysNoticeData()
            this.$router.push({path: '/notes'});
          }).catch((e)=>{
            this.loadNoticeData();
            this.loadSysNoticeData()
          })
        }else{
          this.$router.push({path: '/notes'});
        }

      },
      timer() {
        return setInterval(()=>{
          this.loadNoticeData()
          this.loadSysNoticeData()
        },300000)
      },
      loadNoticeData (){
        this.loadding = true
        this.$get(`notice/short/${this.user.userId}`,{
        }).then((r)=>{
          let data = r.data.data
          this.announcement1 = data.rows
          this.msg1Count = data.total
          this.msg1Title = "通知(" + this.msg1Count + ")";
          this.loadding=false
        }).catch((e)=>{
          this.loadding=false
        })
      },
      loadSysNoticeData (){
        this.loadding = true
        this.$get(`notice/short/sys`,{
        }).then((r)=>{
          let data = r.data.data
          this.announcement2 = data.rows
          this.msg2Count = data.total;
          this.msg2Title = "系统消息(" + this.msg2Count + ")";
          this.loadding=false
        }).catch((e)=>{
          this.loadding=false
        })
      },
      fetchNotice () {
        if (this.loadding) {
          this.loadding = false
          return
        }
        this.loadding = true
        setTimeout(() => {
          this.loadding = false
        }, 200)
      },
      showAnnouncement(record){
        this.hovered = false;
        this.$refs.ShowAnnouncement.detail(record);
        this.$put(`notice/check/`,{
          noticeIds: record.noticeId
        }).then((r)=>{})
      },
      toMyAnnouncement(){
        this.$router.push({
          path: '/application/notes',
          name: 'MyNotes'
        });
      },
      modalFormOk(){
      },
      handleHoverChange (visible) {
        this.hovered = visible;
      }

    }
  }
</script>

<style lang="css">
  .header-notice-wrapper {
    top: 50px !important;
  }
</style>
<style lang="less" scoped>
  .header-notice{
    display: inline-block;
    transition: all 0.3s;

    span {
      vertical-align: initial;
    }
  }
</style>
