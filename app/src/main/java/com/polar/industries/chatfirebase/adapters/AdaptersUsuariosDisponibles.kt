package com.polar.industries.chatfirebase.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.polar.industries.chatfirebase.ChatActivity
import com.polar.industries.chatfirebase.R
import com.polar.industries.chatfirebase.models.UsuarioDisponible

class AdaptersUsuariosDisponibles(private val context: Context, private val userList: ArrayList<UsuarioDisponible>, private val activity: Activity): RecyclerView.Adapter<AdaptersUsuariosDisponibles.UsuarioHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_usuarios, parent, false)
        return UsuarioHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsuarioHolder, position: Int) {
        var usuarioActual: UsuarioDisponible = userList[position]

        holder.textViewCorreoItem.text = usuarioActual.correoElectronico

        holder.cardUsuarioDisponible.setOnClickListener {
            irAChat(usuarioActual.uid,usuarioActual.correoElectronico)
        }
    }

    private fun irAChat(uid: String?, correo: String?) {
        val intent: Intent = Intent(context, ChatActivity::class.java).apply {
            putExtra("uid", uid)
            putExtra("correo", correo)
            activity.startActivity(this)
        }
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UsuarioHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textViewCorreoItem: TextView = itemView.findViewById(R.id.textViewCorreoItem)
        val cardUsuarioDisponible: CardView = itemView.findViewById(R.id.cardUsuarioDisponible)
    }
}
