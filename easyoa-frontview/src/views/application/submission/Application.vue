<template>
  <a-card :bordered="false" class="card-area">
    <div>
      <div class="operator">
        <a-button v-hasPermission="'application:add'" @click="add">新增</a-button>
          <a-tooltip>
    <template slot="title">
     填写错误->关闭当前申请，流程也将终止
    </template>
        <a-button v-hasPermission="'application:delete'" @click="batchDelete" type="danger">终止申请</a-button>
          </a-tooltip>
             <a-tooltip>
    <template slot="title">
      日程有变->可撤销当前申请(执行中/已完成均可)
    </template>
        <a-button v-hasPermission="'application:cancel'" @click="batchCancel" type="danger">撤销申请</a-button>
          </a-tooltip> 
        <a-dropdown v-hasPermission="'application:view'">
          <a-button>
            更多操作
            <a-icon type="down"/>
          </a-button>
        </a-dropdown>
      </div>
      <div style="border-bottom: #949fa8;margin-bottom: 10px">
        <a-collapse defaultActiveKey="1" :bordered="false" >
          <a-collapse-panel  key="1" :style="customStyle">
            <template slot="header">
            【休假规则说明】&nbsp;<a-icon type="notification" />
            </template>
            <p>{{text1}}</p>
            <br>
            <p>{{text2}}</p>
            <br>
            <p>{{text3}}</p>
            <br>
            <p>{{text4}}</p>
            <br>
            <p>{{text5}}</p>
            <br>
            <p>{{text6}}</p>
          </a-collapse-panel>
        </a-collapse>
      </div>
      <a-table ref="TableInfo"
               :columns="columns"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               @change="handleTableChange"
               :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
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

    <application-add
      @close="handleApplicationAddClose"
      @success="handleApplicationAddSuccess"
      :applicationAddVisiable="applicationAdd.visiable">
    </application-add>

    <application-info
    @close="handleApplicationInfoClose"
    :applicationInfoData="applicationInfo.data"
    :applicationInfoVisiable="applicationInfo.visible">
    </application-info>

  </a-card>
</template>

<script>
  import ApplicationAdd from './ApplicationAdd'
  import ApplicationInfo from './ApplicationInfo'
  import {mapState, mapMutations} from 'vuex'


  export default {
    name: "Application",
    components: {
      ApplicationAdd,
      ApplicationInfo
    },
    data() {
      return {
        text1: `1. 休假时间范围：上午8：30-12：00，下午13：00-17：30，跨越上下午计作1天，填写时间需符合工作时间；`,
        text2: `2. 入职当年度未满一年者，年假按入职日期至12月31日的天数折算，上半年仅限休全年年假的一半，剩余年假数详见【我的数据】；`,
        text3: `3. 若有年假剩余，请优先申请年假抵扣；`,
        text4: `4. 带薪病假工资按正常工资60%折算，请知悉，剩余带薪病假数详见【我的数据】.`,
        text5: `5. 婚假、产假、陪产假、病假（超过1天）需提交相关文件证明并上传系统审核.`,
        text6: `6. 特殊假期规定：婚假-3天，产假-42天（详情咨询管理人员），陪产假-15天，普通病假-20天.`,
        customStyle: 'background: #f7f7f7;border-radius: 4px;margin-bottom: 24px;border: 0;overflow: hidden;font-size:14px;font-weight:500',
        currentTab: 0,
        applicationAdd: {
          data: {},
          visiable: false
        },
        applicationInfo: {
          data: {},
          visible: false
        },
        key: +new Date(),
        dataSource: [],
        loading: false,
        selectedRowKeys: [],
        paginationInfo: null,
        pagination: {
          pageSizeOptions: ['10', '20', '30', '40', '100'],
          defaultCurrent: 1,
          defaultPageSize: 10,
          showQuickJumper: true,
          showSizeChanger: true,
          showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
        },
      }
    },
    computed: {
      columns() {
        let {sortedInfo,filteredInfo} = this;
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
        },{
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
          title:'阶段',
          dataIndex: 'stage',
          align:'center'
        }, {
          title: '创建时间',
          dataIndex: 'createTime',
          align: 'center',
          sorter: true,
          sortOrder: sortedInfo.columnKey === "createTime" && sortedInfo.order
        }, {
          title: '操作',
          dataIndex: 'operation',
          width: 100,
          scopedSlots: {customRender: 'operation'},
          fixed: 'right'
        }]
      },
      ...mapState({
        currentUser: state => state.account.user
      })
    },
    mounted() {
      this.fetch();
      this.$message.warning('请详细阅读【休假规则说明】点击展开详情', 3)
    },
    methods: {
      ...mapMutations({
        setUser: 'account/setUser'
      }),
      handleClick() {
        this.loading = !this.loading
      },
       onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
    },
      reset(){
        // 取消选中
        this.selectedRowKeys = []
        // 重置分页
        this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
        if (this.paginationInfo) {
          this.paginationInfo.current = this.pagination.defaultCurrent
          this.paginationInfo.pageSize = this.pagination.defaultPageSize
        }
        // 重置查询参数
        this.fetch()
      },
      onSelectChange(selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys
      },
      batchCancel(){
 if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择需要撤销的记录')
          return
        }
        let that = this
         let processIds = [];
          for (let key of that.selectedRowKeys) {
            processIds.push(that.dataSource[key].applicationId);
          }
        this.$confirm({
          title: '确定要撤销当前休假申请？',
          content: '当您点击确认后，当前休假申请将被立即撤销,休假数据也会回滚',
          centered: false,
          onOk() {
            that.$delete('apply/cancel/' + processIds.join(",")).then(() => {
              that.$message.success('申请已被撤销')
              that.selectedRowKeys = []
              that.fetch()
            })
          },
          onCancel() {
            that.selectedRowKeys = []
          }
        })
      },
      batchDelete() {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择需要删除的记录')
          return
        }
        let that = this
          let processIds = [];
          for (let key of that.selectedRowKeys) {
            processIds.push(that.dataSource[key].applicationId);
          }
        this.$confirm({
          title: '确定要终止当前休假申请？',
          content: '当您点击确认后，当前休假申请将被立即终止,后续流程也将一并终止',
          centered: false,
          onOk() {
            that.$delete('apply/' + processIds.join(",")).then(() => {
              that.$message.success('申请已被终止')
              that.selectedRowKeys = []
              that.fetch()
            })
          },
          onCancel() {
            that.selectedRowKeys = []
          }
        })
      },
      handleTableChange(pagination, filters, sorter) {
        // 将这两个个参数赋值给Vue data，用于后续使用
        this.paginationInfo = pagination
        alert("sorter.field"+sorter.field)
        alert("sorter.order"+sorter.order)
        this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
          ...filters
        })
      },
      view(record){
         this.applicationInfo.visible = true
         this.applicationInfo.data = record
      },
      handleApplicationInfoClose(){
        this.applicationInfo.visible= false
      },
      handleApplicationAddClose() {
        this.applicationAdd.visiable = false
        localStorage.removeItem("APPLY_INFO")
        this.fetch()
      },
      handleApplicationAddSuccess() {
        this.applicationAdd.visiable = false
        this.$message.success('新增申请成功')
        localStorage.removeItem("APPLY_INFO")
        this.fetch()
      },
      fetch(params = {}) {
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
        this.$get(`apply/user/${this.currentUser.userId}`, {
          ...params
        }).then((r) => {
          let data = r.data
          this.loading = false
          const pagination = {...this.pagination}
          pagination.total = data.total
          this.dataSource = data.rows
          this.pagination = pagination
        })
      },
      add() {
        this.applicationAdd.visiable = true
      },
    }
  }
</script>

<style lang="less" scoped>
  @import "../../../../static/less/Common";
</style>
