package com.andromeda.iot.light

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.RaspiPin
import java.io.IOException


class Light {
    fun main(on:Boolean){
        val gpio = GpioFactory.getInstance()
        val ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00)
        if(on) ledPin.high()
        else ledPin.low()

    }
    @Throws(IOException::class)
    fun <T> yamlArrayToObjectList(yaml: String?, tClass: Class<T>?): ArrayList<T>? {
        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        val listType: CollectionType = mapper.typeFactory.constructCollectionType(ArrayList::class.java, tClass)
        return mapper.readValue(yaml, listType)
    }
}