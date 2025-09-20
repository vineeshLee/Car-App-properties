package com.vineesh.carappdemo

import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GearViewModel : ViewModel() {

    enum class GEAR(val value: Int) {
        UNKNOWN(0),
        NEUTRAL(1),
        REVERSE(2),
        PARK(4),
        DRIVE(8);

        companion object {
            infix fun from(value: Int): GEAR = entries.firstOrNull { it.value == value } ?: UNKNOWN
        }
    }

    /** manager to receive car properties like gear information */
    private var carPropertyManager: CarPropertyManager? = null
    fun unRegister() {
        carPropertyManager?.unregisterCallback(gerPropertyCallBack)
    }

    fun registerListener() {
        carPropertyManager?.registerCallback(
            gerPropertyCallBack, VehiclePropertyIds.GEAR_SELECTION,
            CarPropertyManager.SENSOR_RATE_NORMAL
        )
    }

    fun initCar(car: Car) {
        carPropertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
    }

    //Gear Information
    private val _gearFlow = MutableStateFlow(GEAR.UNKNOWN)
    val gearFlow = _gearFlow.asStateFlow()

    private var gerPropertyCallBack = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(carPropertyValue: CarPropertyValue<*>?) {
            val gearValue = carPropertyValue?.value as? Int
            viewModelScope.launch {
                if (gearValue != null) {
                    _gearFlow.emit(GEAR.from(gearValue))
                } else {
                    _gearFlow.emit(GEAR.UNKNOWN)
                }
            }
        }

        override fun onErrorEvent(p0: Int, p1: Int) {
            viewModelScope.launch {
                _gearFlow.emit(GEAR.UNKNOWN)
            }
        }

    }
}