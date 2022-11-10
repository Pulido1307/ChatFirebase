package com.polar.industries.chatfirebase.helpers

import android.app.AlertDialog
import android.content.Context

class AlertDialogPersonalized {
    public constructor(titulo: String, mensaje: String, context: Context){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
