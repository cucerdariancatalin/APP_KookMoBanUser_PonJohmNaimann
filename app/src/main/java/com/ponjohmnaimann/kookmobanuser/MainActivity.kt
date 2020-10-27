package com.ponjohmnaimann.kookmobanuser

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        if (PrefInit.prefs.successLogIn) {

            val loggedinIntent = Intent(this, LoggedInMainActivity::class.java)
            startActivity(loggedinIntent)

        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PrefInit.prefs.successLogIn = false
        editTextTextPersonName.hint = PrefInit.prefs.name
        editTextTextPersonServiceNumber.hint = PrefInit.prefs.serviceNumber
        editTextTextSignUpCode.hint = PrefInit.prefs.signUpCode

        val createURL = "https://osam.riyenas.dev/api/soldier/create"

        return_btn.setOnClickListener {

            LoadingDialog(this).show()

            val name = editTextTextPersonName.text.toString()
            val serviceNumber = editTextTextPersonServiceNumber.text.toString()
            val signUpCode = editTextTextSignUpCode.text.toString()
            val manufacturer = Build.MANUFACTURER
            val guid = UUID.randomUUID().toString()

            val params = HashMap<String, String>()
            params["name"] = name
            params["signUpCode"] = signUpCode
            params["manufacturer"] = manufacturer
            params["guid"] = guid
            params["serviceNumber"] = serviceNumber

            val userJSON = JSONObject(params as Map<*, *>)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, createURL, userJSON,
                Response.Listener { response ->
                    PrefInit.prefs.seed = response.getString("seed")
                    PrefInit.prefs.adminID = response.getLong("adminId").toString()
                    PrefInit.prefs.deviceID = response.getLong("deviceId").toString()
                    PrefInit.prefs.successLogIn = true
                    PrefInit.prefs.name = name
                    PrefInit.prefs.serviceNumber = serviceNumber
                    val loggedInIntent = Intent(this, LoggedInMainActivity::class.java)
                    startActivity(loggedInIntent)
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener {
                    Toast.makeText(this, "연결 실패", Toast.LENGTH_SHORT).show()
                }
            )

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

            if (PrefInit.prefs.successLogIn) {
                val loggedInIntent = Intent(this, LoggedInMainActivity::class.java)
                startActivity(loggedInIntent)
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                finish()
            }

            LoadingDialog(this).dismiss()

        }
    }
}