import * as types from '../mutation-types'

const state = {
  apiKey: 'RTUwMDEwNzR8R0JPWERFVk18MTUxNTAzMjA5MTAxNQ==',
  keyType: 'GBOXDEVM',
  // keytype: 'GBOXCOMM',
  baseURI: 'http://localhost:8080',
  onOff: 'off'
}

/* eslint-disable no-shadow */
/* eslint-disable no-param-reassign */
/* eslint-disable no-plusplus */

const mutations = {
  [types.TOGGLE_ON_OFF] (state) {
    if (state.onOff === 'on') {
      state.onOff = 'off'
    } else {
      state.onOff = 'on'
    }
  }
}

export default {
  state,
  mutations
}
