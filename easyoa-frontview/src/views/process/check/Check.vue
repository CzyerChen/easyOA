<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row>
          <div :class="advanced ? null: 'fold'">
            <a-col :md="12" :sm="24">
              <a-form-item
                label="请假类别"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-select
                  mode="default"
                  notFoundContent="暂无类别"
                  v-model="queryParams.leaveType"
                  style="width: 100%">
                  <a-select-option v-for="a in applyTypes" :key="a">{{a}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item
                label="创建时间"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <range-date @change="handleDateChange" ref="createTime"></range-date>
              </a-form-item>
            </a-col>
              <a-col :md="12" :sm="24">
              <a-form-item
                label="休假时间"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <range-date @change="handleApplyDateChange" ref="startTime"></range-date>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item
                label="员工工号"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
              <a-input v-model="queryParams.userCode"/>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item
                label="申请状态"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-select
                  mode="default"
                  notFoundContent="暂无状态"
                  v-model="queryParams.status"
                  style="width: 100%">
                  <a-select-option v-for="a in statusEnum" :key="a.code">{{a.name}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>

    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="exportExcel" v-hasPermission="'check:export'">导出数据</a-button>
      </div>
    </div>
    <div>
      <a-table ref="TableInfo"
               :columns="columns"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               @change="handleTableChange"
               :scroll="{ x: 1500 }">
        <template slot="icon" slot-scope="text, record">
          <a-icon :type="text"/>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon v-hasPermission="'application:view'" type="eye" theme="twoTone" twoToneColor="#42b983"
                  @click="view(record)" title="查看申请流程详情"></a-icon>
          <!-- <a-icon v-hasPermission="'application:update'" type="setting" theme="twoTone" twoToneColor="#4a9ff5"
                  @click="edit(record)" title="修改申请流程详情"></a-icon> -->
          <a-icon v-hasPermission="'application:finish'"
                  v-if="record.status === 'RUNNING' && record.stage.indexOf('人力资源审批') ===-1"
                  type="check-circle" theme="twoTone" twoToneColor="#faad14"
                  @click="finish(record)" title="快速通过申请"></a-icon>
          <a-badge v-hasNoPermission="'application:view'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>

    <application-info
      @close="handleApplicationInfoClose"
      :applicationInfoData="applicationInfo.data"
      :applicationInfoVisiable="applicationInfo.visible">
    </application-info>

    <application-edit
      ref="applicationEdit"
      @close="handleApplicationEditClose"
      @success="handleApplicationEditSuccess"
      :applicationEditData="applicationEditData"
      :applicationEditVisiable="applicationEditVisible">
    </application-edit>

  </a-card>
</template>

<script>
  import ApplicationInfo from '../../application/submission/ApplicationInfo'
  import ApplicationEdit from '../../application/submission/ApplicationEdit'
  import RangeDate from '@/components/datetime/RangeDate'

  export default {
    name: 'Check',
    components: {
      ApplicationInfo,
      ApplicationEdit,
      RangeDate
    },
    data() {
      return {
        applicationInfo: {
          data: {},
          visible: false
        },
        applicationEditData:{},
        applicationEditVisible:false,
        advanced: false,
        queryParams: {
          status:'none'
        },
        applyTypes: [],
        statusEnum: [
          {
            name:'不限',
            code:'none'
          },{
            name:'已结束',
            code:'finished'
          },{
            name:'运行中',
            code:'running'
          }],
        filteredInfo: null,
        sortedInfo: null,
        paginationInfo: null,
        dataSource: [],
        loading: false,
        pagination: {
          pageSizeOptions: ['10', '20', '30', '40', '100'],
          defaultCurrent: 1,
          defaultPageSize: 10,
          showQuickJumper: true,
          showSizeChanger: true,
          showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
        }
      }
    },
    methods: {
      toggleAdvanced() {
        this.advanced = !this.advanced;
        if (!this.advanced) {
          this.queryParams.createTimeFrom = ''
          this.queryParams.createTimeTo = ''
          this.queryParams.applyTimeFrom =''
          this.queryParams.applyTimeTo =''
        }
      },
      handleDateChange(value) {
        if (value) {
          this.queryParams.createTimeFrom = value[0]
          this.queryParams.createTimeTo = value[1]
        }
      },
      handleApplyDateChange(value) {
        if (value) {
          this.queryParams.applyTimeFrom = value[0]
          this.queryParams.applyTimeTo = value[1]
        }
      },
      view(record) {
        this.applicationInfo.visible = true
        this.applicationInfo.data = record
      },
      edit(record) {
        this.applicationEditVisible = true
        this.applicationEditData = record
        this.$refs.applicationEdit.setFormValues(record)
      },
      finish(record){
        let that = this
        let params = this.queryParams
        this.$confirm({
          title: '确定通过所选的申请？',
          content: '当您点击确认后，所选申请将被流转',
          centered: false,
          onOk() {
            let processIds = []
            processIds.push(record.applicationId)
            that.$delete('flow/operate/transfer/' + processIds.join(','))
              .then(() => {
              that.$message.success('申请已被流转')
              that.fetch({...params})
            })
          },
          onCancel() {
        }
        })
      },
      handleApplicationInfoClose() {
        this.applicationInfo.visible = false
      },
      handleApplicationEditClose() {
        this.applicationEditVisible = false
      },
      handleApplicationEditSuccess() {
        this.applicationEditVisible = false;
        this.$message.success('修改申请成功')
        this.search()
      },
      handleTableChange(pagination, filters, sorter) {
        // 将这三个参数赋值给Vue data，用于后续使用
        this.paginationInfo = pagination
        this.filteredInfo = filters
        this.sortedInfo = sorter

        this.applicationInfo.visiable = false
        this.fetch({
          ...this.queryParams,
          ...filters
        })
      },
      search() {
        let {sortedInfo, filteredInfo} = this
        let sortField, sortOrder;
        // 获取当前列的排序和列的过滤规则
        if (sortedInfo) {
          sortField = sortedInfo.field
          sortOrder = sortedInfo.order
        }
        this.fetch({
          ...this.queryParams,
          ...filteredInfo
        })
      },
      reset() {
        // 重置分页
        this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
        if (this.paginationInfo) {
          this.paginationInfo.current = this.pagination.defaultCurrent
          this.paginationInfo.pageSize = this.pagination.defaultPageSize
        }
        // 重置列过滤器规则
        this.filteredInfo = null
        // 重置列排序规则
        this.sortedInfo = null
        // 重置查询参数
        this.queryParams = {}
        // 清空时间选择
        if (this.advanced) {
          this.$refs.createTime.reset()
        }
        this.fetch()
      },
      fetch(params = {}) {
        // 显示loading
        this.loading = true
        if (this.paginationInfo) {
          // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
          this.$refs.TableInfo.pagination.current = this.paginationInfo.current
          this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
          params.pageSize = this.paginationInfo.pageSize
          params.pageNum = this.paginationInfo.current
        } else {
          // 如果分页信息为空，则设置为默认值
          params.pageSize = this.pagination.defaultPageSize
          params.pageNum = this.pagination.defaultCurrent
        }
        this.$get(`apply`, {
          ...params
        }).then((r) => {
          let data = r.data
          const pagination = {...this.pagination}
          pagination.total = data.total
          this.dataSource = data.rows
          this.pagination = pagination
          // 数据加载完毕，关闭loading
          this.loading = false
        })
      },
      exportExcel() {
        let {sortedInfo, filteredInfo} = this
        let sortField, sortOrder
        // 获取当前列的排序和列的过滤规则
        if (sortedInfo) {
          sortField = sortedInfo.field
          sortOrder = sortedInfo.order
        }
        this.$export('apply/excel', {
          sortField: sortField,
          sortOrder: sortOrder,
          ...this.queryParams,
          ...filteredInfo
        })
      }
    },
    mounted() {
      this.$get('apply/commontypes').then((r) => {
        this.applyTypes = r.data
      }).catch((e) => {
        this.$message("休假类型获取失败")
      })
      this.reset();
      this.fetch();
    },
    computed: {
      columns() {
        let {sortedInfo, filteredInfo} = this;
        sortedInfo = sortedInfo || {};
        filteredInfo = filteredInfo || {};
        return [{
          title: '序号',
          dataIndex: 'applicationId',
          align: 'center'
        }, {
          title: '申请人',
          dataIndex: 'userName',
          align: 'center'
        }, {
          title: '工号',
          dataIndex: 'userCode',
          align: 'center'
        }, {
          title: '所属部门',
          dataIndex: 'deptName',
          align: 'center'
        }, {
          title: '申请类型',
          dataIndex: 'leaveType',
          align: 'center'
        }, {
          title: '休假天数',
          dataIndex: 'days',
          align: 'center'
        },{
          title: '剩余年假抵消',
          dataIndex: 'previousDays',
          align: 'center'
        }, {
          title: '今年年假抵消',
          dataIndex: 'currentDays',
          align: 'center'
        },  {
          title: '请假开始时间',
          dataIndex: 'startTime',
          align: 'center'
        }, {
          title: '请假结束时间',
          dataIndex: 'endTime',
          align: 'center'
        }, {
          title: '状态',
          dataIndex: 'status',
          align: 'center'
        }, {
          title: '创建时间',
          dataIndex: 'createTime',
          align: 'center'
        }, {
          title: '操作',
          dataIndex: 'operation',
          width: 100,
          scopedSlots: {customRender: 'operation'},
          fixed: 'right'
        }]
      },
    }
  }
</script>

<style lang="less" scoped>
  @import "../../../../static/less/Common";
</style>

