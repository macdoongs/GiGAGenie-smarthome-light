var options={};

function init(){
  options={};

  options.apikey="RTUwMDEwNzR8R0JPWERFVk18MTUxNTAzMjA5MTAxNQ==";
  options.keytype="GBOXDEVM";
  //options.keytype="GBOXCOMM";

  gigagenie.init(options, function(code, message, extra){
    if(code == 200){
      console.log("Initialize Success");
    }
  });
}

gigagenie.voice.onRequestClose = function(){
  options = {};

  gigagenie.voice.svcFinished(options, function(code, message,extra){

  });
};

function control(){
    var options={};
    options.voicelanguage = 1;

    gigagenie.voice.getVoiceText(options, function(code, message, extra){
      if(code == 200){
        alert("command : " + extra.voicetext);
      }else{
        alert("다시해보세요");
      }
  });
}


gigagenie.media.onMuteRequest = function(extra){
  if(extra){
    console.log("Mute!");
  }else{
    console.log("unMute!");
  }
};

function stopTTS(){
  alert("음성인식 중단 요청");

  var options={};

  gigagenie.voice.stopTTS(options, function(code, message, extra){
    if(code == 200){
      alert("음성인식 중단 성공");
    }else if (code == 404){
      alert("음성인식 실행 중이 아님");
    }else{
      alert("음성인식 중단 실패: " + message);
    }
  });
}

// TODO modify function
// TEST request
function handlePower(onoff){
  alert("turn " + onoff + " the light");

  var body = {};

  body.onoff = onoff;

  var httpRequest;
  if (window.XMLHttpRequest) { // 모질라, 사파리등 그외 브라우저, ...
      httpRequest = new XMLHttpRequest();
  } else if (window.ActiveXObject) { // IE 8 이상
      httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
  }
  httpRequest.onreadystatechange = function () {
      if (httpRequest.readyState == 4 && httpRequest.status == 200) {
        // Response data
        var json = JSON.parse(httpRequest.responseText);

        alert(httpRequest.responseText);
      }
  };

  httpRequest.open('PUT', location.origin + '/api/device/4/light', true);
  httpRequest.setRequestHeader("Content-type", "application/json");
  httpRequest.send(JSON.stringify(body));
}
