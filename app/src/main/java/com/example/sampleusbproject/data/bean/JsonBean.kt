package com.example.sampleusbproject.data.bean


data class JsonBean(
    var status: String? = null,
    var scs_type: String? = null,
    var dial_up: String? = null,
    var door_number: String? = null,
    var msg: String? = null,
    var times: String? = null
) {
    override fun toString(): String {
        return "JsonBean{\"status\":\"$status\", \"scs_type\":\"$scs_type\", \"dial_up\":\"$dial_up\", \"door_number\":\"$door_number\", \"msg\":\"$msg\", \"times\":\"$times\"}"
    }
}