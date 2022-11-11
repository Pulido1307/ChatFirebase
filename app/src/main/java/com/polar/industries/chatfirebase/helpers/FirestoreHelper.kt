package com.polar.industries.chatfirebase.helpers

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.polar.industries.chatfirebase.InicioSesionActivity
import com.polar.industries.chatfirebase.MainActivity
import com.polar.industries.chatfirebase.models.UsuarioDisponible
import java.util.*


class FirestoreHelper {
    private lateinit var database: DatabaseReference

    companion object {
        public val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        public fun getCurrentUser(): FirebaseUser? {
            return mAuth.currentUser
        }

        var user: User? = null

        var db = FirebaseFirestore.getInstance()
        val UsuariosCollection = db.collection("ClavesUltraSecretas")

        var pardo: String? = null
        var polar: String? = null
    }


    constructor() {
        database = Firebase.database.reference
    }

    public fun crearUsuario(
        correo: String,
        pass: String,
        context: Context,
        dialog: ProgressDialog
    ) {
        if (correo.isNotEmpty() && pass.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(correo, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = mAuth.currentUser
                    addUserCloud(correo, pass, user!!.uid, context, dialog)
                } else {
                    dialog.dismiss()
                }
            }
        } else {
            dialog.dismiss()
            val aDP: AlertDialogPersonalized = AlertDialogPersonalized(
                "Error en las credenciales \uD83D\uDE1E",
                "Correo electrónico o contraseña inválidas",
                context
            )
        }
    }

    private fun addUserCloud(
        correo: String,
        pass: String,
        document: String,
        context: Context,
        dialog: ProgressDialog
    ) {

        val us: UsuarioDisponible = UsuarioDisponible(correo, document, pass)
        dialog.dismiss()

        database.child("Usuarios").child(document).setValue(us).addOnCompleteListener {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            val intent: Intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }.addOnFailureListener {
            Toast.makeText(context, "Se cayó en cloud", Toast.LENGTH_SHORT).show()
            val intent: Intent = Intent(context, InicioSesionActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }

    }

    public fun inciarSesion(
        correo: String,
        pass: String,
        context: Context,
        dialog: ProgressDialog
    ) {
        if (correo.isNotEmpty() && pass.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener {
                dialog.dismiss()
                if (it.isSuccessful) {
                    val user: FirebaseUser? = mAuth.currentUser
                    esteMetodoNoObtieneClaves("NoSonLasKeys", dialog, context, correo)
                } else {
                    var error: String = it.exception?.message.toString()

                    when (error) {
                        "There is no user record corresponding to this identifier. The user may have been deleted." -> {
                            val alertA: AlertDialogPersonalized = AlertDialogPersonalized(
                                "Error al iniciar sesión \uD83D\uDE1E",
                                "No existe registro de usuario correspondiente a este correo electrónico. Es posible que el usuario haya sido eliminado.",
                                context
                            )
                        }

                        "The password is invalid or the user does not have a password." -> {
                            val alertA: AlertDialogPersonalized = AlertDialogPersonalized(
                                "Error al iniciar sesión \uD83D\uDE1E",
                                "La contraseña no es válida.",
                                context
                            )
                        }

                        "The user account has been disabled by an administrator." -> {
                            val alertA: AlertDialogPersonalized = AlertDialogPersonalized(
                                "Error al iniciar sesión \uD83D\uDE1E",
                                "Cuenta inhabilidada, contacta al administrador.",
                                context
                            )
                        }

                        else -> {
                            val alertA: AlertDialogPersonalized = AlertDialogPersonalized(
                                "Error al iniciar sesión \uD83D\uDE1E",
                                "Verifica tu conexión a Internet.",
                                context
                            )
                        }
                    }
                }
            }
        } else {
            dialog.dismiss()
            val alertDialogPersonalized: AlertDialogPersonalized = AlertDialogPersonalized(
                "Error en las credenciales de inicio de sesión \uD83D\uDE1E",
                "Correo electrónico o contraseña inválidas",
                context
            )
        }
    }


    fun esteMetodoNoObtieneClaves(document: String?, dialog: ProgressDialog, context: Context, correo: String) {
        UsuariosCollection.document(document!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (Objects.requireNonNull(document).exists()) {
                    dialog.dismiss()
                    val data = document.data

                    pardo = data!!["NoEsLaClavePrivada"].toString()
                    polar = data!!["NoEsLaClavePublica"].toString()

                    Toast.makeText(context, "¡Bienvenido $correo!", Toast.LENGTH_LONG).show()
                    val intent: Intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()

                }
            } else {
                val intent = Intent(context, InicioSesionActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
            dialog.dismiss()
        }
    }


}