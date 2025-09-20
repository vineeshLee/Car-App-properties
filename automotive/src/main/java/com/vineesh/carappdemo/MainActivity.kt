package com.vineesh.carappdemo

import android.car.Car
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vineesh.carappdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    /** car object to communicate with car services */
    private lateinit var car : Car

    override fun onResume() {
        super.onResume()
        gearViewModel.registerListener()
    }
    override fun onPause() {
        //unregister listener
        gearViewModel.unRegister()
        fuelViewModel.unRegister()
        super.onPause()
    }

    override fun onDestroy() {
        //disconnect to car object
        if (car.isConnected) car.disconnect()
        super.onDestroy()
    }

    private lateinit var binding: ActivityMainBinding
     private val gearViewModel: GearViewModel by viewModels()
     private val fuelViewModel: FuelViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            gearViewModel.gearFlow.collect {gear->
                updateUi(gear.name)
            }
        }
        binding.btGearStatus.setOnClickListener {
            setUpCarAndConnect()
        }

        binding.btFuelStatus.setOnClickListener {
            car = Car.createCar(
                this, null, Car.CAR_WAIT_TIMEOUT_WAIT_FOREVER
            ) { car: Car, ready: Boolean ->
                if (ready) {
                    fuelViewModel.initCar(car)
                }
            }
           lifecycleScope.launch {
               delay(1.seconds)
               fuelViewModel.unRegister()
           }
        }

        lifecycleScope.launch{
            fuelViewModel.fuelInfoFlow.collect{
                val data=binding.tvFuelStatus.text
                binding.tvFuelStatus.text =buildString {
                    append(data)
                    append(it)
                }
            }
        }
    }

    private fun setUpCarAndConnect() {
        // connect to car and get manager
        car = Car.createCar(
            this, null, Car.CAR_WAIT_TIMEOUT_WAIT_FOREVER
        ) { car: Car, ready: Boolean ->
            if (ready) {
               gearViewModel.initCar(car)
            }
        }
    }

    private fun updateUi(gearStatus: String) {
        binding.tvGearStatus.text = gearStatus
    }

}