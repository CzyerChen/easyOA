<template>
  <a-drawer
    title="新增规则"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="ruleAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='规则名称' v-bind="formItemLayout">
        <a-input v-model="rule.ruleName"
                 v-decorator="['ruleName',
                   {rules: [
                    { required: true, message: '规则名称不能为空'},
                    { max: 10, message: '长度不能超过10个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='最大允许请假时长'
                   v-bind="formItemLayout">
        <a-input v-model="rule.maxPermitDay"
                 v-decorator="['maxPermitDay',
                 {rules: [
                  { required: true, message: '最大允许请假时长不能为空'},
                  { max: 3, message: '长度不能超过3个字符'}
                ]}]"/>
      </a-form-item>
      <a-form-item label='最小请假天数'
                   v-bind="formItemLayout">
        <a-input v-model="rule.leaveDaysFrom"
                 v-decorator="['leaveDaysFrom']"/>
      </a-form-item>
      <a-form-item label='最大请假天数(不包含)'
                   v-bind="formItemLayout">
        <a-input v-model="rule.leaveDaysTo"
                 v-decorator="['leaveDaysTo']"/>
      </a-form-item>
      <a-form-item label='提前请假时间'
                   v-bind="formItemLayout">
        <a-input v-model="rule.forwardDays"
                 v-decorator="['forwardDays']"/>
      </a-form-item>
      <a-form-item label='年龄要求'
                   v-bind="formItemLayout">
        <a-input v-model="rule.age"
                 v-decorator="['age']"/>
      </a-form-item>
      <a-form-item label='最小工龄'
                   v-bind="formItemLayout">
        <a-input v-model="rule.workYearsFrom"
                 v-decorator="['workYearsFrom']"/>
      </a-form-item>
      <a-form-item label='最大工龄（包含）'
                   v-bind="formItemLayout">
        <a-input v-model="rule.workYearsTo"
                 v-decorator="['workYearsTo']"/>
      </a-form-item>
      <a-form-item label='需要证明文件' v-bind="formItemLayout">
        <a-radio-group
          v-model="rule.needUpload"
          v-decorator="[
            'needUpload',
            {rules: [{ required: true, message: '请选择是否需要证明文件' }]}
          ]">
          <a-radio :value="true">是</a-radio>
          <a-radio :value="false">否</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='证明文件要求'
                   v-bind="formItemLayout">
        <a-textarea
          :rows="4"
          v-model="rule.fileRequired"
          v-decorator="[
          'fileRequired',
          {rules: [
            { max: 100, message: '长度不能超过100个字符'}
          ]}]">
        </a-textarea>
      </a-form-item>
      <a-form-item label='说明'
                   v-bind="formItemLayout">
        <a-textarea
          :rows="4"
          v-model="rule.notice"
          v-decorator="[
          'notice',
          {rules: [
            { max: 100, message: '长度不能超过100个字符'}
          ]}]">
        </a-textarea>
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
    labelCol: { span: 6 },
    wrapperCol: { span: 15 }
  }
  export default {
    name: 'RuleAdd',
    props: {
      ruleAddVisiable: {
        default: false
      }
    },
    data () {
      return {
        loading: false,
        formItemLayout,
        form: this.$form.createForm(this),
        rule: {
          ruleName: '',
          maxPermitDay: '',
          leaveDaysFrom: '',
          leaveDaysTo: '',
          forwardDays: '',
          age: '',
          workYearsFrom: '',
          workYearsTo: '',
          needUpload: '',
          fileRequired: '',
          notice: ''
        }
      }
    },
    methods: {
      reset () {
        this.loading = false
        this.rule= {}
        this.form.resetFields()
      },
      onClose () {
        this.reset()
        this.$emit('close')
      },
      handleSubmit () {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            this.$post('rule', {
              ...this.rule
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
      ruleAddVisiable () {
      }
    }
  }
</script>
