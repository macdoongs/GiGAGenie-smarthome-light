import * as types from './mutation-types'

// Common
export const togglePower = ({ commit }) => {
  commit(types.TOGGLE_ON_OFF)
}
