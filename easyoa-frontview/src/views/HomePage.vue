<template>
  <div :class="[multipage === true ? 'multi-page':'single-page', 'not-menu-page', 'home-page']">
    <a-row :gutter="8" class="head-info">
      <a-card class="head-info-card">
        <a-col :span="12">
          <div class="head-info-avatar">
            <img alt="头像" :src="avatar" />
          </div>
          <div class="head-info-count">
            <div class="head-info-welcome">{{welcomeMessage}}</div>
            <div class="head-info-desc">
              <p>
                {{user.deptName ? user.deptName : '暂无部门'}} | {{user.roleName ? user.roleName : '暂无角色'}}|{{user.position
                ? user.position:'暂无职位'}}
              </p>
            </div>
            <div
              class="head-info-time"
            >上次登录时间：{{user.lastLoginTime ? user.lastLoginTime : '第一次访问系统'}}</div>
          </div>
        </a-col>
        <a-col :span="12">
          <div>
            <a-row class="more-info">
              <a-col :span="4"></a-col>
              <a-col :span="4">
                <head-info title="周申请数" :content="weeklyApply" :center="false" :bordered="false" />
              </a-col>
              <a-col :span="4">
                <head-info title="月申请数" :content="monthlyApply" :center="false" :bordered="false" />
              </a-col>
              <a-col :span="12">
                <head-info title="剩余休假数" :content="restVacation" :center="false" />
              </a-col>
            </a-row>
          </div>
        </a-col>
      </a-card>
    </a-row>
    <a-row :gutter="16" class="count-info">
      <a-col :span="16" class="project-wrapper">
        <a-card class="project-card">
          <div slot="title">
            <a-icon type="highlight" />待办事项
          </div>
          <div :style="{ border: '1px', borderRadius: '4px',height:'360px'}">
            <div style="margin:10px">
              <a-tabs default-active-key="1" @change="callback">
                <a-tab-pane key="1" tab="待办申请">
                  <a-table
                    :columns="applyColumns"
                    :data-source="applyData"
                    :size="small"
                    :pagination="false"
                    class="hometable"
                    :scroll="{ x: '100%', y: 250 }"
                  >
                    <template slot="action" slot-scope="text, record">
                      <a @click="view(record)">详情</a>
                    </template>
                  </a-table>
                </a-tab-pane>
                <a-tab-pane v-if="tabProcessVisual" key="2" tab="待办处理" force-render>
                  <a-table
                    :columns="processColumns"
                    :data-source="applyProcessData"
                    :size="small"
                    :pagination="false"
                    class="hometable"
                    :scroll="{ x: '100%', y: 250 }"
                  >
                    <template slot="action" slot-scope="text, record">
                      <a @click="myProcess(record)">详情</a>
                    </template>
                  </a-table>
                </a-tab-pane>
              </a-tabs>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="8" class="project-wrapper">
        <a-card class="project-card">
          <div slot="title">休假日历</div>
          <div :style="{ border: '1px', borderRadius: '4px',height:'360px' }">
            <a-calendar :fullscreen="false" @panelChange="onPanelChange">
              <ul slot="dateCellRender" slot-scope="value" class="events">
                <li v-for="item in getListData(value)" :key="item.content">
                  <a-badge :status="item.type" :text="item.content" />
                </li>
              </ul>
              <template slot="monthCellRender" slot-scope="value">
                <div v-if="getMonthData(value)" class="notes-month">
                  <section>{{ getMonthData(value) }}</section>
                </div>
              </template>
            </a-calendar>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="16" class="count-info">
      <a-col :span="16" class="project-wrapper">
        <a-card class="project-card">
          <div slot="title">
            <a-icon type="highlight" />流程提醒
          </div>
          <div :style="{ border: '1px', borderRadius: '4px',height:'250px'}">
            <div style="margin:10px">
              <a-table
                :columns="msgColumns"
                :data-source="messageData"
                :size="small"
                :pagination="false"
                class="hometable"
                :scroll="{ x: '100%',y:200 }"
              ></a-table>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="8" class="project-wrapper">
        <a-card class="project-card">
          <div slot="title">传送门</div>
          <div :style="{ border: '1px', borderRadius: '4px',height:'250px'}">
            <a-row type="flex" justify="center" :style="{paddingTop:'20px'}">
              <a-col :span="4" class="project-wrapper">
                <a-button type="primary" icon="plus" size="large" @click="quickApply()">快速申请</a-button>
              </a-col>
            </a-row>
            <a-row type="flex" justify="center" :style="{paddingTop:'20px'}">
              <a-col :span="4" class="project-wrapper">
                <a-button type="primary" icon="bar-chart" size="large" @click="myData()">我的数据</a-button>
              </a-col>
            </a-row>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import HeadInfo from "@/views/common/HeadInfo";
import { mapState } from "vuex";
import moment from "moment";

moment.locale("zh-cn");

const applyColumns = [
  {
    title: "序号",
    width: 80,
    dataIndex: "applicationId",
    key: "key",
    fixed: "left",
    align: "center"
  },
  {
    title: "申请类型",
    width: 120,
    dataIndex: "leaveType",
    key: "leaveType",
    fixed: "left",
    align: "center",
    ellipsis: true
  },
  {
    title: "天数",
    width: 80,
    dataIndex: "days",
    key: "days",
    align: "center",
    ellipsis: true
  },
  {
    title: "状态",
    width: 100,
    dataIndex: "status",
    key: "status",
    align: "center",
    ellipsis: true
  },
  {
    title: "进度",
    width: 100,
    dataIndex: "stage",
    key: "stage",
    align: "center",
    ellipsis: true
  },
  {
    title: "创建时间",
    width: 200,
    dataIndex: "createTime",
    key: "createTime",
    align: "center",
    ellipsis: true
  },
  {
    title: "开始时间",
    width: 200,
    dataIndex: "startTime",
    key: "startTime",
    align: "center",
    ellipsis: true
  },
  {
    title: "结束时间",
    width: 200,
    dataIndex: "endTime",
    key: "endTime",
    align: "center",
    ellipsis: true
  },
  {
    title: "操作",
    key: "operation",
    fixed: "right",
    width: 100,
    scopedSlots: { customRender: "action" }
  }
];

const processColumns = [
  {
    title: "序号",
    width: 80,
    dataIndex: "applicationId",
    key: "key",
    fixed: "left",
    align: "center"
  },
  {
    title: "申请人",
    width: 200,
    dataIndex: "userName",
    key: "userName",
    fixed: "left",
    align: "center",
    ellipsis: true
  },
  {
    title: "申请类型",
    width: 100,
    dataIndex: "leaveType",
    key: "leaveType",
    align: "center",
    ellipsis: true
  },
  {
    title: "天数",
    width: 80,
    dataIndex: "days",
    key: "days",
    align: "center",
    ellipsis: true
  },
  {
    title: "进度",
    width: 100,
    dataIndex: "stage",
    key: "stage",
    align: "center",
    ellipsis: true
  },
  {
    title: "创建时间",
    width: 200,
    dataIndex: "createTime",
    key: "createTime",
    align: "center",
    ellipsis: true
  },
  {
    title: "开始时间",
    width: 200,
    dataIndex: "startTime",
    key: "startTime",
    align: "center",
    ellipsis: true
  },
  {
    title: "结束时间",
    width: 200,
    dataIndex: "endTime",
    key: "endTime",
    align: "center",
    ellipsis: true
  },
  {
    title: "操作",
    key: "operation",
    fixed: "right",
    width: 100,
    scopedSlots: { customRender: "action" }
  }
];

const msgColumns = [
  {
    title: "序号",
    width: 80,
    dataIndex: "noticeId",
    key: "key",
    fixed: "left"
  },
  {
    title: "消息",
    width: 600,
    dataIndex: "msgContent",
    key: "message"
  },
  {
    title: "创建日期",
    width: 150,
    dataIndex: "createTime",
    key: "createTime",
    ellipsis: true
  }
];

export default {
  name: "HomePage",
  components: { HeadInfo },
  data() {
    return {
      applyColumns,
      msgColumns,
      processColumns,
      tabProcessVisual: false,
      listData: [],
      applyData: [],
      applyProcessData: [],
      messageData: [],
      small: "small",
      series: [],

      chartOptions: {
        chart: {
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: "35%"
          }
        },
        dataLabels: {
          enabled: false
        },
        rows: [],
        stroke: {
          show: true,
          width: 2,
          colors: ["transparent"]
        },
        xaxis: {
          categories: []
        },
        fill: {
          opacity: 1
        }
      },
      weeklyApply: "",
      monthlyApply: "",
      restVacation: "",
      userRole: "",
      userDept: "",
      lastLoginTime: "",
      welcomeMessage: ""
    };
  },
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      user: state => state.account.user
    }),
    avatar() {
      return `static/avatar/${this.user.avatar}`;
    }
  },
  methods: {
    callback(){

    },
    onPanelChange(value, mode) {
      if ("month" === mode) {
        let day = value.year() + "-" + (value.month() + 1) + "-" + value.date();
        this.fetchDefaultCalendarData(day);
      }
    },
    welcome() {
      const date = new Date();
      const hour = date.getHours();
      let time =
        hour < 6
          ? "早上好"
          : hour <= 11
          ? "上午好"
          : hour <= 13
          ? "中午好"
          : hour <= 18
          ? "下午好"
          : "晚上好";
      let welcomeArr = [
        "几天没见又更好看了呢😍",
        "今天又写了几个Bug🐞呢",
        "今天吃了什么好吃的呢",
        "今天您微笑了吗😊",
        "今天帮助别人解决问题了吗",
        "准备吃些什么呢"
      ];
      let index = Math.floor(Math.random() * welcomeArr.length);
      return `${time}，${this.user.nickName}，${welcomeArr[index]}`;
    },
    getListData(value) {
      let listData = [];

      let day = value.year() + "-" + (value.month() + 1) + "-" + value.date();
      for (let i = 0; i < this.listData.length; i++) {
        if (day === this.listData[i].day) {
          switch (this.listData[i].hit) {
            case 1:
              listData = [{ type: "success", content: "" }];
              break;
            case 2:
              listData = [{ type: "warning", content: "" }];
              break;
            default:
          }
        }
      }
      return listData || [];
    },
    getMonthData(value) {},
    fetchUserApplyInfo() {
      this.$get(`index/applyinfo/${this.user.userId}`).then(r => {
        let data = r.data;
        this.applyData = data.data;
      });
    },
    fetchUserApplyProcessInfo() {
      this.$get(`index/applyprocessinfo/${this.user.userId}`).then(r => {
        let data = r.data;
        this.applyProcessData = data.data;
        if (this.applyProcessData.length !== 0) {
          this.tabProcessVisual = true;
        }
      });
    },
    fetchUserApplyMessage() {
      this.$get(`index/applymsg/${this.user.userId}`).then(r => {
        let data = r.data;
        this.messageData = data.data;
      });
    },
    fetchDefaultCalendarData(day) {
      let params = {};
      if (day) {
        params.day = day;
      } else {
        params.day = moment().format("YYYY-MM-DD");
      }
      this.$get(`index/applydays/default/${this.user.userId}`, {
        ...params
      }).then(r => {
        this.listData = r.data.data;
      });
    },
    view(record) {
      if (record) {
        this.$router.push({ path: "/application/submission" });
      }
    },
    myProcess(record) {
      if (record) {
        this.$router.push({ path: "/process/todo" });
      }
    },
    quickApply() {
      this.$router.push({ path: "/application/submission" });
    },
    myData() {
      this.$router.push({ path: "/application/vacation" });
    }
  },
  mounted() {
    this.welcomeMessage = this.welcome();
    this.$get(`index/${this.user.userId}`)
      .then(r => {
        this.rows = r.data.data.rows;
        this.weeklyApply = r.data.data.week;
        this.monthlyApply = r.data.data.month;
        this.restVacation = r.data.data.rest;

        let table = document.getElementById("ActiveTable");
        for (let i = 0; i < this.rows.length; i++) {
          let tr1 = document.createElement("tr");
          let td1 = document.createElement("td");
          td1.style.border = "1px solid #f1f1f1";
          td1.style.width = "50%";
          td1.style.padding = ".6rem";
          let div1 = document.createElement("div");
          div1.style.margin = "0 1rem 0 1rem";
          let div11 = document.createElement("div");
          div11.innerHTML = this.rows[i].name;
          div11.style.fontSize = ".9rem";
          div11.style.marginTop = "-2px";
          div11.style.fontWeight = "500";
          let div12 = document.createElement("div");
          div12.style.color = "rgba(0, 0, 0, 0.45)";
          let p1 = document.createElement("p");
          p1.innerHTML = this.rows[i].content;
          p1.style.marginBottom = "0px";
          p1.style.fontSize = ".6rem";
          p1.style.whiteSpace = "normal";
          div12.appendChild(p1);
          div1.appendChild(div11);
          div1.appendChild(div12);
          td1.appendChild(div1);
          tr1.appendChild(td1);

          table.appendChild(tr1);
        }
      })
      .catch(r => {
        console.error(r);
        this.$message.warning("首页信息正在加载");
      });
    this.fetchUserApplyInfo();
    this.fetchUserApplyProcessInfo();
    this.fetchUserApplyMessage();
    this.fetchDefaultCalendarData();

    /**
       *  <tr>
       <td>
       <div class="project-avatar-wrapper">
       <a-avatar class="project-avatar">{{projects[0].avatar}}</a-avatar>
       </div>
       <div class="project-detail">
       <div class="project-name">
       {{projects[0].name}}
       </div>
       <div class="project-desc">
       <p>{{projects[0].des}}</p>
       </div>
       </div>
       </td>
       <td>
       <div class="project-avatar-wrapper">
       <a-avatar class="project-avatar">{{projects[1].avatar}}</a-avatar>
       </div>
       <div class="project-detail">
       <div class="project-name">
       {{projects[1].name}}
       </div>
       <div class="project-desc">
       <p>{{projects[1].des}}</p>
       </div>
       </div>
       </td>
       </tr>
       <tr>


       var tr=document.createElement('tr');
       var tdename=document.createElement('td')
       var tdsalary=document.createElement('td')
       * @type {HTMLElement}
       */
  }
};
</script>
<style lang="less">
.home-page {
  background: #f0f2f5;
  .head-info {
    margin-bottom: 0.5rem;

    .head-info-card {
      padding: 0.5rem;
      border-color: #f1f1f1;

      .head-info-avatar {
        display: inline-block;
        float: left;
        margin-right: 1rem;

        img {
          width: 5rem;
          border-radius: 2px;
        }
      }

      .head-info-count {
        display: inline-block;
        float: left;

        .head-info-welcome {
          font-size: 1.05rem;
          margin-bottom: 0.1rem;
        }

        .head-info-desc {
          color: rgba(0, 0, 0, 0.45);
          font-size: 0.8rem;
          padding: 0.2rem 0;

          p {
            margin-bottom: 0;
          }
        }

        .head-info-time {
          color: rgba(0, 0, 0, 0.45);
          font-size: 0.8rem;
          padding: 0.2rem 0;
        }
      }
    }
  }

  .count-info {
    .visit-count-wrapper {
      padding-left: 0 !important;

      .visit-count {
        padding: 0.5rem;
        border-color: #f1f1f1;

        .ant-card-body {
          padding: 0.5rem 1rem !important;
        }
      }
    }

    .project-wrapper {
      padding-right: 0 !important;

      .project-card {
        border: none !important;

        .ant-card-head {
          border-left: 1px solid #f1f1f1 !important;
          border-top: 1px solid #f1f1f1 !important;
          border-right: 1px solid #f1f1f1 !important;
        }

        .ant-card-body {
          padding: 0 !important;

          table {
            width: 100%;

            td {
              width: 50%;
              border: 1px solid #f1f1f1;
              padding: 0.6rem;

              .project-avatar-wrapper {
                display: inline-block;
                float: left;
                margin-right: 0.7rem;

                .project-avatar {
                  color: #42b983;
                  background-color: #d6f8b8;
                }
              }
            }
          }
        }

        .project-detail {
          display: inline-block;
          float: left;
          text-align: left;
          width: 78%;

          .project-name {
            font-size: 0.9rem;
            margin-top: -2px;
            font-weight: 600;
          }

          .project-desc {
            color: rgba(0, 0, 0, 0.45);

            p {
              margin-bottom: 0;
              font-size: 0.6rem;
              white-space: normal;
            }
          }
        }
        .events {
          list-style: none;
          margin: 0;
          padding: 0;
        }
        .events .ant-badge-status {
          overflow: hidden;
          white-space: nowrap;
          width: 100%;
          text-overflow: ellipsis;
          font-size: 12px;
        }
        .notes-month {
          text-align: center;
          font-size: 28px;
        }
        .notes-month section {
          font-size: 28px;
        }
        .hometable {
          text-overflow: ellipsis;
          word-break: break-all;
          white-space: nowrap;
        }
      }
    }
  }
}
</style>
