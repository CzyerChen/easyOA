<template>
  <div style="width: 100%">
    <a-tabs defaultActiveKey="1" :tabPosition="tabPosition" @change="callback">
      <a-tab-pane tab="进度通知" key="1">
        <div class="operator" style="margin-bottom: 18px">
          <a-button v-hasPermission="'mynotes:delete'" @click="remarkMine">标记为已读</a-button>
          <a-button v-hasPermission="'mynotes:delete'" @click="batchDeleteMine">删除</a-button>
          <a-dropdown>
            <a-button>
              暂无更多操作
              <a-icon type="down"/>
            </a-button>
          </a-dropdown>
        </div>
        <a-table ref="MyTableInfo"
                 :columns="myColumns"
                 :dataSource="myData"
                 :loading="loading"
                 :pagination="pagination1"
                 :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange1}"
                 @change="handleTableChangeForMine"
                 :scroll="{ x: 1500 }">
          <template slot="operation" slot-scope="text, record">
            &nbsp;<a-icon v-hasPermission="'process:view'" type="book" theme="twoTone" twoToneColor="#4a9ff5" @click="viewMine(record)" title="申请详情"></a-icon>
          </template>
        </a-table>
        <my-notes-info
          @close="handleMyNotesInfoClose"
          :my-notes-into-type="myNotesInfo.type"
          :myNotesInfoData="myNotesInfo.data"
          :myNotesInfoVisiable="myNotesInfo.visiable">
        </my-notes-info>

      </a-tab-pane>
      <a-tab-pane tab="系统公告" key="2">
        <div class="operator" style="margin-bottom: 18px">
          <!--<a-button v-hasPermission="'mynotes:delete'" @click="remark">标记为已读</a-button>-->
          <a-dropdown>
            <a-button>
              暂无更多操作
              <a-icon type="down"/>
            </a-button>
          </a-dropdown>
        </div>
        <a-table ref="TableInfo"
                 :columns="columns"
                 :dataSource="data"
                 :loading="loading"
                 :pagination="pagination2"
                 :rowSelection="{selectedRowKeys: selectedRowKeys2, onChange: onSelectChange2}"
                 @change="handleTableChange"
                 :scroll="{ x: 900 }">
          <template slot="operation" slot-scope="text, record">&nbsp;
            <a-icon type="eye" theme="twoTone" twoToneColor="#42b983" @click="view(record)" title="公告详情"></a-icon>
          </template>
        </a-table>
        <my-notes-info
          @close="handleMyNotesInfoClose"
          :my-notes-into-type="myNotesInfo.type"
          :myNotesInfoData="myNotesInfo.data"
          :myNotesInfoVisiable="myNotesInfo.visiable">
        </my-notes-info>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
  import PageLayout from "../../common/PageLayout";
  import MyNotesInfo from './MyNotesInfo'
  import {mapState, mapMutations} from 'vuex'


  const columns = [{
    title: '标题',
    dataIndex: 'title',
    align: 'center',
    width: 200
  }, {
    title: '优先级',
    width:150,
    dataIndex: 'priority',
    customRender: (text, row, index) => {
      switch (text) {
        case "L":
          return "一般消息";
        case "M":
          return "重要消息";
        case "H":
          return "紧急消息";
        default:
          return text
      }
    },
    align: 'center',
  }, {
    title: '发送人',
    dataIndex: 'sender',
    align: 'center',
  }, {
    title: '发送时间',
    dataIndex: 'sendTime',
    align: 'center'
  },{
    title: '操作',
    dataIndex: 'operation',
    width: 100,
    scopedSlots: {customRender: 'operation'},
    fixed: 'right'
  }]

  const myColumns = [{
    title: '标题',
    dataIndex: 'title',
    align: 'center',
    width: 200
  }, {
    title: '优先级',
    width: 150,
    dataIndex: 'priority',
    customRender: (text, row, index) => {
      switch (text) {
        case "L":
          return "一般消息";
        case "M":
          return "重要消息";
        case "H":
          return "紧急消息";
        default:
          return text
      }
    }
  }, {
    title: '内容',
    dataIndex: 'msgContent',
    align: 'center'
  }, {
    title: '发送者',
    dataIndex: 'sender',
    align: 'center'
  }, {
    title: '发送时间',
    dataIndex: 'sendTime',
    align: 'center'
  }, {
    title: '操作',
    dataIndex: 'operation',
    width: 100,
    scopedSlots: {customRender: 'operation'},
    fixed: 'right'
  }]


  export default {
    name: "MyNotes",
    components: {PageLayout, MyNotesInfo},
    data() {
      return {
        data: [],
        myData: [],
        columns,
        myColumns,
        tabPosition: 'left',
        loading: false,
        filteredInfo1: null,
        sortedInfo1: null,
        paginationInfo1: null,
        filteredInfo2: null,
        sortedInfo2: null,
        paginationInfo2: null,
        selectedRowKeys1: [],
        selectedRowKeys2: [],
        myNotesInfo: {
          visiable: false,
          data: {},
          type: 0
        },
        pagination1: {
          pageSizeOptions: ['10', '20', '30', '40', '100'],
          defaultCurrent: 1,
          defaultPageSize: 10,
          showQuickJumper: true,
          showSizeChanger: true,
          showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
        },
        pagination2: {
          pageSizeOptions: ['10', '20', '30', '40', '100'],
          defaultCurrent: 1,
          defaultPageSize: 10,
          showQuickJumper: true,
          showSizeChanger: true,
          showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
        }
      }
    },
    mounted() {
      this.fetchSysNotes();
      this.fetchMyNotes();
    },
    methods: {
      clickInfo(){
        this.$router.push({path:'/application'})
      },
      callback(key) {
        if (this.myNotesInfo.type === 0) {
          this.myNotesInfo.type = 1
        } else {
          this.myNotesInfo.type = 0
        }
      },
      fetchSysNotes(params = {}) {
        this.loading = true
        if (this.paginationInfo2) {
          // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
          this.$refs.TableInfo.pagination.current = this.paginationInfo2.current
          this.$refs.TableInfo.pagination.pageSize = this.paginationInfo2.pageSize
          params.pageSize = this.paginationInfo2.pageSize
          params.pageNum = this.paginationInfo2.current
        } else {
          // 如果分页信息为空，则设置为默认值
          params.pageSize = this.pagination2.defaultPageSize
          params.pageNum = this.pagination2.defaultCurrent
        }
        this.$get('notice/system', {
          ...params
        }).then((r) => {
          let data = r.data
          this.data = data.rows
          const pagination2 = {...this.pagination2}
          pagination2.total = data.total
          this.pagination2 = pagination2
          this.loading = false
        })
      },
      fetchMyNotes(params = {}) {
        this.loading = true
        if (this.paginationInfo1) {
          // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
          this.$refs.MyTableInfo.pagination.current = this.paginationInfo1.current
          this.$refs.MyTableInfo.pagination.pageSize = this.paginationInfo1.pageSize
          params.pageSize = this.paginationInfo1.pageSize
          params.pageNum = this.paginationInfo1.current
        } else {
          // 如果分页信息为空，则设置为默认值
          params.pageSize = this.pagination1.defaultPageSize
          params.pageNum = this.pagination1.defaultCurrent
        }
        this.$get(`notice/personal/${this.currentUser.userId}`, {
          ...params
        }).then((r) => {
          let data = r.data
          this.myData = data.rows
          const pagination1 = {...this.pagination1}
          pagination1.total = data.total
          this.pagination1 = pagination1
          this.loading = false
        })
      },
      onSelectChange1(selectedRowKeys) {
        this.selectedRowKeys1 = selectedRowKeys
      },
      onSelectChange2(selectedRowKeys) {
        this.selectedRowKeys2 = selectedRowKeys
      },
      handleChange(value, key, column) {
        const newData = [...this.data]
        const target = newData.filter(item => key === item.key)[0]
        if (target) {
          target[column] = value
          this.data = newData
        }
      },
      handleMyNotesInfoClose() {
        this.myNotesInfo.visiable = false
        this.searchMine();
        this.search();
      },
      handleTableChange(pagination, filters, sorter) {
        // 将这三个参数赋值给Vue data，用于后续使用
        this.paginationInfo2 = pagination
        this.filteredInfo2 = filters
        this.sortedInfo2 = sorter

        this.myNotesInfo.visiable = false
        this.fetchSysNotes({
          sortField: sorter.field,
          sortOrder: sorter.order,
          ...this.queryParams,
          ...filters
        })
      },
      handleTableChangeForMine(pagination, filters, sorter) {
        // 将这三个参数赋值给Vue data，用于后续使用
        this.paginationInfo1 = pagination
        this.filteredInfo1 = filters
        this.sortedInfo1 = sorter

        this.myNotesInfo.visiable = false
        this.fetchMyNotes({
          sortField: sorter.field,
          sortOrder: sorter.order,
          ...this.queryParams,
          ...filters
        })
      },
      view(record) {
        this.myNotesInfo.data = record;
        this.myNotesInfo.visiable = true
      },
      viewMine(record) {
        this.$router.push({path:'/application'})
      },
      search() {
        let {sortedInfo2, filteredInfo2} = this
        let sortField, sortOrder;
        // 获取当前列的排序和列的过滤规则
        if (sortedInfo2) {
          sortField = sortedInfo2.field
          sortOrder = sortedInfo2.order
        }
        this.fetchSysNotes({
          sortField: sortField,
          sortOrder: sortOrder,
          ...this.queryParams
        })
      },
      searchMine() {
        let {sortedInfo1, filteredInfo1} = this
        let sortField, sortOrder;
        // 获取当前列的排序和列的过滤规则
        if (sortedInfo1) {
          sortField = sortedInfo1.field
          sortOrder = sortedInfo1.order
        }
        this.fetchMyNotes({
          sortField: sortField,
          sortOrder: sortOrder,
          ...this.queryParams
        })
      },
      remarkMine() {
        if (!this.selectedRowKeys1.length) {
          this.$message.warning('请选择需要确认已读的消息')
          return
        }
        let that = this
        this.$confirm({
          title: '确定将选中消息标志为已读?',
          content: '当您点击确定按钮后，这些消息将被标志为已读',
          centered: true,
          onOk() {
            let ids = []
            for (let key of that.selectedRowKeys1) {
              ids.push(that.myData[key].noticeId)
            }
            that.$put('notice/check/', {
              noticeIds: ids.join(',')
            }).then(() => {
              that.$message.success('消息已标记为已读')
              that.selectedRowKeys1 = []
              that.searchMine()
            })
          },
          onCancel() {
            that.selectedRowKeys1 = []
          }
        })
      },
      remark() {
        if (!this.selectedRowKeys2.length) {
          this.$message.warning('请选择需要确认已读的消息')
          return
        }
        let that = this
        this.$confirm({
          title: '确定将选中消息标志为已读?',
          content: '当您点击确定按钮后，这些消息将被标志为已读',
          centered: true,
          onOk() {
            let ids = []
            for (let key of that.selectedRowKeys2) {
              ids.push(that.data[key].noticeId)
            }
            that.$put('notice/check/', {
              noticeIds: ids.join(',')
            }).then(() => {
              that.$message.success('消息已标记为已读')
              that.selectedRowKeys2 = []
              that.search()
            })
          },
          onCancel() {
            that.selectedRowKeys2 = []
          }
        })
      },
      batchDeleteMine() {
        if (!this.selectedRowKeys1.length) {
          this.$message.warning('请选择需要删除的消息')
          return
        }
        let that = this
        this.$confirm({
          title: '确定删除选中消息?',
          content: '当您点击确定按钮后，这些消息将被删除，无法寻回',
          centered: true,
          onOk() {
            let ids = []
            for (let key of that.selectedRowKeys1) {
              ids.push(that.myData[key].noticeId)
            }
            that.$delete('notice/' + ids.join(',')).then(() => {
              that.$message.success('消息已标记为已读')
              that.selectedRowKeys1 = []
              that.searchMine()
            })
          },
          onCancel() {
            that.selectedRowKeys1 = []
          }
        })
      },
    },
    computed: {
      ...mapState({
        currentUser: state => state.account.user
      })
    },
  }
</script>

<style lang="less" scoped>
  @import "../../../../static/less/Common";
</style>
