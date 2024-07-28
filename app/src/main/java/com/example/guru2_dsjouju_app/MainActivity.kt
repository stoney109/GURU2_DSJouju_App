package com.example.guru2_dsjouju_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // 권한 요청 코드 상수
    private val PERMISSION_REQUEST_CODE = 100

    private lateinit var homeoptionmenubtn: Button

    private lateinit var sosManager: SosManager
    private lateinit var sosButton: Button

    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeoptionmenubtn = findViewById(R.id.home_option_menu_btn)

        sosButton = findViewById(R.id.button_sos)
        sosButton.setBackgroundResource(R.drawable.app_icon_sos_x)

        homeoptionmenubtn.setOnClickListener { showPopupMenu(it) }

        sosManager = SosManager(this, sosButton)
        sosButton.setOnClickListener { sosManager.showSosDialog() }
        sosButton.setOnLongClickListener {
            sosManager.handleLongPress()
            true
        }

        // 권한이 필요한 경우 요청
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        // 요청할 권한 목록
        val requiredPermissions = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // 요청할 권한 중 아직 승인되지 않은 권한 필터링
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        // 승인되지 않은 권한이 있으면 권한 요청 실행
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            // 권한이 이미 승인된 경우 MapActivity로 이동
            startMapActivity()
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionsGranted = true
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "$permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    allPermissionsGranted = false
                    Toast.makeText(this, "$permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            // 모든 권한이 승인된 경우에만 MapActivity로 이동
            if (allPermissionsGranted) {
                startMapActivity()
            }
        }
    }

    // 상단 옵션 메뉴
    private fun showPopupMenu(view: android.view.View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_main, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_tutorial -> {
                    // 사용법 항목 클릭 시 Tutorial로 이동
                    val intent = Intent(this, Tutorial::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_settings -> {
                    // 설정 항목 클릭 시 Settings로 이동
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun startMapActivity() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        // finish()를 제거하거나 주석 처리하여 권한 요청 후 Activity가 종료되지 않도록 합니다.
        // finish()
    }
}
