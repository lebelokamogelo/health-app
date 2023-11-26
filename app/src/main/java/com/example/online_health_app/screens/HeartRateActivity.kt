package com.example.online_health_app.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.online_health_app.R
import com.example.online_health_app.databinding.ActivityHeartractivityBinding
import java.util.Arrays


@Suppress("DEPRECATION")
class HeartRateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHeartractivityBinding
    private lateinit var textureView: TextureView
    private var cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var imageDimension: Size
    private val REQUEST_CAMERA_PERMISSION: Int = 1

    private lateinit var mBackgroundHandler: Handler
    private lateinit var mBackgroundThread: HandlerThread

    private var hrtratebpm: Int = 0
    private var mCurrentRollingAverage: Int = 0
    private var mLastRollingAverage: Int = 0
    private var mLastLastRollingAverage: Int = 0
    private lateinit var mTimeArray: LongArray
    private var numCaptures: Int = 0
    private var mNumBeats: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeartractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textureView = findViewById(R.id.texture)
        textureView.surfaceTextureListener = textureListener
        mTimeArray = LongArray(15)

        binding.back.setOnClickListener {
            finish()
        }

        val check = binding.startCaptureButton.text
        binding.startCaptureButton.setOnClickListener {
            when (check) {
                "Scan" -> {
                    openCamera()
                    mNumBeats = 1
                    binding.startCaptureButton.text = "Loading..."
                }
            }
        }
    }

    private val textureListener: TextureView.SurfaceTextureListener =
        object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

                val bmp = textureView.bitmap
                val width = bmp!!.width
                val height = bmp.height
                val pixels = IntArray(height * width)

                bmp.getPixels(pixels, 0, width, width / 2, height / 2, width / 20, height / 20)
                var sum = 0
                for (i in 0 until height * width) {
                    val red = pixels[i] shr 16 and 0xFF
                    sum += red
                }

                if (numCaptures == 20) {
                    mCurrentRollingAverage = sum
                } else if (numCaptures in 21..48) {
                    mCurrentRollingAverage =
                        (mCurrentRollingAverage * (numCaptures - 20) + sum) / (numCaptures - 19)
                } else if (numCaptures >= 49) {
                    mCurrentRollingAverage = (mCurrentRollingAverage * 29 + sum) / 30
                    if (mLastRollingAverage > mCurrentRollingAverage && mLastRollingAverage > mLastLastRollingAverage && mNumBeats < 15) {
                        mTimeArray[mNumBeats] = System.currentTimeMillis()

                        mNumBeats++
                        if (mNumBeats == 15) {
                            calcBPM()
                            binding.progress.text = hrtratebpm.toString()
                            binding.progress.progress = hrtratebpm.toFloat()
                            binding.startCaptureButton.text = "Scan"
                        }
                    }
                }

                if(binding.startCaptureButton.text == "Scan"){
                    closeCamera()
                    stopBackgroundThread()
                }

                // Another capture
                numCaptures++
                // Save previous two values
                mLastLastRollingAverage = mLastRollingAverage
                mLastRollingAverage = mCurrentRollingAverage
            }
        }

    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background").also { it.start() }
        mBackgroundHandler = Handler(mBackgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread.quitSafely()
        try {
            mBackgroundThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun openCamera() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = manager.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraId!!)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            imageDimension = map!!.getOutputSizes(SurfaceTexture::class.java)[0]

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@HeartRateActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
                return
            }
            manager.openCamera(cameraId!!, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createCameraPreview() {
        try {
            val texture = textureView.surfaceTexture
            texture?.setDefaultBufferSize(imageDimension.width, imageDimension.height)
            val surface = Surface(texture)
            captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            cameraDevice?.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice == null) return
                        cameraCaptureSessions = session
                        updatePreview()

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        if (cameraDevice == null) return
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
        try {
            cameraCaptureSessions?.setRepeatingRequest(
                captureRequestBuilder.build(),
                null,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        cameraDevice?.close()
        cameraDevice = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Handle permission denied
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = textureListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun calcBPM() {
        val med: Int
        val time = LongArray(14)
        for (i in 0..13) {
            time[i] = mTimeArray[i + 1] - mTimeArray[i]
        }
        Arrays.sort(time)
        med = time[time.size / 2].toInt()
        hrtratebpm = 60000 / med

    }
}