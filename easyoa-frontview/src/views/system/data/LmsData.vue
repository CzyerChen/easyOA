<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row>
          <div :class="advanced ? null: 'fold'">
            <a-col :md="12" :sm="24">
              <a-form-item
                label="用户工号"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.userCode"/>
              </a-form-item>
            </a-col>

           <!-- <a-col :md="12" :sm="24">
              <a-form-item
                label="月份"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-select
                  mode="default"
                  notFoundContent="暂无类别"
                  v-model="queryParams.month"
                  style="width: 100%">
                  <a-select-option v-for="a in months" :key="a">{{a}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>-->
            <a-col :md="12" :sm="24">
              <a-form-item
                label="月份"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-month-picker
                :defaultValue="moment()"
                :disabledDate="disabledDate"
                @change="onMonthChange"
                placeholder="请选择月份"
                 />
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
      </a-table>
    </div>

  </a-card>
</template>

<script>
import moment from 'moment';
moment.locale('zh-cn');

  export default {
    name: 'LmsData',
    components: {
    },
    data() {
      return {
        advanced: false,
        queryParams: {
          month: moment().format('YYYY-MM'),
        },
        months: ['1','2','3','4','5','6','7','8','9','10','11','12'],
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
      moment,
      disabledDate(current) {
        // Can not select days before today and today
        return current && current > moment().endOf('day');
      },
      onMonthChange(date, dateString) {
        this.queryParams.month = dateString
      },
      toggleAdvanced() {
        this.advanced = !this.advanced;
        if (!this.advanced) {
          this.queryParams.createTimeFrom = ''
          this.queryParams.createTimeTo = ''
        }
      },
      handleDateChange(value) {
        if (value) {
          this.queryParams.createTimeFrom = value[0]
          this.queryParams.createTimeTo = value[1]
        }
      },
      handleTableChange(pagination, filters, sorter) {
        // 将这三个参数赋值给Vue data，用于后续使用
        this.paginationInfo = pagination
        this.filteredInfo = filters
        this.sortedInfo = sorter

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
        if(!this.queryParams.month){
          params.month = moment().format('YYYY-MM');
        }else{
          params.month = this.queryParams.month;
        }
        this.$get(`vacation/cal`, {
          ...params
        }).then((r) => {
          let data = r.data
          const pagination = {...this.pagination}
          pagination.total = data.total
          this.dataSource = data.rows
          this.pagination = pagination
          // 数据加载完毕，关闭loading
          this.loading = false
        }).catch((e)=>{
          this.loading=false
          this.$message.error("操作失败")
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
        this.$export('vacation/cal/excel', {
          sortField: sortField,
          sortOrder: sortOrder,
          ...this.queryParams,
          ...filteredInfo
        })
      }
    },
    mounted() {
      this.fetch();
    },
    computed: {
      columns() {
        let {sortedInfo, filteredInfo} = this;
        sortedInfo = sortedInfo || {};
        filteredInfo = filteredInfo || {};
        return [{
          title: '员工编号',
          dataIndex: 'userCode',
          align: 'center'
        }, {
          title: '姓名',
          dataIndex: 'userName',
          align: 'center'
        }, {
          title: '入职日期',
          dataIndex: 'hireDate',
          align: 'center'
        }, {
          title: '年假',
          dataIndex: 'annual',
          align: 'center'
        }, {
          title: '有薪病假',
          dataIndex: 'sick',
          align: 'center'
        }, {
          title: '病假',
          dataIndex: 'sickNormal',
          align: 'center'
        }, {
          title: '婚假',
          dataIndex: 'marriage',
          align: 'center'
        }, {
          title: '产假/陪产假',
          dataIndex: 'materPaternity',
          align: 'center'
        }, {
          title: '孕检假',
          dataIndex: 'maternity4',
          align: 'center'
        }, {
          title: '丧假',
          dataIndex: 'funeral',
          align: 'center'
        }, {
          title: '事假',
            dataIndex: 'casual',
            align: 'center'
        }, {
          title: '应有年假',
          dataIndex: 'annualShould',
          align: 'center'
        }, {
          title: '折算后年假',
          dataIndex: 'annualCal',
          align: 'center'
        }, {
          title: '今年剩余年假',
          dataIndex: 'annualRest',
          align: 'center'
        },{
          title: '上年剩余年假',
          dataIndex: 'restAnnualLeave',
          align: 'center'
        }]
      },
    }
  }
</script>

<style lang="less" scoped>
  @import "../../../../static/less/Common";
</style>

