<template>
  <div class="toggle-switch">
    <label class="switch" >
      <input v-if="check" id="check" name="check" type="checkbox" v-on:click="$emit('toggle_' + toggleId)" checked>
      <input v-else id="check" name="check" type="checkbox" v-on:click="$emit('toggle_' + toggleId)">
      <div class="slider round"></div>
    </label>
  </div>
</template>

<script>
export default {
  name: 'toggle-switch',
  props: {
    toggleId: {
      type: Number,
      validator: function (value) {
        console.log(value)
        return true
      }
    },
    check: {
      type: Boolean,
      validator: function (value) {
        console.log(value)
        return true
      }
    }
  },
  data () {
    return {
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 28px;
}

.switch input {display:none;}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 30px;
}

.slider.round:before {
  border-radius: 50%;
}
</style>
