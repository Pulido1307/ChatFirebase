package com.polar.industries.chatfirebase

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.polar.industries.chatfirebase.helpers.FirestoreHelper
import kotlinx.android.synthetic.main.activity_inicio_sesion.*

class InicioSesionActivity : AppCompatActivity() {
    private var fbHelper: FirestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        supportActionBar!!.hide()

        actionButtons()
    }

    private fun actionButtons() {
        buttonRegistrate.setOnClickListener {
            val intent: Intent = Intent(this@InicioSesionActivity, RegistrateActivity::class.java)
            startActivity(intent)
        }

        buttonIniciarSesion.setOnClickListener {
            val progressDialog = ProgressDialog(this@InicioSesionActivity)
            progressDialog.setMessage("Inciando Sesión ...")
            progressDialog.show()
            fbHelper.inciarSesion(txtCorreoL.editText?.text.toString(), txtPassL.editText?.text.toString(), this@InicioSesionActivity,progressDialog)
        }
    }
}