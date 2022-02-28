<template>
  <div class="user-layout-reset">
    <a-form ref="formReset" :autoFormCreate="(form)=>{this.form = form}" id="formReset" style="padding: 15px">
      <a-tabs size="large" :tabBarStyle="{textAlign: 'center'}" style="padding: 0 2px;" :activeKey="activeKey"
              @change="handleTabsChange">
        <a-tab-pane tab="密码重置" key="1">
          <a-alert type="error" :closable="true" v-show="error" :message="error" showIcon
                   style="margin-bottom: 24px;"></a-alert>

          <a-form-item
            fieldDecoratorId="name"
            :fieldDecoratorOptions="{rules: [{ required: true, message: '请输入用户名', whitespace: true}]}">
            <a-input size="large" placeholder="请输入用户名">
              <a-icon slot="prefix" type="user"></a-icon>
            </a-input>
          </a-form-item>

          <a-popover placement="rightTop" trigger="click" :visible="state.passwordLevelChecked">
            <template slot="content">
              <div :style="{ width: '240px' }">
                <div :class="['user-reset', passwordLevelClass]">强度：<span>{{ passwordLevelName }}</span></div>
                <a-progress :percent="state.percent" :showInfo="false" :strokeColor=" passwordLevelColor "/>
                <div style="margin-top: 10px;font-size:14px">
                  <span>请至少输入 6 个字符。</span>
                </div>
              </div>
            </template>
            <a-form-item
              fieldDecoratorId="password"
              :fieldDecoratorOptions="{rules: [{ required: true, message: '至少6位密码'}, { validator: this.handlePasswordLevel }], validateTrigger: ['change', 'blur']}">
              <a-input size="large" type="password" @click="handlePasswordInputClick" autocomplete="false"
                       placeholder="至少6位密码" style="font-size:14px"></a-input>
            </a-form-item>
          </a-popover>

          <a-form-item
            fieldDecoratorId="password2"
            :fieldDecoratorOptions="{rules: [{ required: true, message: '至少6位密码' }, { validator: this.handlePasswordCheck }], validateTrigger: ['change', 'blur']}">

            <a-input size="large" type="password" autocomplete="false" placeholder="确认密码"
                     style="font-size:14px"></a-input>
          </a-form-item>


          <a-row :gutter="0">
            <a-col :span="14">
              <a-form-item
                fieldDecoratorId="inputCode"
                :fieldDecoratorOptions=validatorRules.inputCode>
                <a-input
                  size="large"
                  type="text"
                  placeholder="请输入验证码">
                  <a-icon slot="prefix" type="mail"/>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :span="10">
              <a-button style="width: 100%" class="captcha-button" size="large" @click="getVertifyCode">获取验证码</a-button>
            </a-col>
          </a-row>

          <a-form-item>
            <a-button
              size="large"
              type="primary"
              shape="circle"
              htmlType="submit"
              class="reset-button"
              style="font-size: 14px;width: 50%;"
              :loading="resetBtn"
              @click.stop.prevent="doReset"
              :disabled="resetBtn">重置密码
            </a-button>
            <a class="loginStyle" @click="returnLogin" style="float: right;line-height: 40px;">使用已有账户登录</a>
          </a-form-item>
        </a-tab-pane>
      </a-tabs>
    </a-form>
  </div>
</template>
<script>
  import {mapMutations} from 'vuex'

  const levelNames = {
    0: '低',
    1: '低',
    2: '中',
    3: '强'
  }
  const levelClass = {
    0: 'error',
    1: 'error',
    2: 'warning',
    3: 'success'
  }
  const levelColor = {
    0: '#ff0000',
    1: '#ff0000',
    2: '#ff7e05',
    3: '#52c41a'
  }

  export default {
    name: 'Reset',
    components: {},
    data() {
      return {
        loading: false,
        error: '',
        activeKey: '1',
        validatorRules: {
          username: {rules: [{required: true, message: '请输入账户名', whitespace: true}]},
          password: {rules: [{required: true, message: '请输入密码', whitespace: true}]},
          inputCode: {rules: [{required: true, message: '请输入验证码', whitespace: true}]}
        },
        state: {
          time: 60,
          smsSendBtn: false,
          passwordLevel: 0,
          passwordLevelChecked: false,
          percent: 10,
          progressColor: '#FF0000'
        },
        form: this.$form.createForm(this),
        resetBtn: false
      }
    },
    computed: {
      passwordLevelClass() {
        return levelClass[this.state.passwordLevel]
      },
      passwordLevelName() {
        return levelNames[this.state.passwordLevel]
      },
      passwordLevelColor() {
        return levelColor[this.state.passwordLevel]
      }
    },
    methods: {
      handleTabsChange(val) {
        this.activeKey = val
      },
      doReset() {
        if (this.activeKey === '1') {
          // 用户名密码登录
          this.form.validateFields(['name', 'password', 'inputCode'], {force: true}, (errors, values) => {
            if (!errors) {
              this.loading = true
              let name = this.form.getFieldValue('name')
              let password = this.form.getFieldValue('password')
              let code = this.form.getFieldValue('inputCode')
              this.$post('user/reset', {
                username: name,
                password: password,
                code: code
              }).then((r) => {
                let result = r.data.data
                if (result) {
                  this.$message.success('重置密码成功')
                  this.returnLogin()
                }else{
                  this.$message.error('重置密码失败')
                  this.returnReset()
                }
              }).catch(() => {
                this.$message.error('重置密码失败')
                this.returnReset()
              })
            }
          })
        }
      },
      getVertifyCode() {
        let name = this.form.getFieldValue('name')
        this.loading = true
        this.$get(`mail/` + name).then((r) => {
          this.$message.success("邮箱验证码已发送")
          this.loading = false
        }).catch((e) => {
          this.$message.error("次数超出限制，请稍后重试")
        })
      },
      isMobile() {
        return this.$store.state.setting.isMobile
      },
      handlePasswordLevel(rule, value, callback) {
        let level = 0

        // 判断这个字符串中有没有数字
        if (/[0-9]/.test(value)) {
          level++
        }
        // 判断字符串中有没有字母
        if (/[a-zA-Z]/.test(value)) {
          level++
        }
        // 判断字符串中有没有特殊符号
        if (/[^0-9a-zA-Z_]/.test(value)) {
          level++
        }
        this.state.passwordLevel = level
        this.state.percent = level * 30
        if (level >= 2) {
          if (level >= 3) {
            this.state.percent = 100
          }
          callback()
        } else {
          if (level === 0) {
            this.state.percent = 10
          }
          callback(new Error('数字、字母、符号需要至少包含两个'))
        }
      },

      handlePasswordCheck(rule, value, callback) {
        let password = this.form.getFieldValue('password')
        if (value === undefined) {
          callback(new Error('请输入密码'))
        }
        if (value && password && value.trim() !== password.trim()) {
          callback(new Error('两次密码不一致'))
        }
        callback()
      },
      handlePasswordInputClick() {
        if (!this.isMobile()) {
          this.state.passwordLevelChecked = true
          return
        }
        this.state.passwordLevelChecked = false
      },
      returnLogin() {
        this.$emit('reset', 'Login')
      },
      returnReset() {
        this.$emit('reset', 'Reset')
      }
    }
  }
</script>

<style lang="less">
  .user-reset {
    &.error {
      color: #ff0000;
    }

    &.warning {
      color: #ff7e05;
    }

    &.success {
      color: #52c41a;
    }
  }

  .user-layout-reset {
    .ant-input-group-addon {
      &:first-child {
        background-color: #fff;
      }
    }

    & > h3 {
      font-size: 14px;
      margin-bottom: 20px;
    }

    .reset-button {
      width: 50%;
    }

    .loginStyle {
      float: right;
      line-height: 40px;
    }
  }
</style>
