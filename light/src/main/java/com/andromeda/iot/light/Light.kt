package com.andromeda.iot.light

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.RaspiPin


class Light {
    fun main(on:Boolean){
        val gpio = GpioFactory.getInstance()
        val ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00)
        if(on) ledPin.high()
        else ledPin.low()

    }
}