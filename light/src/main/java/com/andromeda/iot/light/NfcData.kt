package com.andromeda.iot.light

import java.math.BigInteger
import javax.smartcardio.CommandAPDU
import javax.smartcardio.TerminalFactory


class NfcData {
    private fun bin2hex(data: ByteArray): String? {
        return String.format("%0" + data.size * 2 + "X", BigInteger(1, data))
    }
    fun main(){
        System.setProperty("mainKeyForLight", nfcData())
    }
    private fun nfcData():String{
        // Display the list of terminals
        val factory = TerminalFactory.getDefault();
        val terminals = factory.terminals().list();
        println("Terminals: $terminals");
        val terminal = terminals[0]
        val card = terminal.connect("*")
        println("Card: $card")
        val channel = card.basicChannel
        val response = channel.transmit(CommandAPDU(byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())))
        println("Response: $response")

        if (response.sW1 == 0x63 && response.sW2 == 0x00) println("Failed")

        println("UID: " + bin2hex(response.data))
        return ""
    }


}