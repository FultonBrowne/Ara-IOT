package com.andromeda.iot.light

import com.microsoft.azure.sdk.iot.device.DeviceClient
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol

class Listener  {
    private val connString: String = ""
    private val listen = DeviceClient("", IotHubClientProtocol.HTTPS)
    fun main(){
        listen.open()

    }



}