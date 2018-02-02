
function makeGatewayURL (ip, port, version) {
  const URL = "http://" + ip + ":" + port + "/" + version

  return URL;
}

module.exports = makeGatewayURL;
