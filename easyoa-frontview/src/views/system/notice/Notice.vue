<template>
  <a-card :bordered="false" class="card-area">
    <div>
      <div class="operator">
        <a-button v-hasPermission="'notice:add'" ghost type="primary" @click="add">新增</a-button>
        <a-button v-hasPermission="'notice:delete'" @click="batchDelete">删除</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="remark" slot-scope="text, record">
          <a-popover placement="topLeft">
            <template slot="content">
              <div style="max-width: 200px">{{text}}</div>
            </template>
            <p style="width: 200px;margin-bottom: 0">{{text}}</p>
          </a-popover>
        </template>
        <template slot="operation" slot-scope="text, record">
          &nbsp
          <a-icon type="eye" theme="twoTone" twoToneColor="#42b983" @click="view(record)" title="查看公告"></a-icon>
        </template>
      </a-table>

      <notice-add
      @close="handleNoticeAddClose"
      @success="handleNoticeAddSuccess"
      :notice-add-visiable="this.noticeAdd.visible">
      </notice-add>

      <notice-info
      @close="handleNoticeInfoClose"
      :notice-info-visiable="noticeInfo.visiable"
      :notice-info-data="noticeInfo.data">
      </notice-info>

    </div>
  </a-card>
</template>

<script>

  import NoticeAdd from './NoticeAdd';
  import NoticeInfo from './NoticeInfo';

  export default {
    name: 'Notice',
    components:{NoticeAdd,NoticeInfo},
    data () {
      return {
        noticeAdd:{
          visible:false
        },
        noticeInfo:{
          visiable: false,
          data:{}
        },
        dataSource: [],
        sortedInfo: {},
        paginationInfo: null,
        selectedRowKeys: [],
        pagination: {
          pageSizeOptions: ['10', '20', '30', '40', '100'],
          defaultCurrent: 1,
          defaultPageSize: 10,
          showQuickJumper: true,
          showSizeChanger: true,
          showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
        },
        loading: false
      }
    },
    computed: {
      columns () {
        let { sortedInfo } = this
        sortedInfo = sortedInfo || {}
        return [{
          title: '序号',
          dataIndex: 'noticeId'
        }, {
          title: '标题',
          dataIndex: 'title',
          width: 350
        }, {
          title: '创建时间',
          dataIndex: 'createTime',
        }, {
          title: '发送者',
          dataIndex: 'sender',
        },  {
          title: '发送时间',
          dataIndex: 'sendTime',
        },{
          title: '操作',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }]
      }
    },
    mounted () {
      this.fetch()
    },
    methods: {
      onSelectChange (selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys
      },
      add () {
        this.noticeAdd.visible = true
      },
      handleNoticeAddClose(){
        this.noticeAdd.visible = false
      },
      handleNoticeAddSuccess(){
        this.noticeAdd.visible=false
        this.$message.success('新增公告成功')
        this.search()
      },
      view (record) {
        this.noticeInfo.data = record
        this.noticeInfo.visiable =true
      },
      handleNoticeInfoClose () {
        this.noticeInfo.visiable=false
      },
      handleDateChange (value) {
        if (value) {
          this.queryParams.createTimeFrom = value[0]
          this.queryParams.createTimeTo = value[1]
        }
      },
      batchDelete () {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择需要删除的记录')
          return
        }
        let that = this
        this.$confirm({
          title: '确定删除所选中的记录?',
          content: '当您点击确定按钮后，这些记录将会被彻底删除',
          centered: true,
          onOk () {
            let ids = []
            for (let key of that.selectedRowKeys) {
              ids.push(that.dataSource[key].noticeId)
            }
            that.$delete(`notice/`+ids.join(',')).then(() => {
              that.$message.success('删除成功')
              that.selectedRowKeys = []
              that.search()
            })
          },
          onCancel () {
            that.selectedRowKeys = []
          }
        })
      },
      search () {
        this.fetch()
      },
      reset () {
        // 取消选中
        this.selectedRowKeys = []
        // 重置分页
        this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
        if (this.paginationInfo) {
          this.paginationInfo.current = this.pagination.defaultCurrent
          this.paginationInfo.pageSize = this.pagination.defaultPageSize
        }
        // 重置列排序规则
        this.sortedInfo = null
        this.fetch()
      },
      handleTableChange (pagination, filters, sorter) {
        // 将这两个参数赋值给Vue data，用于后续使用
        this.paginationInfo = pagination
        this.sortedInfo = sorter
        this.fetch()
      },
      fetch (params = {}) {
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
        this.$get('notice/system', {
          ...params
        }).then((r) => {
          let data = r.data
          const pagination = { ...this.pagination }
          pagination.total = data.total
          this.dataSource = data.rows
          this.pagination = pagination
          this.loading = false
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  @import "../../../../static/less/Common";
</style>
