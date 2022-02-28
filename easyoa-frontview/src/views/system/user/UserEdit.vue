<template>
  <a-drawer
    title="修改用户"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="userEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='用户名' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['userName']"/>
      </a-form-item>
      <a-form-item label='邮箱' v-bind="formItemLayout">
        <a-input
          v-decorator="[
          'email',
          {rules: [
            { type: 'email', message: '请输入正确的邮箱' },
            { max: 50, message: '长度不能超过50个字符'}
          ]}
        ]"/>
      </a-form-item>
      <a-form-item label="手机" v-bind="formItemLayout">
        <a-input
          v-decorator="['phone', {rules: [
            { pattern: '^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$', message: '请输入正确的手机号'}
          ]}]"/>
      </a-form-item>
      <a-form-item label='角色' v-bind="formItemLayout">
        <a-select
          mode="default"
          :allowClear="true"
          style="width: 100%"
          @change="handleSelectChange"
          v-decorator="[
            'roleId',
            {rules: [{ required: true, message: '请选择角色' }]}
          ]">
          <a-select-option v-for="r in roleData" :key="r.roleId.toString()">{{r.roleName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label='部门' v-bind="formItemLayout">
        <a-tree-select
          :allowClear="true"
          :dropdownStyle="{ maxHeight: '220px', overflow: 'auto' }"
          :treeData="deptTreeData"
          @change="onDeptChange"
          :value="userDept">
        </a-tree-select>
      </a-form-item>
      <a-form-item label='状态' v-bind="formItemLayout">
        <a-radio-group
          v-decorator="[
            'status',
            {rules: [{ required: true, message: '请选择状态' }]}
          ]">
          <a-radio value="0">锁定</a-radio>
          <a-radio value="1">有效</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='性别' v-bind="formItemLayout">
        <a-radio-group
          v-decorator="[
            'sex',
            {rules: [{ required: true, message: '请选择性别' }]}
          ]">
          <a-radio value="M">男</a-radio>
          <a-radio value="F">女</a-radio>
          <a-radio value="U">保密</a-radio>
        </a-radio-group>
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
import {mapState, mapMutations} from 'vuex'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'UserEdit',
  props: {
    userEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      deptTreeData: [],
      roleData: [],
      userDept: [],
      userId: '',
      loading: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  methods: {
    ...mapMutations({
      setUser: 'account/setUser'
    }),
    handleSelectChange(value){
    },
    onClose () {
      this.loading = false
      this.form.resetFields()
      this.$emit('close')
    },
    setFormValues ({...user}) {
      this.userId = user.userId
      let fields = ['userName', 'email',  'sex', 'phone']
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
      if (user.roleId) {
        this.form.getFieldDecorator('roleId')
         this.form.setFieldsValue({'roleId': user.roleId+''})
      }
      if(user.status){
        this.form.getFieldDecorator('status')
         this.form.setFieldsValue({'status': user.status+''})
      }
      if (user.deptId) {
        this.userDept = [user.deptId]
      }
    },
    onDeptChange (value) {
      this.userDept = value
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let user = this.form.getFieldsValue()
          user.userId = this.userId
          user.deptId = this.userDept
          user.userName = this.userName
          this.$put('user', {
            ...user
          }).then((r) => {
            this.loading = false
            this.$emit('success')
            // 如果修改用户就是当前登录用户的话，更新其state
            if (user.userName === this.currentUser.userName) {
              this.$get(`user/${user.userName}`).then((r) => {
                this.setUser(r.data)
              })
            }
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  },
  watch: {
    userEditVisiable () {
      if (this.userEditVisiable) {
        this.$get('role/all').then((r) => {
          this.roleData = r.data.rows
        })
        this.$get('dept').then((r) => {
          this.deptTreeData = r.data.rows.children
        })
      }
    }
  }
}
</script>
