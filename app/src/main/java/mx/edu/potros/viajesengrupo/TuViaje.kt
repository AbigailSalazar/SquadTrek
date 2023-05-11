package mx.edu.potros.viajesengrupo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.database.ktx.values

class TuViaje : AppCompatActivity() {

    val usuarioId="-NUileJDCu_cQMfcael9"
    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    var tuViajes: ArrayList<Viajes> = ArrayList<Viajes>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tu_viaje)

     /*   agregarViajes()
        var listview: ListView = findViewById(R.id.listview_tu_viaje) as ListView
        var adaptador: TuViaje.AdaptadorViajes = TuViaje.AdaptadorViajes(this, tuViajes)
        listview.adapter = adaptador
*/

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
                    val intent = Intent(this, Amigo::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

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

        val btnTodolist = findViewById<TextView>(R.id.tu_viaje_todolist_tv)
        val btnEvento1 = findViewById<Button>(R.id.btnEvento1)
        val btnEvento2 = findViewById<Button>(R.id.btnEvento2)

        btnTodolist.setOnClickListener {
            val intent = Intent(this, ToDoList::class.java)
            startActivity(intent)
        }



        btnEvento1.setOnClickListener {
            val intent = Intent(this, Eventos::class.java)
            var viajeKey=this.intent.getStringExtra("viajeKey")
            //agregar la fecha, para poderlo buscar los eventos en la bd
            intent.putExtra("dia","11 feb,2023")
            if(viajeKey!=null){
                intent.putExtra("viajeKey",viajeKey)
            }
            startActivity(intent)
        }
        btnEvento2.setOnClickListener {
            val intent = Intent(this, Eventos::class.java)
            startActivity(intent)
        }

        var btnAmigos:ImageButton= findViewById(R.id.btnAddFriends)
        btnAmigos.setOnClickListener {
            val intent = Intent(this, AgregarAmigoViaje::class.java)
            startActivity(intent)
        }

        //carga el viaje desde firebase
        cargarViaje()

        var lugar=findViewById(R.id.tu_viaje_lugar_tv) as TextView
        var imagen=findViewById(R.id.tu_viaje_imagen_iv) as ImageView
        var fecha=findViewById(R.id.tu_viaje_rango_fechas_tv) as TextView
        var amigos=findViewById(R.id.amigo1_tu_viaje) as ImageView



        if(viajeSel!=null){
            lugar.setText(viajeSel.ubicacion)
            imagen.setImageResource(R.drawable.cdmx)
            fecha.setText(viajeSel.fechaInicio+" a "+ viajeSel.fechaFinal)
        }



    }

    fun cargarViaje(){
        var viajeId = intent.getStringExtra("viajeKey")
        if(viajeId!=null){
            val viajesListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val childData = snapshot.getValue() as Map<String, Any>?
                    if (childData != null) {
                        val viaje = Viaje(
                            (childData["fechaInicio"] as String?)!!,
                            (childData["fechaFinal"] as String?)!!,
                            ArrayList(),
                            (childData["ubicacion"] as String?)!!,
                            ArrayList()

                        )
                        print(viaje.toString())
                        viajeSeleccionado(viaje)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            var viajeRef= userRef.child(usuarioId)
                .child("viajesEnProceso")
                .child(viajeId)
                .addValueEventListener(viajesListener)
        }
    }

    companion object{
        private var viajeSel: Viaje =Viaje("","", ArrayList(),"", ArrayList())

        fun viajeSeleccionado(viajeObject: Viaje){
            this.viajeSel=viajeObject
        }
    }


}