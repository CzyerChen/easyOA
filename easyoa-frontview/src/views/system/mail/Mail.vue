<template>
  <a-card :bordered="false" class="card-area">
    <div>
      <div class="operator">
        <a-button v-hasPermission="'mail:add'" ghost type="primary" @click="add">新增</a-button>
        <a-button v-hasPermission="'mail:delete'" @click="batchDelete">删除</a-button>
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
          <a-icon v-hasPermission="'mail:edit'" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修改邮件"></a-icon>
        </template>
      </a-table>

      <mail-add
        @close="handleMailAddClose"
        @success="handleMailAddSuccess"
        :mail-add-visiable="this.mailAdd.visible">
      </mail-add>

      <mail-edit
        ref="mailEdit"
        @close="handleMailEditClose"
        @success="handleMailEditSuccess"
        :mail-edit-visiable="this.mailEdit.visiable"
        :mail-info-data="this.mailInfo.data">
      </mail-edit>

    </div>
  </a-card>
</template>

<script>

  import MailAdd from './MailAdd';
  import MailEdit from './MailEdit';

  export default {
    name: 'Mail',
    components:{MailAdd,MailEdit},
    data () {
      return {
        mailAdd:{
          visible:false
        },
        mailInfo:{
          visiable: false,
          data:{}
        },
        mailEdit: {
          visiable: false,
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
          dataIndex: 'id'
        }, {
          title: '邮箱',
          dataIndex: 'email',
          width: 350
        }, {
          title: '启用',
          dataIndex: 'active',
          customRender: (text, row, index) => {
            switch (text) {
              case true:
                return <a-tag color = "cyan" > 启用 < /a-tag>
              case false:
                return <a-tag color = "pink" > 禁用 < /a-tag>
              default:
                return text
            }
          }
        },{
          title: '创建时间',
          dataIndex: 'createTime',
        },{
          title: '更新时间',
          dataIndex: 'updateTime'
        },
        {
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
        this.mailAdd.visible = true
      },
      handleMailAddClose(){
        this.mailAdd.visible = false
      },
      handleMailAddSuccess(){
        this.mailAdd.visible=false
        this.$message.success('新增邮件成功')
        this.search()
      },
      handleDateChange (value) {
        if (value) {
          this.queryParams.createTimeFrom = value[0]
          this.queryParams.createTimeTo = value[1]
        }
      },
      edit (record) {
        this.$refs.mailEdit.setFormValues(record)
        this.mailInfo.data = record
        this.mailEdit.visiable = true
      },
      handleMailEditSuccess () {
        this.mailEdit.visiable = false
        this.$message.success('修改邮件成功')
        this.search()
      },
      handleMailEditClose () {
        this.mailEdit.visiable = false
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
              ids.push(that.dataSource[key].id)
            }
            that.$delete(`mail/`+ids.join(',')).then(() => {
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
        this.$get('mail', {
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
