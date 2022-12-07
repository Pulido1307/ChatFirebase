package com.polar.industries.chatfirebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.polar.industries.chatfirebase.adapters.AdapterChat
import com.polar.industries.chatfirebase.helpers.CifradoK
import com.polar.industries.chatfirebase.helpers.FirestoreHelper
import com.polar.industries.chatfirebase.models.Chat
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class ChatActivity : AppCompatActivity() {
    private var idUserDestino: String? = null
    private var idUserEnvia: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var conversacionLista: ArrayList<Chat>
    private var cifrado: CifradoK = CifradoK()


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

        cifrado.setPublicKeyString(FirestoreHelper.polar)

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

                var panda: String? = cifrado.Encrypt(editTextMensajeChat.text.toString())

                val msj: Chat = Chat(idUserEnvia, idUserDestino, panda)
                database.push().setValue(msj)
                Toast.makeText(this@ChatActivity, "¡Mensaje enviado!", Toast.LENGTH_SHORT).show()

                val json = JSONObject()
                json.put("message", editTextMensajeChat.text.toString())
                sendDataMqtt(json.toString())

                editTextMensajeChat.setText("")
            }
        }
    }

    fun sendDataMqtt(jsonString:String) {

        GlobalScope.launch {
            Dispatchers.IO
            val url = URL("https://webserviceexamplesmq.000webhostapp.com/Mqtt/SendDataMqtt.php")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("Content-Type", "application/json")
            httpURLConnection.setRequestProperty("Accept", "application/json")
            httpURLConnection.doInput = true
            httpURLConnection.doOutput = true

            val outputStreamWritter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWritter.write(jsonString)
            outputStreamWritter.flush()

            val responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = httpURLConnection.inputStream.bufferedReader().use { it.readLine() }
                withContext(Dispatchers.Main) {
                    try {
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val gsonAux = gson.toJson(JsonParser.parseString(response))
                        Log.e("gsonAux", gsonAux)

                        if (gsonAux.contains("1")) {
                            Toast.makeText(this@ChatActivity, "Se envío con exito al MQTT", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ChatActivity, "No se envío al MQTT", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception){
                        Log.e("error", e.toString())
                        Toast.makeText(this@ChatActivity, "No se envío al MQTT", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Log.e("HTTP ERROR DE CONEXIÓN", "")
            }
        }
    }
}