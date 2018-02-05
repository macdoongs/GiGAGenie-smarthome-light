<template>
  <div>
    <center>
      <input type='submit' value='STOP TTS' class='button button-no' v-on:click='stopTTS()'>
      <br>
      <br>
      <br>
      <input type='submit' value='START TTS' class='button button-yes' v-on:click='control()'>
      <br>
      <br>
      <br>
      <input type='submit' value='Power on' class='button button-on' v-on:click='handlePower("on")'>
      <br>
      <br>
      <br>
      <input type='submit' value='Power off' class='button button-off' v-on:click='handlePower("off")'>
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
export default {
  name: 'Index',
  data () {
    return {
      options: { }
    }
  },
  methods: {
    init: function () {
      this.options = { }

      this.options.apikey = 'RTUwMDEwNzR8R0JPWERFVk18MTUxNTAzMjA5MTAxNQ=='
      this.options.keytype = 'GBOXDEVM'
      // this.options.keytype='GBOXCOMM'

      gigagenie.init(this.options, function (code, message, extra) {
        if (code === 200) {
          console.log('Initialize Success')
        }
      })
    },
    control: function () {
      var options = { }
      options.voicelanguage = 1

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
    handlePower: function (onoff) {
      alert('turn ' + onoff + ' the light')

      const baseURI = this.baseURI

      var body = { }
      body.onoff = onoff

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
    baseURI: function () {
      return this.$store.getters.baseURI
    }
  }
}
</script>

<!-- Add 'scoped' attribute to limit CSS to this component only -->
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

</style>
