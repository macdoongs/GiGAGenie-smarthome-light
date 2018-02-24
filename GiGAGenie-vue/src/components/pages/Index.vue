<template>
  <div>
    <center>
      <Button v-bind:buttonId='0' v-bind:value="'음성 인식 종료'" class="button-no" v-on:click_0='stopTTS()'></Button>
      <br>
      <br>
      <br>
      <Button v-bind:buttonId='1' v-bind:value='"음성 인식 시작"' class="button-yes" v-on:click_1='control()'></Button>
      <br>
      <br>
      <br>
      <br>
      <!--<input v-model="wid" placeholder="테스트 ID">-->
      <br>
      <br>
      <br>
      <input v-model="inputText" placeholder="여기를 수정해보세요" autofocus>
      <br>
      <br>
      <br>
      <Button v-bind:buttonId='2' v-bind:value='"전송"' class="button-off" v-on:click_2='interactWatson(inputText)'></Button>
      <br>
      <br>
      <span style="font-size:50px; font-color:white;">{{inputText}}</span>
      <br>
      <br>
      <br>
      <span style="font-size:50px; font-color:white;">{{outputText}}</span>
      <br>
      <br>
      <br>
    </center>
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
  beforeMount: function () {
    this.wid = this.randomString()
  },
  mounted : function () {
    this.$nextTick(function () {
      this.init()
    })
  },
  data () {
    return {
      options: { },
      toggleId: 0,
      check: false,
      inputText: "",
      outputText: "",
      wid: "test"
    }
  },
  methods: {
    init: function () {
      this.options = { }

      this.options.apikey = this.apiKey
      this.options.keytype = this.keyType
      // console.log('init');
      // alert('init')

      gigagenie.init(this.options, (function (code, message, extra) {
        if (code === 200) {
          alert('Initialize Success')
          // console.log('Initialize Success')
          this.interactWatson(this.inputText)
        } else {
          alert('Initialize Failure')
          // console.log('Initialize Failure')
        }
      }).bind(this))
    },
    control: function () {
      alert('음성인식 시작')

      var options = { }
      // options.voicelanguage = 1

      gigagenie.voice.getVoiceText(options, (function (code, message, extra) {
        if (code === 200) {
          this.inputText = extra.voicetext;
          alert('command : ' + extra.voicetext)
          this.interactWatson(extra.voicetext)
        } else {
          alert('다시해보세요')
        }
      }).bind(this))
    },
    interactWatson: function (inputText) {
      this.outputText = "watson..."

      const wid = this.wid
      const baseURI = this.baseURI

      var body = { }

      body.inputText = inputText

      this.$http.post(
        `${baseURI}/api/watson/${wid}`,
        body
      )
      .then((response) => {
        this.outputText = response.data
        this.inputText = ""

        var options = {}
        options.ttstext = response.data

        gigagenie.voice.sendTTS(options, (function(code, message, extra){
          if (code === 200) {
            this.control()
          } else {
            alert('잘 못 읽겠어요..')
          }
        }).bind(this))
      })
      .catch((error) => {
        console.log(error)
        console.log(error.stack)
        alert(error)
        // alert(error.stack)
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
        // console.log(response.data)
        // alert(response.data.message)
      })
      .catch((error) => {
        // console.log(error)
        // console.log(error.stack)
        alert(error)
        // alert(error.stack)
      })
    },
    randomString: function () {
      var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      var num = 8;

      var text = ""
      for( var i=0; i < num; i++ ) {
          text += possible.charAt(Math.floor(Math.random() * possible.length))
      }

      return text;
    }
  },
  computed: {
    apiKey: function () {
      return this.$store.getters.apiKey
    },
    keyType: function () {
      return this.$store.getters.keyType
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
