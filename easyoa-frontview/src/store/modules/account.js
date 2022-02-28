import db from 'utils/localstorage'

export default {
  namespaced: true,
  state: {
    token: db.get('USER_TOKEN'),
    expireTime: db.get('EXPIRE_TIME'),
    user: db.get('USER'),
    permissions: db.get('PERMISSIONS'),
    roles: db.get('ROLES'),
    apply: db.get('APPLY_INFO')
  },
  mutations: {
    setToken (state, val) {
      db.save('USER_TOKEN', val)
      state.token = val
    },
    setExpireTime (state, val) {
      db.save('EXPIRE_TIME', val)
      state.expireTime = val
    },
    setUser (state, val) {
      db.save('USER', val)
      state.user = val
    },
    setPermissions (state, val) {
      db.save('PERMISSIONS', val)
      state.permissions = val
    },
    setRoles (state, val) {
      db.save('ROLES', val)
      state.roles = val
    },
    setApply(state,val){
      db.save("APPLY_INFO",val)
      state.apply = val
    }
  }
}
