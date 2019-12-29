package com.andromeda.iot.light

import com.microsoft.azure.sdk.iot.device.IotHubMessageResult
import com.microsoft.azure.sdk.iot.device.Message

class Listener : com.microsoft.azure.sdk.iot.device.MessageCallback {
    override fun execute(message: Message?, callbackContext: Any?): IotHubMessageResult? {
        message?.iotHubConnectionString
        return null
    }


}