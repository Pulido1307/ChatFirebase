package com.polar.industries.chatfirebase.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.polar.industries.chatfirebase.R
import com.polar.industries.chatfirebase.helpers.CifradoK
import com.polar.industries.chatfirebase.helpers.FirestoreHelper
import com.polar.industries.chatfirebase.models.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AdapterChat(private val context: Context, private val chatList: ArrayList<Chat>): RecyclerView.Adapter<AdapterChat.chatHolder>(){

    public val DERECHA: Int = 1
    public val IZQUIERDA: Int = 0
    public var mensajePropio = false
    private var cifrado: CifradoK = CifradoK()
    init {
        cifrado.setPrivateKeyString(FirestoreHelper.pardo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chatHolder {
        if (viewType == DERECHA){
            val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_derecho, parent, false)
            return chatHolder(itemView)
        } else{
            val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_izquierdo, parent, false)
            return chatHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: chatHolder, position: Int) {
        var mensajeActual: Chat = chatList[position]

        var zacek: String? = cifrado.Decrypt(mensajeActual.mensaje)
        holder.textViewMensajeItem.text = zacek

        //Envio de mensaje a Alexa
        //if(position == chatList.size-1){}
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList[position].envia.equals(FirestoreHelper.mAuth.currentUser!!.uid)){
            mensajePropio = true
            return DERECHA
        } else{
            mensajePropio = false
            return IZQUIERDA
        }
    }

    inner class chatHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textViewMensajeItem: TextView = itemView.findViewById(R.id.textViewMensajeItem)
    }
}
