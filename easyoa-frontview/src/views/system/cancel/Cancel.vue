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
                  style="width: 100%"
                  required>
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
        <a-button type="primary" ghost @click="batchOperate" v-hasPermission="'application:cancel'">销假</a-button>
      </div>
    </div>
    <div>
      <a-table ref="TableInfo"
               :columns="columns"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               @change="handleTableChange"
               :scroll="{ x: 1500 }">
        <template slot="icon" slot-scope="text, record">
          <a-icon :type="text"/>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon v-hasPermission="'application:view'" type="eye" theme="twoTone" twoToneColor="#42b983"
                  @click="view(record)" title="查看申请流程详情"></a-icon>
          <a-badge v-hasNoPermission="'application:view'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>

    <application-info
      @close="handleApplicationInfoClose"
      :applicationInfoData="applicationInfo.data"
      :applicationInfoVisiable="applicationInfo.visible">
    </application-info>

  </a-card>
</template>

<script>
  import ApplicationInfo from '@/views/application/submission/ApplicationInfo'
  import RangeDate from '@/components/datetime/RangeDate'
  import {mapState, mapMutations} from 'vuex'


  export default {
    name: 'Cancel',
    components: {
      ApplicationInfo,
      RangeDate
    },
    data() {
      return {
        applicationInfo: {
          data: {},
          visible: false
        },
        advanced: false,
        queryParams: {
          status:'FINISHED,RUNNING'
        },
        applyTypes: [],
        filteredInfo: null,
        sortedInfo: null,
        paginationInfo: null,
        dataSource: [],
        loading: false,
        selectedRowKeys: [],
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
        }
      },
      onSelectChange (selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys
      },
      handleDateChange(value) {
        if (value) {
          this.queryParams.createTimeFrom = value[0]
          this.queryParams.createTimeTo = value[1]
          this.queryParams.applyTimeFrom=this.queryParams.createTimeFrom
          this.queryParams.applyTimeTo=this.queryParams.createTimeTo
        }
      },
      view(record){
        this.applicationInfo.visible = true
        this.applicationInfo.data = record
      },
      handleApplicationInfoClose() {
        this.applicationInfo.visible = false
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
          ...filteredInfo,
          ...sortedInfo
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
        this.queryParams = {
           status:'FINISHED,RUNNING'
        }
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
      batchOperate () {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择需要销假的记录')
          return
        }
        let that = this
        this.$confirm({
          title: '确定将选中申请进行销假处理?',
          content: '当您点击确定按钮后，这些申请将被抵消',
          centered: true,
          onOk () {
            let ids = []
            for (let key of that.selectedRowKeys) {
              ids.push(that.dataSource[key].applicationId)
            }
            that.$delete(`apply/cancel/`+ids.join(',')).then(() => {
              that.$message.success('销假成功')
              that.selectedRowKeys = []
              that.search()
            })
          },
          onCancel () {
            that.selectedRowKeys = []
          }
        })
      },
         ...mapMutations({
        setUser: 'account/setUser'
      }),
    },
    mounted() {
      this.$get(`apply/types/${this.currentUser.userId}`).then((r) => {
        this.applyTypes = r.data
      }).catch((e) => {
        this.$message.error("休假类型获取失败")
      })
      this.fetch(this.queryParams);
   
    },
    computed: {
      ...mapState({
        currentUser: state => state.account.user
      }),
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
          title: '所属部门',
          dataIndex: 'deptName',
          align: 'center'
        }, {
          title: '职位',
          dataIndex: 'position',
          align: 'center'
        }, {
          title: '申请类型',
          dataIndex: 'leaveType',
          align: 'center'
        }, {
          title: '天数',
          dataIndex: 'days',
          align: 'center'
        }, {
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

