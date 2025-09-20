package com.vineesh.carappdemo

import android.car.Car
import android.car.FuelType
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FuelViewModel: ViewModel() {

    /** manager to receive car properties like gear information */
    private var carPropertyManager: CarPropertyManager? = null
    fun unRegister() {
        carPropertyManager?.unregisterCallback(fuelLevelCallback)
        carPropertyManager?.unregisterCallback(rangeRemainingCallback)
        carPropertyManager?.unregisterCallback(fuelLowCallback)
    }

    fun initCar(car: Car) {
        carPropertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
        registerFuel()
    }

    //Fuel information
    private val _fuelInfoFlow = MutableStateFlow("STARTED")
    val fuelInfoFlow: StateFlow<String> = _fuelInfoFlow.asStateFlow()


    private val fuelLevelCallback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>?) {
            val level = value?.value as? Float
            viewModelScope.launch {
                _fuelInfoFlow.emit(level.toString())
            }
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            // Handle error
            viewModelScope.launch {
                _fuelInfoFlow.emit("fuelLevelCallback_UNKNOWN")
            }
        }
    }

    private val fuelLowCallback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>?) {
            val isLow = value?.value as? Boolean
            viewModelScope.launch {
                _fuelInfoFlow.emit(if (isLow == true)"LOW" else "HIGH")
            }
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            // Handle error
            viewModelScope.launch {
                _fuelInfoFlow.emit("fuelLowCallback_UNKNOWN")
            }
        }
    }


    private val rangeRemainingCallback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>?) {
            val range = value?.value as? Float
            viewModelScope.launch {
                _fuelInfoFlow.emit("rangeRemaining$range")
            }
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            // Handle error
            viewModelScope.launch {
                _fuelInfoFlow.emit("rangeRemaining_UNKNOWN")
            }
        }
    }

    fun registerFuel(){
        // Register for Fuel Level
        if (carPropertyManager?.isPropertyAvailable(VehiclePropertyIds.FUEL_LEVEL, 0) == true) {
            carPropertyManager?.registerCallback(
                fuelLevelCallback,
                VehiclePropertyIds.FUEL_LEVEL,
                CarPropertyManager.SENSOR_RATE_ONCHANGE // Or your desired rate
            )
        } else {
            // Handle property not available - e.g., log a message or update UI
            viewModelScope.launch {
                _fuelInfoFlow.emit("FUEL_LEVEL_NA") // Indicate not available
            }
        }


        // Register for Fuel Low Warning
        if (carPropertyManager?.isPropertyAvailable(VehiclePropertyIds.FUEL_DOOR_OPEN, 0) == true) { // Note: Corrected property ID if available, otherwise consider FUEL_LEVEL_LOW
            carPropertyManager?.registerCallback(
                fuelLowCallback,
                VehiclePropertyIds.FUEL_LEVEL_LOW, // This is more appropriate for a low fuel warning
                CarPropertyManager.SENSOR_RATE_ONCHANGE
            )

        } else {
            viewModelScope.launch {
                _fuelInfoFlow.emit("FUEL_LEVEL_LOW_NA") // Default or indicate not available
            }
        }

        // Register for Range Remaining
        if (carPropertyManager?.isPropertyAvailable(VehiclePropertyIds.RANGE_REMAINING, 0) == true) {
            carPropertyManager?.registerCallback(
                rangeRemainingCallback,
                VehiclePropertyIds.RANGE_REMAINING,
                CarPropertyManager.SENSOR_RATE_ONCHANGE
            )
        } else {
            viewModelScope.launch {
                _fuelInfoFlow.emit("RANGE_REMAINING_NA") // Indicate not available
            }
        }
    }


}