<template>
  <a-card :bordered="false" class="card-area">
    <div>
      <div class="operator">
        <a-tooltip>
    <template slot="title">
      流转到下一个审批人员
    </template>
    <a-button v-hasPermission="'process:pass'" @click="doTransfer">同意</a-button>
  </a-tooltip>
       
        <a-tooltip>
    <template slot="title">
      流程正常结束
    </template>
          <a-button v-hasPermission="'process:over'" @click="doOver" type="danger">结束</a-button>
  </a-tooltip>

   <a-tooltip>
    <template slot="title">
      重新分配同级受理人
    </template>
          <a-button v-hasPermission="'process:redo'" @click="doRedo" type="danger">重做</a-button>
  </a-tooltip>

   <a-tooltip>
    <template slot="title">
      拒绝该申请
    </template>
          <a-button v-hasPermission="'process:refuse'" @click="doTerminate" type="danger">拒绝</a-button>
  </a-tooltip>

   <a-tooltip>
    <template slot="title">
      撤销当前申请
    </template>
          <a-button v-hasPermission="'process:cancel'" @click="doCancel" type="danger">撤销</a-button>
  </a-tooltip>

      </div>
      <a-table
        ref="TableInfo"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        :scroll="{ x: 1500 }"
      >
        <template slot="icon" slot-scope="text, record">
          <a-icon :type="text" />
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon
            v-hasPermission="'application:view'"
            type="eye"
            theme="twoTone"
            twoToneColor="#42b983"
            @click="view(record)"
            title="查看申请流程详情"
          ></a-icon>
          <a-icon
            v-hasPermission="'application:view'"
            type="file"
            theme="twoTone"
            twoToneColor="#4a9ff5"
            v-if="record.resources !== null"
            @click="download(record)"
            title="下载附件"
          ></a-icon>
          <a-badge v-hasNoPermission="'application:view'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>

    <application-info
      @close="handleApplicationInfoClose"
      :applicationInfoData="applicationInfo.data"
      :applicationInfoVisiable="applicationInfo.visible"
    ></application-info>
  </a-card>
</template>

<script>
import { mapState, mapMutations } from "vuex";
import ApplicationInfo from "../../application/submission/ApplicationInfo";

export default {
  name: "Process",
  components: {
    ApplicationInfo
  },
  data() {
    return {
      applicationInfo: {
        data: {},
        visible: false
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ["10", "20", "30", "40", "100"],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) =>
          `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      }
    };
  },
  methods: {
    ...mapMutations({
      setUser: "account/setUser"
    }),
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
    },
    doTransfer() {
      if (!this.selectedRowKeys.length) {
        this.$message.warning("请选择需要流转的记录");
        return;
      }
      let that = this;
      this.$confirm({
        title: "确定要流转所选的申请？",
        content: "当您点击确认后，所选申请将被流转",
        centered: false,
        onOk() {
          let processIds = [];
          for (let key of that.selectedRowKeys) {
            processIds.push(that.dataSource[key].applicationId);
          }
          that
            .$delete("flow/operate/transfer/" + processIds.join(","))
            .then(() => {
              setTimeout(() => {
                that.$message.success("申请已被流转");
                that.selectedRowKeys = [];
                that.fetch();
              }, 500);
            });
        },
        onCancel() {
          that.selectedRowKeys = [];
        }
      });
    },
    doOver() {
      if (!this.selectedRowKeys.length) {
        this.$message.warning("请选择需要结束的记录");
        return;
      }
      let that = this;
      this.$confirm({
        title: "确定要结束所选的申请？",
        content: "当您点击确认后，所选申请将结束",
        centered: false,
        onOk() {
          let processIds = [];
          for (let key of that.selectedRowKeys) {
            processIds.push(that.dataSource[key].applicationId);
          }
          that.$delete("flow/operate/over/" + processIds.join(",")).then(() => {
            setTimeout(() => {
              that.$message.success("申请已结束");
              that.selectedRowKeys = [];
              that.fetch();
            }, 500);
          });
        },
        onCancel() {
          that.selectedRowKeys = [];
        }
      });
    },
    doRedo() {
      if (!this.selectedRowKeys.length) {
        this.$message.warning("请选择需要重做的记录");
        return;
      }
      let that = this;
      this.$confirm({
        title: "确定要重做所选的申请？",
        content: "当您点击确认后，所选申请将重做",
        centered: false,
        onOk() {
          let processIds = [];
          for (let key of that.selectedRowKeys) {
            processIds.push(that.dataSource[key].applicationId);
          }
          that.$delete("flow/operate/redo/" + processIds.join(",")).then(() => {
            setTimeout(() => {
              that.$message.success("申请已重新分配");
              that.selectedRowKeys = [];
              that.fetch();
            }, 500);
          });
        },
        onCancel() {
          that.selectedRowKeys = [];
        }
      });
    },
    doTerminate() {
      if (!this.selectedRowKeys.length) {
        this.$message.warning("请选择需要拒绝的记录");
        return;
      }
      let that = this;
      this.$confirm({
        title: "确定要拒绝所选的申请？",
        content: "当您点击确认后，所选申请将被拒绝",
        centered: false,
        onOk() {
          let processIds = [];
          for (let key of that.selectedRowKeys) {
            processIds.push(that.dataSource[key].applicationId);
          }
          that
            .$delete("flow/operate/terminate/" + processIds.join(","))
            .then(() => {
              setTimeout(() => {
                that.$message.success("申请已被拒绝");
                that.selectedRowKeys = [];
                that.fetch();
              }, 500);
            });
        },
        onCancel() {
          that.selectedRowKeys = [];
        }
      });
    },
    doCancel() {
      if (!this.selectedRowKeys.length) {
        this.$message.warning("请选择需要撤销的记录");
        return;
      }
      let that = this;
      this.$confirm({
        title: "确定要撤销所选的申请？",
        content: "当您点击确认后，所选申请将被撤销",
        centered: false,
        onOk() {
          let processIds = [];
          for (let key of that.selectedRowKeys) {
            processIds.push(that.dataSource[key].applicationId);
          }
          that
            .$delete("flow/operate/cancel/" + processIds.join(","))
            .then(() => {
              setTimeout(() => {
                that.$message.success("申请已被撤销");
                that.selectedRowKeys = [];
                that.fetch();
              }, 500);
            });
        },
        onCancel() {
          that.selectedRowKeys = [];
        }
      });
    },
    view(record) {
      this.applicationInfo.visible = true;
      this.applicationInfo.data = record;
    },
    download(record) {
      console.log(record.resources);
      this.$download(
        `apply/file/download`,
        {
          fileId: record.resources
        },
        record.fileName
      )
        .then(r => {})
        .catch(r => {
          this.$message(r.responseMessage);
        });
    },
    handleApplicationInfoClose() {
      this.applicationInfo.visible = false;
    },
    handleTableChange(pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination;
      this.filteredInfo = filters;
      this.sortedInfo = sorter;

      this.userInfo.visiable = false;
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      });
    },
    fetch(params = {}) {
      // 显示loading
      this.loading = true;
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current;
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize;
        params.pageSize = this.paginationInfo.pageSize;
        params.pageNum = this.paginationInfo.current;
      } else {
        // 如果分页信息为空，则设置为默认值
        params.pageSize = this.pagination.defaultPageSize;
        params.pageNum = this.pagination.defaultCurrent;
      }
      this.$get(`apply/assignee/todo/${this.currentUser.userId}`, {
        ...params
      }).then(r => {
        let data = r.data;
        const pagination = { ...this.pagination };
        pagination.total = data.total;
        this.dataSource = data.rows;
        this.pagination = pagination;
        // 数据加载完毕，关闭loading
        this.loading = false;
      });
    }
  },
  mounted() {
    this.fetch();
  },
  computed: {
    columns() {
      let { sortedInfo, filteredInfo } = this;
      sortedInfo = sortedInfo || {};
      filteredInfo = filteredInfo || {};
      return [
        {
          title: "序号",
          dataIndex: "applicationId",
          align: "center"
        },
        {
          title: "申请人",
          dataIndex: "userName",
          align: "center"
        },
        {
          title: "所属部门",
          dataIndex: "deptName",
          align: "center"
        },
        {
          title: "职位",
          dataIndex: "position",
          align: "center"
        },
        {
          title: "申请类型",
          dataIndex: "leaveType",
          align: "center"
        },
        {
          title: "天数",
          dataIndex: "days",
          align: "center"
        },
        {
          title: "请假开始时间",
          dataIndex: "startTime",
          align: "center"
        },
        {
          title: "请假结束时间",
          dataIndex: "endTime",
          align: "center"
        },
        {
          title: "状态",
          dataIndex: "status",
          align: "center"
        },
        {
          title: "创建时间",
          dataIndex: "createTime",
          align: "center"
        },
        {
          title: "操作",
          dataIndex: "operation",
          width: 150,
          scopedSlots: { customRender: "operation" },
          fixed: "right"
        }
      ];
    },
    ...mapState({
      currentUser: state => state.account.user
    })
  }
};
</script>

<style lang="less" scoped>
</style>
