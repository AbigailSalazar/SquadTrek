package mx.edu.potros.viajesengrupo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class Amigos : AppCompatActivity() {

    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    var amigos = ArrayList<Usuario>()
    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    lateinit var listview:ListView
    var amigosId=ArrayList<String>()
    lateinit var adaptador:AdaptadorAmigos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amigos)


        agregarAmigos()

        listview= findViewById(R.id.amigos_lv)
        adaptador= AdaptadorAmigos(this, amigos)
        listview.adapter = adaptador


        // Empieza :D
        val btnNavAdd = findViewById<ImageButton>(R.id.btnNavAdd)
        btnNavAdd.setOnClickListener {
            val intent = Intent(this, SeleccionarUbicacion::class.java)
            startActivity(intent)
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btnViajes -> {
                    val intent = Intent(this, MisViajes::class.java)
                    startActivity(intent)
                    true
                }
                R.id.btnHome -> {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    true
                }
                R.id.btnUser -> {
                    val intent = Intent(this, Perfil::class.java)
                    startActivity(intent)
                    true
                }
                R.id.btnAmigos -> {
                    val intent = Intent(this, Amigos::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        val btnMessages = findViewById<ImageView>(R.id.btnMessages)
        val btnNotifications = findViewById<ImageView>(R.id.btnNotifications)
        btnMessages.setOnClickListener {
            val intent = Intent(this, Inbox::class.java)
            startActivity(intent)
        }
        btnNotifications.setOnClickListener {
            val intent = Intent(this, Notificaciones::class.java)
            startActivity(intent)
        }
        // termina :D

        val btnAgregarAmigos:Button=findViewById(R.id.btnAgergarAmigos)
        btnAgregarAmigos.setOnClickListener {
            val intent = Intent(this, AgregarAmigo::class.java)
            startActivity(intent)
        }

        listview.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this, Perfil::class.java)
            intent.putExtra("idAmigo", amigos[i].id)
            startActivity(intent)
        }


    }

    fun agregarAmigos(){
        val amigosListener = object :  ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val datosAmigos = snapshot.getValue()
                amigosId.add(datosAmigos.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        val usuariosListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(amigosId.contains(snapshot.key)){
                    val datosAmigo = snapshot.value as HashMap<*, *>
                    val username = datosAmigo["username"] as String
                    val amigo = Usuario(snapshot.key!!,username,"", ArrayList(),"","", ArrayList())

                    val fotoRef = FirebaseDatabase.getInstance().getReference("Fotos").child(snapshot.key!!)


                    fotoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()&& dataSnapshot.hasChild("foto")) {
                                val fotoUrl = dataSnapshot.child("foto").value.toString()
                                amigo.foto=fotoUrl
                                amigos.add(amigo)
                                adaptador.notifyDataSetChanged()
                            }
                            else{
                                amigos.add(amigo)
                                adaptador.notifyDataSetChanged()
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d("TAG", "Error al leer la URL de la foto del usuario", databaseError.toException())
                        }
                    })

                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        userRef.child(usuarioId!!)
            .child("amigos").addChildEventListener(amigosListener)

        userRef.addChildEventListener(usuariosListener)

    }

    class AdaptadorAmigos: BaseAdapter {
        var amigos=ArrayList<Usuario>()
        var contexto: Context?=null

        constructor(contexto: Context, amigos: ArrayList<Usuario>){
            this.amigos=amigos
            this.contexto=contexto
        }

        override fun getCount(): Int {
            return amigos.size
        }

        override fun getItem(position: Int): Any {
            return amigos[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var amig=amigos[position]
            var inflador= LayoutInflater.from(contexto)
            var vista=inflador.inflate(R.layout.seleccion_amigos_view, null)

            var nombre=vista.findViewById(R.id.select_amigos_nombre_tv) as TextView
            var imagen=vista.findViewById(R.id.select_amigos_img) as ImageView

            nombre.setText(amig.username)
            contexto?.let {
                Glide.with(it)
                    .load(amig.foto)
                    .into(imagen)
            }



            return vista

        }
    }



}