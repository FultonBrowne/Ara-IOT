package com.andromeda.iot.light

import com.microsoft.azure.sdk.iot.device.IotHubMessageResult
import com.microsoft.azure.sdk.iot.device.Message

class Listener : com.microsoft.azure.sdk.iot.device.MessageCallback {
    override fun execute(message: Message?, callbackContext: java.lang.Object?): IotHubMessageResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}