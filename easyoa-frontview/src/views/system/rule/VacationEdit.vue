<template>
  <a-drawer
    title="修改节日"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="vacationEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='节日名称' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['name']"/>
      </a-form-item>
      <a-form-item label='节日日期' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['festival']"/>
      </a-form-item>
      <a-form-item label='放假天数' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['days']"/>
      </a-form-item>
      <a-form-item label="开始日期" v-bind="formItemLayout">
        <a-input
          v-decorator="['startDate', {rules: [
            { pattern: '^(?:(?!0000)[0-9]{4}([-])(?:(?:0?[1-9]|1[0-2])([-])(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])([-])(?:29|30)|(?:0?[13578]|1[02])([-])31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-])0?2([-/.]?)29)$', message: '请输入正确的日期：YYYY-M-D'}
          ]}]"/>
      </a-form-item>
      <a-form-item label="结束日期" v-bind="formItemLayout">
        <a-input
          v-decorator="['endDate', {rules: [
            { pattern: '^(?:(?!0000)[0-9]{4}([-])(?:(?:0?[1-9]|1[0-2])([-])(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])([-])(?:29|30)|(?:0?[13578]|1[02])([-])31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-])0?2([-/.]?)29)$', message: '请输入正确的日期：YYYY-M-D'}
          ]}]"/>
      </a-form-item>
      <a-form-item label='具体日期' v-bind="formItemLayout">
        <a-textarea
          :rows="4"
          v-decorator="[
          'detail',
          {rules: [
            { max: 100, message: '长度不能超过100个字符'}
          ]}]">
        </a-textarea>
      </a-form-item>
      <a-form-item label='放假说明' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['description']"/>
      </a-form-item>
      <a-form-item label='建议' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['advice']"/>
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
    name: 'VacationEdit',
    props: {
      vacationEditVisiable: {
        default: false
      },
      vacationInfoData: {
        require: true
      }
    },
    data() {
      return {
        loading: false,
        formItemLayout,
        form: this.$form.createForm(this),
      }
    },
    methods: {
      reset() {
        this.loading = false
        this.form.resetFields()
      },
      onClose() {
        this.reset()
        this.$emit('close')
      },
      setFormValues({...vacation}) {
        let fields = ['vacationId','name', 'festival', 'days', 'startDate', 'endDate', 'detail', 'description', 'advice']
        Object.keys(vacation).forEach((key) => {
          if (fields.indexOf(key) !== -1) {
            this.form.getFieldDecorator(key)
            let obj = {}
            obj[key] = vacation[key]
            this.form.setFieldsValue(obj)
          }
        })
      },
      handleSubmit() {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            let vacation = this.form.getFieldsValue()
            this.$put(`vacation/${this.vacationInfoData.vacationId}`, {
              ...vacation
            }).then((r) => {
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
      vacationEditVisiable() {
        /* if (this.roleEditVisiable) {
           this.$get('menu').then((r) => {
             this.menuTreeData = r.data.rows.children
             this.allTreeKeys = r.data.ids
             this.$get('role/menu/' + this.roleInfoData.roleId).then((r) => {
               this.defaultCheckedKeys.splice(0, this.defaultCheckedKeys.length, r.data)
               this.checkedKeys = r.data
               this.expandedKeys = r.data
               this.menuTreeKey = +new Date()
             })
           })
         }*/
      }
    }
  }
</script>
