<template>
  <a-card :bordered="false" class="card-area">
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
          <a-icon v-hasPermission="'process:view'" type="eye" theme="twoTone" twoToneColor="#42b983"
                  @click="view(record)" title="查看申请流程详情"></a-icon>
          <a-badge v-hasNoPermission="'process:view'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>

    <application-info
      @close="handleApplicationInfoClose"
      :applicationInfoData="applicationInfo.data"
      :applicationInfoVisiable="applicationInfo.visible">
    </application-info>

  </a-card>
</template>

<script>
  import {mapState, mapMutations} from 'vuex'
  import ApplicationInfo from '../../application/submission/ApplicationInfo'

  export default {
    name: 'Process',
    components :{
      ApplicationInfo
    },
    data() {
      return {
        applicationInfo: {
          data: {},
          visible: false
        },
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
      ...mapMutations({
        setUser: 'account/setUser'
      }),
      view(record){
        this.applicationInfo.visible = true
        this.applicationInfo.data = record
      },
      handleApplicationInfoClose(){
        this.applicationInfo.visible= false
      },
      handleTableChange(pagination, filters, sorter) {
        // 将这三个参数赋值给Vue data，用于后续使用
        this.paginationInfo = pagination
        this.filteredInfo = filters
        this.sortedInfo = sorter

        this.userInfo.visiable = false
        this.fetch({
          sortField: sorter.field,
          sortOrder: sorter.order,
          ...this.queryParams,
          ...filters
        })
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
        this.$get(`apply/assignee/done/${this.currentUser.userId}`, {
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
    },
    mounted() {
      this.fetch()
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
          scopedSlots: {customRender:'operation'},
          fixed: 'right'
        }]
      },
      ...mapState({
        currentUser: state => state.account.user
      })
    }
  }

</script>

<style lang="less" scoped>

</style>
