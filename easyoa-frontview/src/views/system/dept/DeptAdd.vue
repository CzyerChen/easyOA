<template>
  <a-drawer
    title="新增部门"
    :maskClosable="false"
    width="650"
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="deptAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;"
  >
    <a-form :form="form">
      <a-form-item label="部门名称" v-bind="formItemLayout">
        <a-input
          v-model="dept.deptName"
          v-decorator="['deptName',
                   {rules: [
                    { required: true, message: '部门名称不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"
        />
      </a-form-item>
      <a-form-item label="所属公司" v-bind="formItemLayout">
        <a-select
          mode="default"
           v-model="dept.companyId"
          notFoundContent="暂无类别"
          style="width: 100%"
          v-decorator="['companyId',{rules: [{ required: true, message: '请选择所属公司' }]}]"
        >
          <a-select-option v-for="a in companies" :key="a.id">{{a.companyName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="部门排序" v-bind="formItemLayout">
        <a-input-number v-model="dept.level" style="width: 100%" />
      </a-form-item>
      <a-form-item label="上级部门" style="margin-bottom: 2rem" v-bind="formItemLayout">
        <a-tree
          :key="deptTreeKeys"
          :checkable="true"
          :checkStrictly="true"
          @check="handleCheck"
          @expand="handleExpand"
          :expandedKeys="expandedKeys"
          :treeData="deptTreeData"
        ></a-tree>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
};
export default {
  name: "DeptAdd",
  props: {
    deptAddVisiable: {
      default: false
    }
  },
  data() {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      dept: {},
      companies: [],
      checkedKeys: [],
      expandedKeys: [],
      deptTreeData: [],
      deptTreeKeys: +new Date()
    };
  },
  methods: {
    reset() {
      this.loading = false;
      this.deptTreeKeys = +new Date();
      this.expandedKeys = this.checkedKeys = [];
      this.dept = {};
      this.form.resetFields();
    },
    onClose() {
      this.reset();
      this.$emit("close");
    },
    handleCheck(checkedKeys) {
      this.checkedKeys = checkedKeys;
    },
    handleExpand(expandedKeys) {
      this.expandedKeys = expandedKeys;
    },
    handleSubmit() {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined)
        ? this.checkedKeys
        : this.checkedKeys.checked;
      if (checkedArr.length > 1) {
        this.$message.error("最多只能选择一个上级部门，请修改");
        return;
      }
      if(!this.dept.deptName || !this.dept.companyId){
        this.$message.error("请填写部门名称和所属公司");
        return;
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true;
          if (checkedArr.length) {
            this.dept.parentId = checkedArr[0];
          } else {
            this.dept.parentId = "";
          }
          this.$post("dept", {
            ...this.dept
          })
            .then(r => {
              let res = r.data;
              if(res){
                 this.$message.error(res)
              }else{}
              this.reset();
              this.$emit("success");
            })
            .catch(() => {
              this.loading = false;
            });
        }
      });
    },
    loadCompanies() {
      this.$get("company/list").then(r => {
        let res = r.data;
        this.companies = res.data;
      });
    }
  },
  watch: {
    deptAddVisiable() {
      if (this.deptAddVisiable) {
        this.$get("company/list").then(r => {
        let res = r.data;
        this.companies = res.data;
      });
        this.$get("dept").then(r => {
          this.deptTreeData = r.data.rows.children;
        });
      }
    }
  }
};
</script>
