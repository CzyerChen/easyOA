<template>
     <a-card :bordered="false" class="card-area">
      <div>
        <div class="operator">
          <a-button v-hasPermission="'flow:add'" @click="add">新增</a-button>
          <a-button v-hasPermission="'flow:delete'" @click="batchDelete">删除</a-button>
          <a-dropdown v-hasPermission="'flow:view'">
            <a-button>
              更多操作
              <a-icon type="down"/>
            </a-button>
          </a-dropdown>
        </div>
       <a-table :columns="columns"
                 :key="key"
                 :dataSource="dataSource"
                 :pagination="pagination"
                 :loading="loading"
                 :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
                 @change="handleTableChange"
                 :scroll="{ x: 2000 }">
          <template slot="icon" slot-scope="text, record">
            <a-icon :type="text"/>
          </template>
          <template slot="operation" slot-scope="text, record">
            <a-icon v-hasPermission="'flow:edit'" type="setting" theme="twoTone" twoToneColor="#4a9ff5"
                    @click="edit(record)" title="修改"></a-icon>
            <a-badge v-hasNoPermission="'flow:edit'" status="warning" text="无权限"></a-badge>
          </template>
        </a-table>
    </div>

    <flow-add
      @close="handleFlowAddClose"
      @success="handleFlowAddSuccess"
      :flowAddVisiable="flowAddVisiable">
    </flow-add>
    <flow-edit
      ref="flowEdit"
      @close="handleFlowEditClose"
      @success="handleFlowEditSuccess"
      :flowEditVisiable="flowEditVisiable">
    </flow-edit>
  </a-card>
</template>

<script>
  import RangeDate from '@/components/datetime/RangeDate'
  import FlowAdd from './FlowAdd'
  import FlowEdit from './FlowEdit'

  export default {
    name: 'Flow',
    components: {FlowAdd, FlowEdit, RangeDate},
    data() {
      return {
        advanced: false,
        key: +new Date(),
        queryParams: {},
        filteredInfo: null,
        dataSource: [],
        selectedRowKeys: [],
        pagination: {
          defaultPageSize: 1000,
          hideOnSinglePage: true,
          indentSize: 100
        },
        loading: false,
        flowAddVisiable: false,
        flowEditVisiable: false,
        flowInfo:{
          data:{}
        }
      }
    },
    computed: {
      columns() {
        let {filteredInfo} = this
        filteredInfo = filteredInfo || {}
        return [{
          title: '流程名称',
          dataIndex: 'text',
          width: 300,
          fixed: 'left'
        }, {
          title: '业务名称',
          dataIndex: 'title',
        }, {
          title: '可委派人',
          dataIndex: 'assigneeNames',
        }, {
          title: '创建时间',
          dataIndex: 'createTime'
        }, {
          title: '更新时间',
          dataIndex: 'updateTime'
        }, {
          title: '状态',
          dataIndex: 'status',
          customRender: (text, row, index) => {
            switch (text) {
              case false:
                return <a-tag color="cyan" > 启用 </a-tag>
              case true:
                return <a-tag color="pink" > 禁用 </a-tag>
              default:
                return text
            }
          },
          filters: [
            {text: '启用', value: false},
            {text: '禁用', value: true}
          ],
          filterMultiple: false,
          filteredValue: filteredInfo.type || null,
          onFilter: (value, record) => record.type.includes(value)
        }, {
          title: '操作',
          dataIndex: 'operation',
          width: 100,
          scopedSlots: {customRender: 'operation'},
          fixed: 'right'
        }]
      }
    },
    mounted() {
      this.fetch()
    },
    methods: {
      onSelectChange(selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys
      },
      handleFlowEditClose() {
        this.flowEditVisiable = false
      },
      handleFlowEditSuccess() {
        this.flowEditVisiable = false
        this.$message.success('修改流程成功')
        this.fetch()
      },
      edit(record) {
        this.$refs.flowEdit.setFormValues(record)
        this.flowEditVisiable = true
      },
      handleFlowAddClose() {
        this.flowAddVisiable = false
      },
      handleFlowAddSuccess() {
        this.flowAddVisiable = false
        this.$message.success('新增流程成功')
        this.fetch()
      },
      createMenu() {
        this.flowAddVisiable = true
      },
      handleDateChange(value) {
        if (value) {
          this.queryParams.createTimeFrom = value[0]
          this.queryParams.createTimeTo = value[1]
        }
      },
      batchDelete() {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择需要删除的记录')
          return
        }
        let that = this
        this.$confirm({
          title: '确定删除所选中的记录?',
          content: '当您点击确定按钮后，这些记录将会被彻底删除，如果其包含子记录，也将一并删除！',
          centered: true,
          onOk() {
            that.$delete('flow/' + that.selectedRowKeys.join(',')).then(() => {
              that.$message.success('删除成功')
              that.selectedRowKeys = []
              that.fetch()
            })
          },
          onCancel() {
            that.selectedRowKeys = []
          }
        })
      },
      search() {
        let {filteredInfo} = this
        this.fetch({
          ...this.queryParams,
          ...filteredInfo
        })
      },
      reset() {
        // 取消选中
        this.selectedRowKeys = []
        // 重置列过滤器规则
        this.filteredInfo = null
        // 重置查询参数
        this.queryParams = {}
        // 清空时间选择
        this.$refs.createTime.reset()
        this.fetch()
      },
      handleTableChange(pagination, filters, sorter) {
        // 将这两个个参数赋值给Vue data，用于后续使用
        this.filteredInfo = filters
        this.fetch({
          sortField: sorter.field,
          sortOrder: sorter.order,
          ...this.queryParams,
          ...filters
        })
      },
      fetch(params = {}) {
        this.loading = true
        this.$get('flow', {
          ...params
        }).then((r) => {
          let data = r.data
          this.loading = false
          if (Object.is(data.rows.children, undefined)) {
            this.dataSource = data.rows
          } else {
            this.dataSource = data.rows.children
          }
        })
      },
      add() {
        this.flowAddVisiable = true
      },
    }
  }
</script>
<style lang="less" scoped>
  @import "../../../../static/less/Common";
</style>
