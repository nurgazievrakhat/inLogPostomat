package android_serialport_api

import android.util.Log
import java.io.File
import java.io.IOException
import java.io.LineNumberReader
import java.io.FileReader

class SerialPortFinder {
    private val drivers: MutableList<Driver> by lazy { loadDrivers() }

    private fun loadDrivers(): MutableList<Driver> {
        val driversList = mutableListOf<Driver>()
        LineNumberReader(FileReader("/proc/tty/drivers")).use { reader ->
            reader.forEachLine { line ->
                val driverName = line.substring(0, 21).trim()
                val parts = line.split("\\s+".toRegex())

                if (parts.size >= 5 && parts.last() == "serial") {
                    Log.d(TAG, "Found new driver $driverName on ${parts[parts.size - 4]}")
                    driversList.add(Driver(driverName, parts[parts.size - 4]))
                }
            }
        }
        return driversList
    }

    fun getAllDevices(): Array<String> = try {
        drivers.flatMap { driver ->
            driver.devices.map { device ->
                "${device.name} (${driver.name})"
            }
        }.toTypedArray()
    } catch (e: IOException) {
        e.printStackTrace()
        emptyArray()
    }

    fun getAllDevicesPath(): Array<String> = try {
        drivers.flatMap { driver ->
            driver.devices.map { it.absolutePath }
        }.toTypedArray()
    } catch (e: IOException) {
        e.printStackTrace()
        emptyArray()
    }

    inner class Driver(
        val name: String,
        private val deviceRoot: String
    ) {
        val devices: List<File> by lazy { loadDevices() }

        private fun loadDevices(): List<File> {
            return File("/dev").listFiles()?.filter { file ->
                file.absolutePath.startsWith(deviceRoot).also { matches ->
                    if (matches) Log.d(TAG, "Found new device: $file")
                }
            } ?: emptyList()
        }
    }

    companion object {
        private const val TAG = "SerialPort"
    }
}