<template>
  <a-drawer
    title="修改邮件"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="mailEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">

      <a-form-item label='邮件名称'
                   v-bind="formItemLayout">
        <a-input readOnly  v-decorator="['email']"/>
      </a-form-item>
      <a-form-item label='邮件密码' v-bind="formItemLayout">
        <a-input v-decorator="[
          'password',
          {rules: [{ required: true, message: '邮箱密码不能为空' }]}
        ]"
                 type="password"
        />
      </a-form-item>
      <a-form-item label='是否启用' v-bind="formItemLayout">
        <a-radio-group
          v-model="active"
          v-decorator="[
            'active',
            {rules: [{ required: true, message: '请选择是否启用' }]}
          ]">
          <a-radio :value="true">是</a-radio>
          <a-radio :value="false">否</a-radio>
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
  const formItemLayout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 17 }
  }
  export default {
    name: 'MailEdit',
    props: {
      mailEditVisiable: {
        default: false
      },
      mailInfoData:{
        required: true
      }
    },
    data () {
      return {
        loading: false,
        formItemLayout,
        form: this.$form.createForm(this),
        active: ''
      }
    },
    methods: {
      reset () {
        this.loading = false
        this.form.resetFields()
      },
      onClose () {
        this.reset()
        this.$emit('close')
      },
      setFormValues({...rule}) {
        let fields = ['email', 'password', 'active']
        Object.keys(rule).forEach((key) => {
          if (fields.indexOf(key) !== -1) {
            this.form.getFieldDecorator(key)
            let obj = {}
            if(rule[key] === undefined){
              obj[key] =''
            }else{
              obj[key] = rule[key]
            }

            this.form.setFieldsValue(obj)
          }
        })
      },
      handleSubmit () {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            let mail = this.form.getFieldsValue()
            mail.id = this.mailInfoData.id
            mail.active=this.active
            this.$put('mail', {
              ...mail
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
      mailEditVisiable () {
        if (this.mailEditVisiable) {
        }
      }
    }
  }
</script>
