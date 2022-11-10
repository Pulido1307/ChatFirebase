package com.polar.industries.chatfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.polar.industries.chatfirebase.adapters.AdapterChat
import com.polar.industries.chatfirebase.helpers.FirestoreHelper
import com.polar.industries.chatfirebase.models.Chat
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    private var idUserDestino: String? = null
    private var idUserEnvia: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var conversacionLista: ArrayList<Chat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.hide()

        textViewCorreoDestino.text = intent.getStringExtra("correo")
        idUserDestino = intent.getStringExtra("uid")
        idUserEnvia = FirestoreHelper.mAuth.currentUser!!.uid

        database = Firebase.database.getReference("Chats")

        recyclerViewConversacion.layoutManager = LinearLayoutManager(this@ChatActivity)
        recyclerViewConversacion.setHasFixedSize(true)

        conversacionLista = arrayListOf()


        getMensajes()

        actionButtons()
    }

    private fun getMensajes() {
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    conversacionLista.clear()
                    for (conversacionSnapShot in snapshot.children){
                        val mensajeActual = conversacionSnapShot.getValue(Chat::class.java)
                        if(mensajeActual!!.envia.equals(idUserDestino) && mensajeActual!!.recibe.equals(idUserEnvia)){
                            conversacionLista.add(mensajeActual!!)
                        } else if(mensajeActual!!.envia.equals(idUserEnvia) && mensajeActual!!.recibe.equals(idUserDestino)){
                            conversacionLista.add(mensajeActual!!)
                        }
                    }
                    recyclerViewConversacion.adapter = AdapterChat(this@ChatActivity, conversacionLista)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Se cayó más feo que la maquina en la liguilla :c", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun actionButtons() {
        imageButtonEnviarMSJ.setOnClickListener {
            if(editTextMensajeChat.text.toString().isNotEmpty()){
                val msj: Chat = Chat(idUserEnvia, idUserDestino, editTextMensajeChat.text.toString())
                database.push().setValue(msj)
                Toast.makeText(this@ChatActivity, "¡Mensaje enviado!", Toast.LENGTH_SHORT).show()
                editTextMensajeChat.setText("")
            }
        }
    }
}