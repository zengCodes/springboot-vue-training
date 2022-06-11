<template>
  <ChatApp :account="account" :isAdmin="isAdmin" :userId="userId"></ChatApp>
</template>

<script>
import ChatApp from '@/components/chat/app'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      account: undefined,
      isAdmin: false,
    }
  },
  created() {
    this.account = this.userName
    this.userInfo.roles.forEach(item => {
      this.isAdmin = item.match(new RegExp(/admin/)) ? true : false
    })
  },
  computed: {
    ...mapState({
      userInfo: state => state.user,
    }),
    userName() {
      return this.userInfo.user.userName
    },
    userId() {
      return this.userInfo.user.userId
    },
  },
  components: {
    ChatApp,
  },
}
</script>
<style lang="scss" scoped></style>
