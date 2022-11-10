package com.polar.industries.chatfirebase

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.polar.industries.chatfirebase.helpers.FirestoreHelper
import kotlinx.android.synthetic.main.activity_registrate.*

class RegistrateActivity : AppCompatActivity() {
    private var firestore: FirestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrate)

        buttonRegistrarUsuario.setOnClickListener {
            val progressDialog = ProgressDialog(this@RegistrateActivity)
            progressDialog.setMessage("Registrando...")
            progressDialog.show()
            firestore.crearUsuario(textInputCorreoAdd.editText?.text.toString(), textInputPassAdd.editText?.text.toString(), this@RegistrateActivity, progressDialog)
        }
    }
}