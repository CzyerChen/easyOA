<template>
  <a-drawer
    title="新增邮件"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="mailAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='邮件名称' v-bind="formItemLayout">
        <!-- <a-input style="width: 100%"
                  v-model="data.title"
                  v-decorator="['keyy',
                    {rules: [
                     { required: true, message: '不能为空'}
                   ]}]"/>-->
        <a-input v-model="data.email"
                 @blur="handleUserNameBlur"
                 v-decorator="['email',{rules: [{ required: true, message: '邮件名称不能为空'}]}]"
                 placeholder="请填写合法邮箱"/>
      </a-form-item>
      <a-form-item label='邮件密码' v-bind="formItemLayout">
        <!--  <a-input style="width: 100%"
                   v-model="notice.title"
                   v-decorator="['keyy',
                     {rules: [
                      { required: true, message: '不能为空'}
                    ]}]"/>-->
        <a-input v-model="data.password"
                 v-decorator="[
          'password',
          {rules: [{ required: true, message: '邮箱密码不能为空' }]}
        ]"
                 type="password"
        />
      </a-form-item>
      <a-form-item label='是否启用' v-bind="formItemLayout">
        <a-radio-group
          v-model="data.active"
          v-decorator="['active',{rules: [{ required: true, message: '请选择是否启用'}]}]">
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
    labelCol: {span: 3},
    wrapperCol: {span: 18}
  }
  export default {
    name: 'MailAdd',
    props: {
      mailAddVisiable: {
        default: false
      }
    },
    data() {
      return {
        loading: false,
        formItemLayout,
        form: this.$form.createForm(this),
        data: {}
      }
    },
    methods: {
      reset() {
        this.loading = false
        this.data = {}
        this.form.resetFields()
      },
      onClose() {
        this.reset()
        this.$emit('close')
      },
      handleSubmit() {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            this.$post('mail', {
              ...this.data
            }).then(() => {
              this.reset()
              this.loading = false
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        })
      },
      handleUserNameBlur() {
        let mail = this.data.email.trim();
        if (mail.length) {
          if (mail.length > 50) {
            this.validateStatus = 'error'
            this.help = '邮箱名称不能超过50个字符'
          } else if (mail.length < 2) {
            this.validateStatus = 'error'
            this.help = '邮箱名称不能少于2个字符'
          } else if (mail.charAt('@') === null) {
            this.validateStatus = 'error'
            this.help = '请输入正确的邮箱'
          } else if (mail.indexOf('@qq.com') === -1) {
            this.validateStatus = 'error';
            this.help = '请输入正确的公司企业邮箱'
          } else {
            this.validateStatus = 'validating'
            this.$get(`mail/check/${mail}`).then((r) => {
              if (r.data) {
                this.validateStatus = 'success'
                this.help = ''
              } else {
                this.validateStatus = 'error'
                this.help = '抱歉，该邮箱已存在'
              }
            })
          }
        } else {
          this.validateStatus = 'error'
          this.help = '邮箱名称不能为空'
        }
      }
    }
  }
</script>
