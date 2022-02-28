<template>
  <a-drawer
    title="新增流程"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="flowAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='流程名称' v-bind="formItemLayout">
        <a-input v-model="flow.name"
                 v-decorator="['name',
                   {rules: [
                    { required: true, message: '流程名称不能为空'},
                    { max: 10, message: '长度不能超过10个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='业务名称'
                   v-bind="formItemLayout">
        <a-input v-model="flow.content"
                 v-decorator="['content',
                 {rules: [
                  { required: true, message: '业务名称不能为空'},
                  { max: 50, message: '长度不能超过50个字符'}
                ]}]"/>
      </a-form-item>
      <a-form-item label='可委派人'
                   v-bind="formItemLayout">
          <a-select
            mode="multiple"
            :allowClear="true"
            v-model="assignee.id"
            style="width: 100%"
            v-decorator="['id',{rules: [{ required: false, message: '请选择委派人' }]}]">
            <a-select-option v-for="u in userData" :key="u.userId">{{u.userName}}</a-select-option>
          </a-select>
      </a-form-item>
      <a-form-item label='是否启用' v-bind="formItemLayout">
        <a-radio-group
          v-model="flow.deleted"
          v-decorator="[
            'deleted',
            {rules: [{ required: true, message: '请选择是否启用' }]}
          ]">
          <a-radio :value="false">是</a-radio>
          <a-radio :value="true">否</a-radio>
        </a-radio-group>
      </a-form-item>

      <a-form-item label='上级流程'
                   style="margin-bottom: 2rem"
                   v-bind="formItemLayout">
        <a-tree
          :key="flowTreeKey"
          :checkable="true"
          :checkStrictly="true"
          @check="handleCheck"
          @expand="handleExpand"
          :expandedKeys="expandedKeys"
          :treeData="flowTreeData">
        </a-tree>
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
    labelCol: { span: 4 },
    wrapperCol: { span: 17 }
  }
  export default {
    name: 'FlowAdd',
    props: {
      flowAddVisiable: {
        default: false
      }
    },
    data () {
      return {
        loading: false,
        formItemLayout,
        form: this.$form.createForm(this),
        flowTreeKey: +new Date(),
        userData: [],
        flow: {},
        assignee: {},
        checkedKeys: [],
        expandedKeys: [],
        flowTreeData: [],
      }
    },
    methods: {
      reset () {
        this.loading = false
        this.flowTreeKey = +new Date()
        this.expandedKeys = this.checkedKeys = []
        this.flow = {}
        this.form.resetFields()
      },
      onClose () {
        this.reset()
        this.$emit('close')
      },
      handleCheck (checkedKeys) {
        this.checkedKeys = checkedKeys
      },
      expandAll () {
        this.expandedKeys = this.allTreeKeys
      },
      closeAll () {
        this.expandedKeys = []
      },
      handleExpand (expandedKeys) {
        this.expandedKeys = expandedKeys
      },
      handleSubmit () {
        let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
        if (checkedArr.length > 1) {
          this.$message.error('最多只能选择一个上级流程，请修改')
          return
        }
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            if (checkedArr.length) {
              this.flow.parentId = checkedArr[0]
            } else {
              this.flow.parentId = '0'
            }
            // 0 表示菜单 1 表示按钮
            //this.flow.type = '0'
            if(this.assignee.id){
            let assignees = this.assignee.id.join(',')
            this.flow.assigneeIds = assignees
          }
            this.$post('flow', {
              ...this.flow
            }).then(() => {
              this.reset()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        })
      }
    },
    watch: {
      flowAddVisiable () {
        if (this.flowAddVisiable) {
          this.$get('user/cpy').then((r) => {
            this.userData = r.data.rows
          });
          this.$get('flow').then((r) => {
            this.flowTreeData = r.data.rows.children
            this.allTreeKeys = r.data.ids
          })
        }
      }
    }
  }
</script>
