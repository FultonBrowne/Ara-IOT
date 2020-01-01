package com.andromeda.iot.light

import com.microsoft.azure.sdk.iot.device.*
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus
import java.io.IOException
import java.net.URISyntaxException
import java.net.URL
import java.util.*


/**
 * Handles messages from an IoT Hub. Default protocol is to use
 * MQTT transport.
 */
object Listener{
    @Throws(IOException::class, URISyntaxException::class)
    fun main() {
        println("Starting...")
        println("Beginning setup.")
        if(System.getProperty("araId") == null) Nfc().main()
        val connString = System.getProperty("araId")
        val protocol: IotHubClientProtocol = IotHubClientProtocol.MQTT


        println("Successfully read input parameters.")
        System.out.format("Using communication protocol %s.\n", protocol.name)
        val client = DeviceClient(connString, protocol)
        println("Successfully created an IoT Hub client.")
            val callback = MessageCallbackMqtt()
            val counter = Counter(0)
            client.setMessageCallback(callback, counter)
        println("Successfully set message callback.")
        client.registerConnectionStatusChangeCallback(IotHubConnectionStatusChangeCallbackLogger(), Any())
        client.open()
        println("Opened connection to IoT Hub.")
        println("Beginning to receive messages...")
        println("Press any key to exit...")
        val scanner = Scanner(System.`in`)
        scanner.nextLine()
        client.closeNow()
        println("Shutting down...")
    }

    /** Used as a counter in the message callback.  */
    open class Counter(private var num: Int) {
        fun get(): Int {
            return num
        }

        fun increment() {
            num++
        }

        override fun toString(): String {
            return num.toString()
        }

    }

    class MessageCallback : com.microsoft.azure.sdk.iot.device.MessageCallback {
        override fun execute(msg: Message, context: Any): IotHubMessageResult {
            val counter = context as Counter
            println(
                    "Received message " + counter.toString()
                            + " with content: " + String(msg.bytes, Message.DEFAULT_IOTHUB_MESSAGE_CHARSET))
            val switchVal = counter.get() % 3
            val res: IotHubMessageResult
            res = when (switchVal) {
                0 -> IotHubMessageResult.COMPLETE
                1 -> IotHubMessageResult.ABANDON
                2 -> IotHubMessageResult.REJECT
                else -> throw IllegalStateException("Invalid message result specified.")
            }
            println("Responding to message " + counter.toString() + " with " + res.name)
            counter.increment()
            return res
        }
    }

    // Our MQTT doesn't support abandon/reject, so we will only display the messaged received
// from IoTHub and return COMPLETE
    class MessageCallbackMqtt : com.microsoft.azure.sdk.iot.device.MessageCallback {
        override fun execute(msg: Message, context: Any): IotHubMessageResult {
            val counter = context as Counter
            println(
                    "Received message " + counter.toString()
                            + " with content: " + String(msg.bytes, Message.DEFAULT_IOTHUB_MESSAGE_CHARSET))
            counter.increment()
            val url = URL("")
            return IotHubMessageResult.COMPLETE
        }
    }

    class IotHubConnectionStatusChangeCallbackLogger : IotHubConnectionStatusChangeCallback {
        override fun execute(status: IotHubConnectionStatus, statusChangeReason: IotHubConnectionStatusChangeReason, throwable: Throwable?, callbackContext: Any) {
            println()
            println("CONNECTION STATUS UPDATE: $status")
            println("CONNECTION STATUS REASON: $statusChangeReason")
            if (throwable != null) {
                println("CONNECTION STATUS THROWABLE: " + throwable.message)
            }
            println()
            throwable?.printStackTrace()
            when (status) {
                IotHubConnectionStatus.DISCONNECTED -> { //connection was lost, and is not being re-established. Look at provided exception for
    // how to resolve this issue. Cannot send messages until this issue is resolved, and you manually
    // re-open the device client
                }
                IotHubConnectionStatus.DISCONNECTED_RETRYING -> { //connection was lost, but is being re-established. Can still send messages, but they won't
    // be sent until the connection is re-established
                }
                IotHubConnectionStatus.CONNECTED -> { //Connection was successfully re-established. Can send messages.
                }
            }
        }
    }
}