<template>
  <a-drawer
    title="修改申请"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="applicationEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='申请ID'
                   v-bind="formItemLayout">
        <a-input readOnly v-decorator="['applicationId']" :disabled="true"/>
      </a-form-item>
      <a-form-item label='申请人'
                   v-bind="formItemLayout">
        <a-input readOnly v-decorator="['userName']" :disabled="true"/>
      </a-form-item>
      <a-form-item label='所属部门'
                   v-bind="formItemLayout">
        <a-input readOnly v-decorator="['deptName']" :disabled="true"/>
      </a-form-item>
      <a-form-item label='所属职位'
                   v-bind="formItemLayout">
        <a-input readOnly v-decorator="['position']" :disabled="true"/>
      </a-form-item>
      <a-form-item label='请假类别'
                   v-bind="formItemLayout">
        <a-select
          mode="default"
          notFoundContent="暂无类别"
          style="width: 100%"
          v-bind="formItemLayout"
          v-decorator="['leaveType',{rules: [{ required: true, message: '请选择申请类别' }]}]">
          <a-select-option v-for="a in applyTypes" :key="a">{{a}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label='请假时长'
                   v-bind="formItemLayout">
        <a-input v-decorator="['days']"></a-input>
      </a-form-item>
      <a-form-item label='请假时间'
                   v-bind="formItemLayout">
        <a-range-picker
          :value="dateRange"
          :showTime="{ format: 'HH:mm' }"
          :format="dateFormat"
          :placeholder="['开始时间', '结束时间']"
          @change="onDateChange"
          @ok="onOk"
        />
      </a-form-item>
      <a-form-item label='请假原因'
                   v-bind="formItemLayout">
        <a-input readOnly v-decorator="['leaveReason']" :disabled="true"/>
      </a-form-item>
      <a-form-item label='申请状态'
                   v-bind="formItemLayout">
        <a-input readOnly v-decorator="['status']" :disabled="true"/>
      </a-form-item>
      <a-form-item label='申请阶段'
                   v-bind="formItemLayout">
        <a-input readOnly v-decorator="['stage']" :disabled="true"/>
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
  import moment from 'moment';
  import {mapState, mapMutations} from 'vuex'

  const formItemLayout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 19 }
  }
  export default {
    name: 'ApplicationEdit',
    components: {moment},
    props: {
      applicationEditVisiable: {
        require: true,
        default: false
      },
      applicationEditData: {
        require: true
      }
    },
    data() {
      return {
        formItemLayout,
        form: this.$form.createForm(this),
        items: [],
        applyTypes: [],
        loading: false,
        dateRange: [],
        dateFormat: 'YYYY-MM-DD HH:mm',
      }
    },
    methods: {
      onClose () {
        this.loading = false
        this.form.resetFields()
        this.$emit('close')
      },
      onDateChange(value, dateString) {
        this.form.startTime = dateString[0]
        this.form.endTime = dateString[1]
      },
      onOk(value) {
      },
      setFormValues({...application}) {
        let fields = ['applicationId', 'userName', 'deptName', 'position', 'leaveType', 'days', 'leaveReason', 'status', 'stage']
        Object.keys(application).forEach((key) => {
          if (fields.indexOf(key) !== -1) {
            this.form.getFieldDecorator(key)
            let obj = {}
            obj[key] = application[key]
            this.form.setFieldsValue(obj)
          }
        })

        this.dateRange[0] = moment(application.startTime, this.dateFormat)
        this.dateRange[1] = moment(application.endTime, this.dateFormat)
      },
      handleSubmit() {
        this.form.validateFields((err, values) => {
          if (!err) {
            let applicationVO = this.form.getFieldsValue()
            if(this.form.startTime){
              applicationVO.startTime = moment(this.form.startTime,this.dateFormat).format('YYYY-MM-DD HH:mm:ss')
            }
          if(this.form.endTime){
            applicationVO.endTime = moment(this.form.endTime,this.dateFormat).format('YYYY-MM-DD HH:mm:ss')
          }
            this.$put(`apply/update`, {...applicationVO})
              .then((r) => {
                this.loading = false
                this.$emit('success')
              }).catch((e) => {
              this.loading = false
              this.$message.error('修改申请失败')
            })
          }
        })
      },
         ...mapMutations({
        setUser: 'account/setUser'
      }),
    },
    watch: {
      applicationInfoVisiable() {
        if (this.applicationInfoVisiable) {
          this.$get(`flow/${this.applicationInfoData.applicationId}`)
            .then((r) => {
              this.items = r.data.data
            }).catch((e) => {

          })
        }
      }
    },
     computed: {
      ...mapState({
        currentUser: state => state.account.user
      })
    },
    mounted() {
      this.$get(`apply/types/${this.currentUser.userId}`).then((r) => {
        this.applyTypes = r.data
      })
    }
  }
</script>
