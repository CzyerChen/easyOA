<template>
  <div style="width: 100%">
    <a-tabs defaultActiveKey="1" :tabPosition="tabPosition">
      <a-tab-pane tab="规则管理" key="1">
        <div class="operator">
          <a-button v-hasPermission="'rule:delete'" @click="batchDeleteRule">删除</a-button>
          <a-dropdown >
            <a-button>
              更多操作 <a-icon type="down" />
            </a-button>
          </a-dropdown>
        </div>
        <a-table ref="RuleTableInfo"
                 :columns="ruleColumns"
                 :dataSource="ruleData"
                 :loading="loading"
                 :pagination="pagination1"
                 :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange1}"
                 @change="handleTableChangeForRule">
          <template slot="operation" slot-scope="text, record">
            <a-icon v-hasPermission="'rule:edit'" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="editRule(record)" title="修改"></a-icon>
            &nbsp;
            <a-icon type="eye" theme="twoTone" twoToneColor="#42b983" @click="viewRule(record)" title="查看"></a-icon>
          </template>
        </a-table>
        <rule-info
          @close="handleRuleInfoClose"
          :ruleInfoData="ruleInfo.data"
          :ruleInfoVisiable="ruleInfo.visiable">
        </rule-info>

        <rule-add
        @close="handleRuleAddClose"
        @success="handleRuleAddSuccess"
        :rule-add-visiable="ruleAdd.visiable">
        </rule-add>

        <!-- 修改规则 -->
        <rule-edit
          ref="ruleEdit"
          :ruleInfoData="ruleInfo.data"
          @close="handleRuleEditClose"
          @success="handleRuleEditSuccess"
          :ruleEditVisiable="ruleEdit.visiable">
        </rule-edit>

      </a-tab-pane>
      <a-tab-pane tab="节日管理" key="2">
        <div>
          <p><a-icon type="menu-fold"/><b>  {{moment().format('YYYY')}}年 假期列表 </b></p>
          <div class="operator">
        <a-dropdown >
          <a-menu slot="overlay">
            <a-menu-item  key="excel-vacation">
              <div v-if="this.data.length === 0">
              <a-upload
                class="upload-area"
                :fileList="fileList"
                :remove="handleRemove"
                :disabled="fileList.length === 1"
                :beforeUpload="beforeUpload"
              >
                <a-button>
                  <a-icon type="upload"/>
                  导入假期信息
                </a-button>
              </a-upload>
              <a-button
                @click="handleUpload"
                :disabled="fileList.length === 0"
                :loading="this.uploading">
                {{this.uploading ? '上传中' : '上传完毕' }}
              </a-button>
              </div>
            </a-menu-item>
          </a-menu>
          <a-button>
            更多操作
            <a-icon type="down"/>
          </a-button>
        </a-dropdown>
      </div>
        </div>

        <a-table ref="TableInfo"
                 :columns="columns"
                 :dataSource="data"
                 :loading="loading"
                 :pagination="pagination2"
                 :rowSelection="{selectedRowKeys: selectedRowKeys2, onChange: onSelectChange2}"
                 @change="handleTableChange">
          <template slot="operation" slot-scope="text, record">
            <a-icon v-hasPermission="'vacation:edit'" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修改"></a-icon>
            &nbsp;
            <a-icon type="eye" theme="twoTone" twoToneColor="#42b983" @click="view(record)" title="查看"></a-icon>
          </template>
        </a-table>
        <vacation-info
          @close="handleVacationInfoClose"
          :vacationInfoData="vacationInfo.data"
          :vacationInfoVisiable="vacationInfo.visiable">
        </vacation-info>

        <!-- 修改角色 -->
        <vacation-edit
          ref="vacationEdit"
          :vacationInfoData="vacationInfo.data"
          @close="handleVacationEditClose"
          @success="handleVacationEditSuccess"
          :vacationEditVisiable="vacationEdit.visiable">
        </vacation-edit>

      </a-tab-pane>
    <!--  <a-tab-pane tab="节日日历" key="3">
        <a-calendar
          @change="onCalendarChange"
          @select="onCalendarSelect">
          <ul class="events" slot="dateCellRender" slot-scope="value">
            <li v-for="item in getCalendarData(value)" :key="item.content">
              <a-badge :status="item.type" :text="item.content"/>
            </li>
          </ul>
          <template slot="monthCellRender" slot-scope="value">
            <div v-if="getMonthData(value)" class="notes-month">
              <section>{{getMonthData(value)}}</section>
              <span>Backlog number</span>
            </div>
          </template>
        </a-calendar>
      </a-tab-pane>-->
    </a-tabs>
  </div>

 <!-- <div>
    <a-card>

    </a-card>
  </div>-->
</template>

<script>
import PageLayout from '../../common/PageLayout'
import VacationInfo from './VacationInfo'
import VacationEdit from './VacationEdit'
import RuleInfo from './RuleInfo'
import RuleEdit from './RuleEdit'
import RuleAdd from './RuleAdd'
import moment from 'moment'
moment.locale('zh-cn')

const columns = [{
  title: '节日名称',
  dataIndex: 'name',
  width: '25%',
  align: 'center'
}, {
  title: '节日日期',
  dataIndex: 'festival',
  width: '25%',
  align: 'center'
}, {
  title: '放假天数',
  dataIndex: 'days',
  width: '25%',
  align: 'center'
}, {
  title: '操作',
  dataIndex: 'operation',
  align: 'center',
  scopedSlots: {customRender: 'operation'}
}]

const ruleColumns = [{
  title: '规则名称',
  dataIndex: 'ruleName',
  width: '10%',
  align: 'center'
}, {
  title: '最长假期',
  dataIndex: 'maxPermitDay',
  width: '10%',
  align: 'center'
}, {
  title: '说明',
  dataIndex: 'notice',
  width: '20%',
  align: 'center'
}, {
  title: '文件要求',
  dataIndex: 'fileRequired',
  width: '15%',
  align: 'center'
}, {
  title: '创建时间',
  dataIndex: 'createTime',
  width: '15%',
  align: 'center'
}, {
  title: '更新时间',
  dataIndex: 'updateTime',
  width: '15%',
  align: 'center'
}, {
  title: '操作',
  dataIndex: 'operation',
  align: 'center',
  scopedSlots: {customRender: 'operation'}
}]

export default {
  name: 'Rule',
  components: {PageLayout, VacationInfo, VacationEdit, RuleInfo, RuleEdit, RuleAdd},
  data () {
    return {
      fileList: [],
      uploading: false,
      data: [],
      ruleData: [],
      columns,
      ruleColumns,
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
      vacationInfo: {
        visiable: false,
        data: {}
      },
      vacationEdit: {
        visiable: false
      },
      ruleInfo: {
        visiable: false,
        data: {}
      },
      ruleEdit: {
        visiable: false
      },
      ruleAdd: {
        visiable: false
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
  mounted () {
    this.getVacationData()
    this.getRuleData()
  },
  created () {

  },
  methods: {
    moment,
    handleUpload () {
      const {fileList} = this
      const formData = new FormData()
      formData.append('excel-vacation', fileList[0])
      debugger
      this.uploading = true
      this.$upload('vacation/excel', formData).then((r) => {
        let data = r.data
        if (data.code === 200) {
          this.$message.success('文件上传成功')
          this.importData = data.data
          this.errors = data.error
          this.times = data.time / 1000
          this.uploading = false
          this.importResultVisible = true
          this.form.uploadFile = data.data
        }
      }).catch((r) => {
        console.error(r)
        this.uploading = false
        this.fileList = []
      })
    },
    handleRemove (file) {
      if (this.uploading) {
        this.$message.warning('文件导入中，请勿删除')
        return
      }
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    beforeUpload (file) {
      this.fileList = [...this.fileList, file]
      return false
    },
    getCalendarData (value) {
      this.loading = true
      let year = value.year()
      let month = value.month()
      let day = value.date()
      this.$get(`vacation/calendar/${year}/${month}/${day}`).then(r => {
        return []
      }).catch((e) => {
        this.loading = false
      })
    },
    getVacationData (params = {}) {
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
      this.$get('vacation/page', {
        ...params
      }).then((r) => {
        let data = r.data
        this.data = data.rows
        const pagination2 = { ...this.pagination2 }
        pagination2.total = data.total
        this.pagination2 = pagination2
        this.loading = false
      })
    },
    getRuleData (params = {}) {
      this.loading = true
      if (this.paginationInfo1) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.RuleTableInfo.pagination.current = this.paginationInfo1.current
        this.$refs.RuleTableInfo.pagination.pageSize = this.paginationInfo1.pageSize
        params.pageSize = this.paginationInfo1.pageSize
        params.pageNum = this.paginationInfo1.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.pageSize = this.pagination1.defaultPageSize
        params.pageNum = this.pagination1.defaultCurrent
      }
      this.$get('rule/page', {
        ...params
      }).then((r) => {
        let data = r.data
        this.ruleData = data.rows
        const pagination1 = { ...this.pagination1 }
        pagination1.total = data.total
        this.pagination1 = pagination1
        this.loading = false
      })
    },
    onSelectChange1 (selectedRowKeys) {
      this.selectedRowKeys1 = selectedRowKeys
    },
    onSelectChange2 (selectedRowKeys) {
      this.selectedRowKeys2 = selectedRowKeys
    },
    onCalendarChange (value) {
      this.getCalendarData(value)
    },
    onCalendarSelect (value) {
      this.getCalendarData(value)
    },
    handleChange (value, key, column) {
      const newData = [...this.data]
      const target = newData.filter(item => key === item.key)[0]
      if (target) {
        target[column] = value
        this.data = newData
      }
    },
    handleVacationEditClose () {
      this.vacationEdit.visiable = false
    },
    handleVacationEditSuccess () {
      this.vacationEdit.visiable = false
      this.$message.success('修改节日成功')
      this.search()
    },
    handleRuleEditClose () {
      this.ruleEdit.visiable = false
    },
    handleRuleEditSuccess () {
      this.ruleEdit.visiable = false
      this.$message.success('修改规则成功')
      this.searchRules()
    },
    handleVacationInfoClose () {
      this.vacationInfo.visiable = false
    },
    handleRuleInfoClose () {
      this.ruleInfo.visiable = false
    },
    addRule () {
      this.ruleAdd.visiable = true
    },
    handleRuleAddClose () {
      this.ruleAdd.visiable = false
    },
    handleRuleAddSuccess () {
      this.ruleAdd.visiable = false
      this.$message.success('新增规则成功')
      this.searchRules()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo2 = pagination
      this.filteredInfo2 = filters
      this.sortedInfo2 = sorter

      this.vacationInfo.visiable = false
      this.getVacationData({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    handleTableChangeForRule (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo1 = pagination
      this.filteredInfo1 = filters
      this.sortedInfo1 = sorter

      this.ruleInfo.visiable = false
      this.getRuleData({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    view (record) {
      this.vacationInfo.data = record
      this.vacationInfo.visiable = true
    },
    viewRule (record) {
      this.ruleInfo.data = record
      this.ruleInfo.visiable = true
    },
    edit (record) {
      this.$refs.vacationEdit.setFormValues(record)
      this.vacationEdit.visiable = true
      this.vacationInfo.data = record
    },
    editRule (record) {
      this.$refs.ruleEdit.setFormValues(record)
      this.ruleEdit.visiable = true
      this.ruleInfo.data = record
    },
    search () {
      let {sortedInfo2, filteredInfo2} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo2) {
        sortField = sortedInfo2.field
        sortOrder = sortedInfo2.order
      }
      this.getVacationData({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams
      })
    },
    searchRules () {
      let {sortedInfo1, filteredInfo1} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo1) {
        sortField = sortedInfo1.field
        sortOrder = sortedInfo1.order
      }
      this.getRuleData({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams
      })
    },
    batchDeleteRule () {
      if (!this.selectedRowKeys1.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ruleIds = []
          for (let key of that.selectedRowKeys1) {
            ruleIds.push(that.ruleData[key].ruleId)
          }
          that.$delete('rule/' + ruleIds.join(',')).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys1 = []
            that.searchRules()
          })
        },
        onCancel () {
          that.selectedRowKeys1 = []
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
  @import "../../../../static/less/Common";

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

  .editable-row-operations a {
    margin-right: 8px;
  }
</style>
