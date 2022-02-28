<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row>
            <a-col :md="12" :sm="24">
              <a-form-item label="公司名称" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.companyName" />
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="创建时间" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                <range-date @change="handleDateChange" ref="createTime"></range-date>
              </a-form-item>
            </a-col>
          </a-row>
        </div>
        <span style="float: right; margin-top: 3px;">
          <a-button type="primary" @click="search">查询</a-button>
          <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </span>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button v-hasPermission="'company:add'" type="primary" ghost @click="add">新增</a-button>
        <a-button v-hasPermission="'company:delete'" @click="batchDelete">删除</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table
        ref="TableInfo"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="pagination"
        :loading="loading"
        :scroll="{ x: 900 }"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange"
      >
        <template slot="operation" slot-scope="text, record">
          <a-icon
            v-hasPermission="'company:update'"
            type="setting"
            theme="twoTone"
            twoToneColor="#4a9ff5"
            @click="edit(record)"
            title="修改"
          ></a-icon>
          <a-badge v-hasNoPermission="'company:update'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>
    <!-- 新增 -->
    <company-add
      @success="handleCompanyAddSuccess"
      @close="handleCompanyAddClose"
      :companyAddVisiable="companyAddVisiable"
    ></company-add>
    <company-edit
      ref="companyEdit"
      @success="handleCompanyEditSuccess"
      @close="handleCompanyEditClose"
      :companyEditVisiable="companyEditVisiable"
    ></company-edit>
  </a-card>
</template>

<script>
import RangeDate from "@/components/datetime/RangeDate";
import CompanyAdd from "./CompanyAdd";
import CompanyEdit from "./CompanyEdit";

export default {
  name: "Company",
  components: { CompanyAdd, CompanyEdit, RangeDate },
  data() {
    return {
      advanced: false,
      dataSource: [],
      selectedRowKeys: [],
      queryParams: {},
      sortedInfo: null,
      pagination: {
        pageSizeOptions: ["10", "20", "30", "40", "100"],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) =>
          `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      loading: false,
      companyAddVisiable: false,
      companyEditVisiable: false
    };
  },
  computed: {
    columns() {
      let { sortedInfo, filteredInfo } = this;
      sortedInfo = sortedInfo || {};
      filteredInfo = filteredInfo || {};
      return [
        {
          title: "名称",
          dataIndex: "companyName"
        },
        {
          title: "创建时间",
          dataIndex: "createTime",
          sorter: true,
          sortOrder: sortedInfo.columnKey === "createTime" && sortedInfo.order
        },
        {
          title: "修改时间",
          dataIndex: "updateTime",
          sorter: true,
          sortOrder: sortedInfo.columnKey === "updateTime" && sortedInfo.order
        },
        {
          title: "操作",
          dataIndex: "operation",
          scopedSlots: { customRender: "operation" },
          fixed: "right",
          width: 120
        }
      ];
    }
  },
  mounted() {
    this.fetch();
  },
  methods: {
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
    },
    handleCompanyAddClose() {
      this.companyAddVisiable = false;
    },
    handleCompanyAddSuccess() {
      this.companyAddVisiable = false;
      this.$message.success("新增公司成功");
      this.fetch();
    },
    add() {
      this.companyAddVisiable = true;
    },
    handleCompanyEditClose() {
      this.companyEditVisiable = false;
    },
    handleCompanyEditSuccess() {
      this.companyEditVisiable = false;
      this.$message.success("修改公司成功");
      this.fetch();
    },
    edit(record) {
      this.companyEditVisiable = true;
      this.$refs.companyEdit.setFormValues(record);
    },
    handleDateChange(value) {
      if (value) {
        this.queryParams.createTimeFrom = value[0];
        this.queryParams.createTimeTo = value[1];
      }
    },
    batchDelete() {
      if (!this.selectedRowKeys.length) {
        this.$message.warning("请选择需要删除的记录");
        return;
      }
      let that = this;
      this.$confirm({
        title: "确定删除所选中的记录?",
        content:
          "当您点击确定按钮后，这些记录将会被彻底删除！",
        centered: true,
        onOk() {
          let ids = [];
          for (let key of that.selectedRowKeys) {
            ids.push(that.dataSource[key].id);
          }
          that.$delete("company/" + ids.join(",")).then(() => {
            that.$message.success("删除成功");
            that.selectedRowKeys = [];
            that.search();
          });
        },
        onCancel() {
          that.selectedRowKeys = [];
        }
      });
    },
    search() {
      let { sortedInfo } = this;
      let sortField, sortOrder;
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field;
        sortOrder = sortedInfo.order;
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams
      });
    },
    reset() {
      // 取消选中
      this.selectedRowKeys = [];
       // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent;
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent;
        this.paginationInfo.pageSize = this.pagination.defaultPageSize;
      }
      // 重置列过滤器规则
      this.filteredInfo = null;
      // 重置列排序规则
      this.sortedInfo = null;
      // 重置查询参数
      this.queryParams = {};
      // 清空部门树选择
      this.$refs.deptTree.reset();
      // 清空时间选择
      if (this.advanced) {
        this.$refs.createTime.reset();
      }
    },
    handleTableChange(pagination, filters, sorter) {
      this.paginationInfo = pagination;
      this.filteredInfo = filters;
      this.sortedInfo = sorter;
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      });
    },
    fetch(params = {}) {
      this.loading = true;
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current;
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize;
        params.pageSize = this.paginationInfo.pageSize;
        params.pageNum = this.paginationInfo.current;
      } else {
        // 如果分页信息为空，则设置为默认值
        params.pageSize = this.pagination.defaultPageSize;
        params.pageNum = this.pagination.defaultCurrent;
      }
      this.$get("company", {
        ...params
      }).then(r => {
        this.loading = false;
         let data = r.data;
        const pagination = { ...this.pagination };
        pagination.total = data.total;
        this.dataSource = data.rows;
        this.pagination = pagination;
        // 数据加载完毕，关闭loading
        this.loading = false;
      });
    }
  }
};
</script>

<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
