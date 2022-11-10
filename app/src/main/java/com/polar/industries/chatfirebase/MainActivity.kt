package com.polar.industries.chatfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.polar.industries.chatfirebase.adapters.AdaptersUsuariosDisponibles
import com.polar.industries.chatfirebase.helpers.FirestoreHelper
import com.polar.industries.chatfirebase.models.UsuarioDisponible
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var usuariosList: ArrayList<UsuarioDisponible>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerVierUsusariosD.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerVierUsusariosD.setHasFixedSize(true)

        usuariosList = arrayListOf()

        getListaUsuarios()
    }

    private fun getListaUsuarios() {
        dbref = FirebaseDatabase.getInstance().getReference("Usuarios")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    usuariosList.clear()
                    for (usuarioSnap in snapshot.children){
                        val userA = usuarioSnap.getValue(UsuarioDisponible::class.java)
                        if(!userA!!.uid!!.equals(FirestoreHelper.mAuth.uid)){
                            usuariosList.add(userA!!)
                        }
                    }
                    recyclerVierUsusariosD.adapter = AdaptersUsuariosDisponibles(this@MainActivity, usuariosList, this@MainActivity)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}