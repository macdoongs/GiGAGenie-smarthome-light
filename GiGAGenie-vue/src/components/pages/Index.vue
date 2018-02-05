<template>
  <div>
    <center>
      <Button v-bind:buttonId='0' v-bind:value="'STOP TTS'" class="button-yes" v-on:click_0='stopTTS()'></Button>
      <br>
      <br>
      <br>
      <Button v-bind:buttonId='1' v-bind:value='"START TTS"' class="button-no" v-on:click_1='control()'></Button>
      <br>
      <br>
      <br>
      <div class='power-toggle'>
        <div class='toggle-title'>조명 전원</div>
        <ToggleSwitch v-bind:toggleId='toggleId' v-bind:check='check' v-on:toggle_0='toggle()'></ToggleSwitch>
        <div>{{onOff}}</div>
      </div>
    </center>
    <!-- Simple audio playback -->
    <audio src='http://developer.mozilla.org/@api/deki/files/2926/=AudioTest_(1).ogg' autoplay>
      Your browser does not support the
      <code>audio</code>
      element.
    </audio>
  </div>
</template>

<script>
/* eslint-disable */
import Button from '@/components/interfaces/Button'
import ToggleSwitch from '@/components/interfaces/ToggleSwitch'
export default {
  name: 'Index',
  components: {
    Button,
    ToggleSwitch
  },
  mounted: function () {
    // TODO modify API request
    if (this.onOff == 'on') {
      this.check = true
    } else {
      this.check = false
    }
  },
  data () {
    return {
      options: { },
      toggleId: 0,
      check: false
    }
  },
  methods: {
    init: function () {
      this.options = { }

      this.options.apikey = this.apiKey
      this.options.keytype = this.keyType

      gigagenie.init(this.options, function (code, message, extra) {
        if (code === 200) {
          console.log('Initialize Success')
        }
      })
    },
    control: function () {
      alert('음성인식 시작')

      var options = { }
      // options.voicelanguage = 1

      gigagenie.voice.getVoiceText(options, function (code, message, extra) {
        if (code === 200) {
          alert('command : ' + extra.voicetext)
        } else {
          alert('다시해보세요')
        }
      })
    },
    stopTTS: function () {
      alert('음성인식 중단 요청')

      var options = { }

      gigagenie.voice.stopTTS(options, function (code, message, extra) {
        if (code === 200) {
          alert('음성인식 중단 성공')
        } else if (code === 404) {
          alert('음성인식 실행 중이 아님')
        } else {
          alert('음성인식 중단 실패: ' + message)
        }
      })
    },
    toggle: function () {
      this.$store.dispatch('togglePower')
      this.handlePower()
    },
    handlePower: function () {
      alert('turn ' + this.onOff + ' the light')

      const baseURI = this.baseURI

      var body = { }
      body.onoff = this.onOff

      this.$http.put(
        `${baseURI}/api/device/4/light`,
        body
      )
      .then((response) => {
        console.log(response.data)
        alert(response.data.message)
      })
      .catch((error) => {
        console.log(error)
        alert(error)
      })
    }
  },
  computed: {
    apiKey: function () {
      return this.$store.getters.apiKey
    },
    keyType: function () {
      return this.$stroe.getters.keyType
    },
    baseURI: function () {
      return this.$store.getters.baseURI
    },
    onOff: function () {
      return this.$store.getters.onOff
    }
  }
}
</script>

<!-- Add 'scoped' attribute to limit CSS to this component only -->
<style scoped>
.button-yes {
  background-color: #4CAF50; /* Green */
}

.button-no {
  background-color: #f44336; /* Red */
}

.button-on {
  background-color: #e7e7e7; color: black; /* Gray */
}

.button-off {
  background-color: #555555; /* Gray */
}

/* Hoverable Buttons */
.button {
    -webkit-transition-duration: 0.4s; /* Safari */
    transition-duration: 0.4s;
}
.button:hover {
    background-color: #e7e7e7; /* Green */
    color: white;
}
</style>
