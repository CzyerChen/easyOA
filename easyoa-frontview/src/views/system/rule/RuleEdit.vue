<template>
  <a-drawer
    title="修改规则"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="ruleEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='规则名称' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['ruleName']"/>
      </a-form-item>
      <a-form-item label='最大允许请假时长' v-bind="formItemLayout">
        <a-input v-decorator="['maxPermitDay']"/>
      </a-form-item>
      <a-form-item label='最小请假天数' v-bind="formItemLayout">
        <a-input v-decorator="['leaveDaysFrom']"/>
      </a-form-item>
      <a-form-item label="最大请假天数(不包含)" v-bind="formItemLayout">
        <a-input v-decorator="['leaveDaysTo']"/>
      </a-form-item>
      <a-form-item label="提前请假时间" v-bind="formItemLayout">
        <a-input v-decorator="['forwardDays']"/>
      </a-form-item>

      <a-form-item label="年龄要求" v-bind="formItemLayout">
        <a-input v-decorator="['age']"/>
      </a-form-item>
      <a-form-item label="最小工龄" v-bind="formItemLayout">
        <a-input v-decorator="['workYearsFrom']"/>
      </a-form-item>
      <a-form-item label="最大工龄（包含）" v-bind="formItemLayout">
        <a-input v-decorator="['workYearsTo']"/>
      </a-form-item>
      <a-form-item label='需要证明文件' v-bind="formItemLayout">
        <a-radio-group
          v-decorator="[
            'needUpload',
            {rules: [{ required: true, message: '请选择是否需要证明文件' }]}
          ]">
          <a-radio :value="true">是</a-radio>
          <a-radio :value="false">否</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='证明文件要求' v-bind="formItemLayout">
        <a-textarea
          :rows="4"
          v-model="this.rule.fileRequired"
          v-decorator="[
          'fileRequired',
          {rules: [
            { max: 100, message: '长度不能超过100个字符'}
          ]}]">
        </a-textarea>
      </a-form-item>
    <a-form-item label='说明' v-bind="formItemLayout">
        <a-textarea
          :rows="4"
          v-model="this.rule.notice"
          v-decorator="[
          'notice',
          {rules: [
            { max: 100, message: '长度不能超过100个字符'}
          ]}]">
        </a-textarea>
      </a-form-item>
      <a-form-item label='创建时间' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['createTime']"/>
      </a-form-item>
      <a-form-item label='更新时间' v-bind="formItemLayout">
        <a-input readOnly v-decorator="['updateTime']"/>
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
    labelCol: {span: 6},
    wrapperCol: {span: 15}
  }
  export default {
    name: 'RuleEdit',
    props: {
      ruleEditVisiable: {
        default: false
      },
      ruleInfoData: {
        require: true
      }
    },
    data() {
      return {
        loading: false,
        formItemLayout,
        form: this.$form.createForm(this),
        radioValue: '',
        rule:{}
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
      setFormValues({...rule}) {
        let fields = ['ruleName', 'maxPermitDay', 'leaveDaysFrom', 'leaveDaysTo',
          'forwardDays', 'age', 'workYearsFrom', 'workYearsTo', 'needUpload','fileRequired','notice', 'createTime', 'updateTime']
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
      handleSubmit() {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            let rule = this.form.getFieldsValue()
            rule.ruleId = this.ruleInfoData.ruleId
            rule.notice = this.rule.notice
            rule.fileRequired = this.rule.fileRequired
            this.$put(`rule`, {
              ...rule
            }).then((r) => {
              this.reset()
              this.$emit('success')
              this.loading = false
            }).catch(() => {
              this.loading = false
            })
          }
        })
      },
      onChangeRadio(event){
        console.log('radio checked', event.target.value)
        this.radioValue=event.target.value
      }
    },
    watch: {
      ruleEditVisiable() {
      }
    }
  }
</script>
