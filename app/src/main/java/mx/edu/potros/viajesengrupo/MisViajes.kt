package mx.edu.potros.viajesengrupo

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList


class MisViajes : AppCompatActivity() {
    var misViajes= HashMap<ImageView,Viaje>()
    //var viajesids=HashMap<ImageView,String>()
    lateinit var list:LinearLayout

    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    //val usuarioId="-NUileJDCu_cQMfcael9"
    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_viajes)

        list=findViewById(R.id.mis_viajes_ly)
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

        agregarViajes()
        cargarViajes()


       /* var listview: ListView = findViewById(R.id.mis_viajes_lv) as ListView

        var adaptador: AdaptadorViajes = AdaptadorViajes(this,misViajes)
        listview.adapter = adaptador*/

    }


    fun agregarViajes(){
//        misViajes.add(Viajes("Guadalajara, Jalisco",R.drawable.guadalajara,R.drawable.round_circle,"Fechas", "Del 20 a 25 de marzo."))
//        misViajes.add(Viajes("Monterrey, Nuevo León",R.drawable.monterrey,R.drawable.round_circle,"Fechas", "Del 11 a 15 de junio."))
//        misViajes.add(Viajes("Cd. México, México.",R.drawable.cdmx,R.drawable.round_circle,"Fechas", "Del 11 a 14 de febrero."))

    }


    fun addViaje(viaje:Viaje){
        var inflador=LayoutInflater.from(this);
        var vista=inflador.inflate(R.layout.mis_viajes_view,null)

        var lugar=vista.findViewById(R.id.mis_viajes_lugar_tv) as TextView
        var imagen=vista.findViewById(R.id.mis_viajes_imagen_iv) as ImageView
        var fecha=vista.findViewById(R.id.mis_viajes_rango_fechas_tv) as TextView
        //var amigos=vista.findViewById(R.id.amigo1) as ImageView


        lugar.setText(viaje.ubicacion)
        imagen.setImageResource(R.drawable.cdmx)
        fecha.setText(viaje.fechaInicio+" a "+ viaje.fechaFinal)
        misViajes.set(imagen,viaje)
        imagen.setOnClickListener {
            val intent = Intent(this, TuViaje::class.java)
            TuViaje.viajeSeleccionado(misViajes.get(imagen)!!)
            //intent.putExtra("viajeKey",viajesids.get(imagen))
            startActivity(intent)
        }

        list.addView(vista)

    }


    //cargar viajes desde firebase
    fun cargarViajes() {
        val viajesListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val viajeId = snapshot.key
                val datosViaje = snapshot.value as HashMap<*, *>
                val ubicacion = datosViaje["ubicacion"] as String
                val fechaInicio = datosViaje["fechaInicio"] as String
                val fechaFinal = datosViaje["fechaFinal"] as String

                val nuevoViaje = Viaje(viajeId!!,fechaInicio, fechaFinal, ArrayList<String>(), ubicacion, ArrayList<Evento>())

                addViaje(nuevoViaje)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle child changed event if needed
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle child removed event if needed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle child moved event if needed
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadEvento:onCancelled", databaseError.toException())
            }
        }

        if (usuarioId != null) {
            userRef.child(usuarioId)
                .child("viajesEnProceso")
                .addChildEventListener(viajesListener)
        }
    }


//    fun llenarListaViajes(listEventos: LinearLayout){
//        misViajes.forEach {
//            var inflador=LayoutInflater.from(this);
//            var vista=inflador.inflate(R.layout.mis_viajes_view,null)
//
//
//            var lugar=vista.findViewById(R.id.mis_viajes_lugar_tv) as TextView
//            var imagen=vista.findViewById(R.id.mis_viajes_imagen_iv) as ImageView
//            var fecha=vista.findViewById(R.id.mis_viajes_rango_fechas_tv) as TextView
//            var amigos=vista.findViewById(R.id.amigo1) as ImageView
//
//            lugar.setText(it.ubicacion)
//            imagen.setImageResource(R.drawable.cdmx)
//            fecha.setText(it.fechaInicio+" a "+ it.fechaFinal)
//
//            viajesids.set(imagen,it)
//
//            imagen.setOnClickListener {
//
//                TuViaje.viajeSeleccionado(viajesids.get(imagen)!!)
//
//                val intent = Intent(this, TuViaje::class.java)
//                startActivity(intent)
//            }
//
//
//            listEventos.addView(vista)
//        }
//    }



/*
    private class AdaptadorViajes: BaseAdapter {
        var viajes=ArrayList<Viajes>()
        var contexto: Context?=null

        constructor(contexto: Context, viajes: ArrayList<Viajes>){
            this.viajes=viajes
            this.contexto=contexto
        }

        override fun getCount(): Int {
            return viajes.size
        }

        override fun getItem(position: Int): Any {
            return viajes[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var viaj=viajes[position]
            var inflador= LayoutInflater.from(contexto)
            var vista=inflador.inflate(R.layout.mis_viajes_view, null)

            var lugar=vista.findViewById(R.id.mis_viajes_lugar_tv) as TextView
            var imagen=vista.findViewById(R.id.mis_viajes_imagen_iv) as ImageView
            var fecha=vista.findViewById(R.id.mis_viajes_rango_fechas_tv) as TextView
            var amigos=vista.findViewById(R.id.amigo1) as ImageView

            lugar.setText(viaj.lugar)
           imagen.setImageResource(viaj.imagen)
            amigos.setImageResource(viaj.amigos)
            fecha.setText(viaj.fechas)

            return vista

        }
   }*/



}