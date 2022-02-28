<template>
  <div class="login">
    <a-form @submit.prevent="doLogin" :autoFormCreate="(form) => this.form = form" style="padding: 15px">
      <a-tabs size="large" :tabBarStyle="{textAlign: 'center'}" style="padding: 0 2px;" :activeKey="activeKey"
              @change="handleTabsChange">
        <a-tab-pane tab="账户密码登录" key="1">
          <a-alert type="error" :closable="true" v-show="error" :message="error" showIcon
                   style="margin-bottom: 24px;"></a-alert>
          <a-form-item
            fieldDecoratorId="name"
            :fieldDecoratorOptions="{rules: [{ required: true, message: '请输入账户名', whitespace: true}]}">
            <a-input size="large" placeholder="请输入用户名">
              <a-icon slot="prefix" type="user"></a-icon>
            </a-input>
          </a-form-item>
          <a-form-item
            fieldDecoratorId="password"
            :fieldDecoratorOptions="{rules: [{ required: true, message: '请输入密码', whitespace: true}]}">
            <a-input size="large" type="password" placeholder="请输入密码"
                     v-decorator="['password', {rules: [{ required: true, message: '请输入密码' }]}]">
              <a-icon slot="prefix" type="lock"></a-icon>
            </a-input>
          </a-form-item>
          <a-row :gutter="0">
            <a-col :span="14">
              <a-form-item
                fieldDecoratorId="inputCode"
                :fieldDecoratorOptions=validatorRules.inputCode>
                <a-input
                  size="large"
                  type="text"
                  @change="inputCodeChange"
                  placeholder="请输入验证码">
                  <!-- v-decorator="['inputCode', validatorRules.inputCode]"-->
                  <a-icon slot="prefix" v-if=" inputCodeContent === verifiedCode " type="smile"
                          :style="{ color: 'rgba(0,0,0,.25)' }"/>
                  <a-icon slot="prefix" v-else type="frown" :style="{ color: 'rgba(0,0,0,.25)' }"/>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :span="10">
              <j-graphic-code @success="generateCode" style="float: right"></j-graphic-code>
            </a-col>
          </a-row>
        </a-tab-pane>
        <!-- <a-tab-pane tab="手机号登录" key="2">
           <a-form-item>
             <a-input size="large">
               <a-icon slot="prefix" type="mobile"></a-icon>
             </a-input>
           </a-form-item>
           <a-form-item>
             <a-row :gutter="8" style="margin: 0 -4px">
               <a-col :span="16">
                 <a-input size="large">
                   <a-icon slot="prefix" type="mail"></a-icon>
                 </a-input>
               </a-col>
               <a-col :span="8" style="padding-left: 4px">
                 <a-button style="width: 100%" class="captcha-button" size="large" @click="getCaptcha">获取验证码</a-button>
               </a-col>
             </a-row>
           </a-form-item>
         </a-tab-pane>-->

      </a-tabs>
      <a-form-item>
        <a-button :loading="loading" style="width: 100%; margin-top: 4px" size="large" htmlType="submit" type="primary">
          登录
        </a-button>
      </a-form-item>
      <div>
        <a style="float: right;padding:10px" @click="regist">注册账户</a>
      </div>
      <div>
        <a style="float: left;padding:10px" @click="reset">忘记密码</a>
      </div>
    </a-form>
  </div>
</template>

<script>
  import {mapMutations} from 'vuex'
  import JGraphicCode from '@/components/tool/JGraphicCode'

  export default {
    components: {
      JGraphicCode
    },
    name: 'Login',
    data() {
      return {
        loading: false,
        error: '',
        activeKey: '1',
        validatorRules: {
          username: {rules: [{required: true, message: '请输入账户名', whitespace: true}]},
          password: {rules: [{required: true, message: '请输入密码', whitespace: true}]},
          inputCode: {rules: [{required: true, message: '请输入验证码'}, {validator: this.validateInputCode}]}
        },
        verifiedCode: "",
        inputCodeContent: "",
        inputCodeNull: true,
        validate_status: "",
        form: this.$form.createForm(this)
      }
    },
    computed: {
      systemName() {
        return this.$store.state.setting.systemName
      },
      copyright() {
        return this.$store.state.setting.copyright
      }
    },
    created() {
      this.$db.clear()
      this.$router.options.routes = []
    },
    methods: {
      doLogin() {
        if (this.activeKey === '1') {
          // 用户名密码登录
          this.form.validateFields(['name', 'password', 'inputCode'], {force: true}, (errors, values) => {
            if (!errors) {
              this.loading = true
              let name = this.form.getFieldValue('name')
              let password = this.form.getFieldValue('password')
              this.$post('login', {
                username: name,
                password: password
              }).then((r) => {
                let data = r.data.data
                this.saveLoginData(data)
                setTimeout(() => {
                  this.loading = false
                }, 500)
                this.$router.push('/')
              }).catch(() => {
                setTimeout(() => {
                  this.loading = false
                }, 500)
              })
            }
          })
        }
        if (this.activeKey === '2') {
          // 手机验证码登录
          this.$message.warning('暂未开发')
        }
      },
      regist() {
        this.$emit('regist', 'Regist')
      },
      reset() {
        this.$emit('reset', 'Reset')
      },
      getCaptcha() {
        this.$message.warning('暂未开发')
      },
      handleTabsChange(val) {
        this.activeKey = val
      },
      ...mapMutations({
        setToken: 'account/setToken',
        setExpireTime: 'account/setExpireTime',
        setPermissions: 'account/setPermissions',
        setRoles: 'account/setRoles',
        setUser: 'account/setUser',
        setTheme: 'setting/setTheme',
        setLayout: 'setting/setLayout',
        setMultipage: 'setting/setMultipage',
        fixSiderbar: 'setting/fixSiderbar',
        fixHeader: 'setting/fixHeader',
        setColor: 'setting/setColor'
      }),
      saveLoginData(data) {
        this.setToken(data.token)
        this.setExpireTime(data.exipreTime)
        this.setUser(data.user)
        this.setPermissions(data.permissions)
        this.setRoles(data.roles)
        this.setTheme(data.config.theme)
        this.setLayout(data.config.layout)
        this.setMultipage(data.config.multiPage === '1')
        this.fixSiderbar(data.config.fixSiderbar === '1')
        this.fixHeader(data.config.fixHeader === '1')
        this.setColor(data.config.color)
      },
      validateInputCode(rule, value, callback) {
        if (!value || this.verifiedCode === this.inputCodeContent) {
          console.log("进入validate 正确" + value)
          callback();
        } else {
          callback("您输入的验证码不正确!");
        }
      },
      generateCode(value) {
        this.verifiedCode = value.toLowerCase()
      },
      inputCodeChange(e) {
        this.inputCodeContent = e.target.value
        if (!e.target.value || 0 == e.target.value) {
          this.inputCodeNull = true
        } else {
          this.inputCodeContent = this.inputCodeContent.toLowerCase()
          this.inputCodeNull = false
        }
      }
    }
  }
</script>

<style lang="less" scoped>
  .login {
    border: solid 2px #eceff1;
    background: #e8d3ff2e;
    float: right;
    margin: 50px 200px;

    .icon {
      font-size: 24px;
      color: rgba(0, 0, 0, 0.2);
      margin-left: 16px;
      vertical-align: middle;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: #1890ff;
      }
    }
  }
</style>

<style>
  .valid-error .ant-select-selection__placeholder {
    color: #f5222d;
  }
</style>
