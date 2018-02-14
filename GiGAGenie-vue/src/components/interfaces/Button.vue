<template>
  <div class="button" v-on:click="$emit('click_' + buttonId)">{{value}}</div>
</template>

<script>
export default {
  name: 'Button',
  props: {
    buttonId: {
      type: Number,
      validator: function (value) {
        // console.log(value)
        return true
      }
    },
    value: {
      type: String,
      validator: function (value) {
        // console.log(value)
        return true
      }
    },
    type: {
      type: String,
      validator: function (value) {
        // console.log(value)
        return true
      }
    }
  },
  beforeMounted: function () {
  },
  data () {
    return {
    }
  },
  computed: {
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.button {
  border: none;
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  border-radius: 4px;
}
</style>
