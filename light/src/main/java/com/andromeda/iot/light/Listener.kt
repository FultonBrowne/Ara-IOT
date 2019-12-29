package com.andromeda.iot.light

import com.microsoft.azure.sdk.iot.device.*
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus
import java.io.IOException
import java.net.URISyntaxException
import java.util.*


/**
 * Handles messages from an IoT Hub. Default protocol is to use
 * MQTT transport.
 */
object Listener{
    /**
     * Receives requests from an IoT Hub. Default protocol is to use
     * MQTT transport.
     *
     * @param args
     * args[0] = IoT Hub connection string
     * args[1] = protocol (optional, one of 'mqtt' or 'amqps' or 'https' or 'amqps_ws')
     */
    @Throws(IOException::class, URISyntaxException::class)
    fun main(args: Array<String>) {
        println("Starting...")
        println("Beginning setup.")
        val connString = args[0]
        val protocol: IotHubClientProtocol = IotHubClientProtocol.HTTPS


        println("Successfully read input parameters.")
        System.out.format("Using communication protocol %s.\n", protocol.name)
        val client = DeviceClient(connString, protocol)
        println("Successfully created an IoT Hub client.")
        if (protocol == IotHubClientProtocol.MQTT) {
            val callback = MessageCallbackMqtt()
            val counter = Counter(0)
            client.setMessageCallback(callback, counter)
        } else {
            val callback = MessageCallback()
            val counter = Counter(0)
            client.setMessageCallback(callback, counter)
        }
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
            return Integer.toString(num)
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
            return IotHubMessageResult.COMPLETE
        }
    }

    class IotHubConnectionStatusChangeCallbackLogger : IotHubConnectionStatusChangeCallback {
        override fun execute(status: IotHubConnectionStatus, statusChangeReason: IotHubConnectionStatusChangeReason, throwable: Throwable, callbackContext: Any) {
            println()
            println("CONNECTION STATUS UPDATE: $status")
            println("CONNECTION STATUS REASON: $statusChangeReason")
            println("CONNECTION STATUS THROWABLE: " + throwable.message)
            println()
            throwable.printStackTrace()
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