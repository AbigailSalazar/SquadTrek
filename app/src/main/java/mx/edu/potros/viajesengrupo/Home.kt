package mx.edu.potros.viajesengrupo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance().currentUser

//    var viajes: ArrayList<Viajes> = ArrayList<Viajes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        // Empieza :D
        val ibGiadalajara = findViewById<ImageView>(R.id.guadalajara_home_img_iv)
        val ibMonterrey = findViewById<ImageView>(R.id.monterrey_home_img_iv)

        ibGiadalajara.setOnClickListener {
            val intent = Intent(this, TuViaje::class.java)
            startActivity(intent)
        }

        ibMonterrey.setOnClickListener {
            val intent = Intent(this, TuViaje::class.java)
            startActivity(intent)
        }

        val btnBack:Button = findViewById(R.id.btnBack)
        btnBack.visibility = View.INVISIBLE

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

        var nameTv: TextView = findViewById(R.id.usernameTv)
        val uid = mAuth?.uid

        val userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid!!)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("username")) {
                    val username = dataSnapshot.child("username").value.toString()
                    nameTv.text = username
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", "Error al leer el nombre de usuario", databaseError.toException())
            }
        })




//        agregarViajes()
//
//        var listview: ListView=findViewById(R.id.listview_home)
//
//        var adaptadpr: AdaptadorViajes = AdaptadorViajes(this, viajes)
//        listview.adapter = adaptadpr
    }

//    fun agregarViajes(){
//        viajes.add(Viajes("Guadalajara, Jalisco",R.drawable.guadalajara,R.drawable.round_circle, "",""))
//        viajes.add(Viajes("Monterrey, Nuevo León",R.drawable.monterrey,R.drawable.round_circle, "",""))
//    }
//
//    private class AdaptadorViajes: BaseAdapter {
//        var viajes=ArrayList<Viajes>()
//        var contexto: Context?=null
//
//        constructor(contexto: Context, viajes: ArrayList<Viajes>){
//            this.viajes=viajes
//            this.contexto=contexto
//        }
//
//        override fun getCount(): Int {
//            return viajes.size
//        }
//
//        override fun getItem(position: Int): Any {
//            return viajes[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            var viaj=viajes[position]
//            var inflador=LayoutInflater.from(contexto)
//            var vista=inflador.inflate(R.layout.mis_viajes_view, null)
//
//            var lugar=vista.findViewById(R.id.mis_viajes_lugar_tv) as TextView
//            var imagen=vista.findViewById(R.id.mis_viajes_imagen_iv) as ImageView
//            var fecha=vista.findViewById(R.id.mis_viajes_rango_fechas_tv) as TextView
//            var amigos=vista.findViewById(R.id.amigo1) as ImageView
//            var fechas=vista.findViewById(R.id.fechas_mis_viajes) as TextView
//
//
//            lugar.setText(viaj.lugar)
//            imagen.setImageResource(viaj.imagen)
//            amigos.setImageResource(viaj.amigos)
//            fecha.setText(viaj.fechas)
//            fechas.setText(viaj.fechas)
//
//            return vista
//
//        }
//    }

}