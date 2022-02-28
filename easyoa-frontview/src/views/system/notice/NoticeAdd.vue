<template>
  <a-drawer
    title="新增公告"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="noticeAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input style="width: 100%"
                        v-model="notice.title"
                        v-decorator="['keyy',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='内容' v-bind="formItemLayout">
        <a-textarea
          :rows="4"
          placeholder="请填写公告内容(250字以内)"
          v-model="notice.msgContent"
          v-decorator="[
          'msgContent',
          {rules: [
            { required:true, max: 250, message: '长度不能超过250个字符'}
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
    labelCol: { span: 3 },
    wrapperCol: { span: 18 }
  }
  export default {
    name: 'NoticeAdd',
    props: {
      noticeAddVisiable: {
        default: false
      }
    },
    data () {
      return {
        loading: false,
        formItemLayout,
        form: this.$form.createForm(this),
        notice: {}
      }
    },
    methods: {
      reset () {
        this.loading = false
        this.notice = {}
        this.form.resetFields()
      },
      onClose () {
        this.reset()
        this.$emit('close')
      },
      handleSubmit () {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.loading=true
            this.$post('notice', {
              ...this.notice
            }).then(() => {
              this.reset()
              this.loading=false
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        })
      }
    }
  }
</script>
