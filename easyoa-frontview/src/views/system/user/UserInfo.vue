<template>
  <a-modal
    v-model="show"
    :centered="true"
    :keyboard="false"
    :footer="null"
    :width="750"
    @cancel="handleCancleClick"
    title="用户信息">
    <a-layout class="user-info">
      <a-layout-sider class="user-info-side">
        <a-avatar shape="square" :size="100" icon="user" :src="`static/avatar/${userInfoData.avatar}`"/>
      </a-layout-sider>
      <a-layout-content class="user-content-one">
        <p><a-icon type="user"/>账户：{{userInfoData.userName}}</p>
        <p :title="userInfoData.roleName"><a-icon type="star"/>角色：{{userInfoData.roleName? userInfoData.roleName: '暂无角色'}}</p>
        <p><a-icon type="skin"/>性别：{{ssex}}</p>
        <p><a-icon type="phone"/>电话：{{userInfoData.phone ? userInfoData.phone : '暂未绑定电话'}}</p>
        <p><a-icon type="mail"/>邮箱：{{userInfoData.email ? userInfoData.email : '暂未绑定邮箱'}}</p>
      </a-layout-content>
      <a-layout-content class="user-content-two">
        <p><a-icon type="home"/>部门：{{userInfoData.deptName ? userInfoData.deptName : '暂无部门信息'}}</p>
        <p>
          <a-icon type="smile" v-if="userInfoData.status === 1"/>
          <a-icon type="frown" v-else/>状态：
          <template v-if="userInfoData.status === 0">
            <a-tag color="red">锁定</a-tag>
          </template>
          <template v-else-if="userInfoData.status === 1">
            <a-tag color="cyan">有效</a-tag>
          </template>
          <template v-else>
            {{userInfoData.status}}
          </template>
        </p>
        <p><a-icon type="clock-circle"/>创建时间：{{userInfoData.createTime}}</p>
        <p><a-icon type="login" />最近登录：{{userInfoData.lastLoginTime}}</p>
        <p :title="userInfoData.description"><a-icon type="message"/>描述：{{userInfoData.description}}</p>
      </a-layout-content>
    </a-layout>
  </a-modal>
</template>
<script>
export default {
  name: 'UserInfo',
  props: {
    userInfoVisiable: {
      require: true,
      default: false
    },
    userInfoData: {
      require: true
    }
  },
  computed: {
    show: {
      get: function () {
        return this.userInfoVisiable
      },
      set: function () {
      }
    },
    ssex () {
      switch (this.userInfoData.sex) {
        case 'M':
          return '男'
        case 'F':
          return '女'
        case 'U':
          return '保密'
        default:
          return this.userInfoData.sex
      }
    }
  },
  methods: {
    handleCancleClick () {
      this.$emit('close')
    }
  }
}
</script>
<style lang="less" scoped>
@import "UserInfo";
</style>
