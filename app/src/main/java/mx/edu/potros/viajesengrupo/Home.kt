package mx.edu.potros.viajesengrupo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class Home : AppCompatActivity() {
    var misViajes = HashMap<ImageView, Viaje>()
    lateinit var list: LinearLayout
    var viajesids=ArrayList<String>()
    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    private val userRef = FirebaseDatabase.getInstance().getReference("Usuarios")
    private val viajesRef= FirebaseDatabase.getInstance().getReference("Viajes")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        list=findViewById(R.id.home_ly)
        // Empieza :D
        val btnBack: Button = findViewById(R.id.btnBack)
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

        cargarViajes()

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


    }

    fun addViaje(viaje: Viaje) {
        var inflador = LayoutInflater.from(this);
        var vista = inflador.inflate(R.layout.home_view, null)

        var lugar = vista.findViewById(R.id.home_lugar_tv) as TextView
        var imagen = vista.findViewById(R.id.home_imagen_iv) as ImageView
        //var amigos=vista.findViewById(R.id.amigo1) as ImageView


        lugar.setText(viaje.ubicacion)
        imagen.setImageResource(R.drawable.cdmx)
        misViajes.set(imagen, viaje)
        imagen.setOnClickListener {
            val intent = Intent(this, TuViaje::class.java)
            TuViaje.viajeSeleccionado(misViajes.get(imagen)!!)
            startActivity(intent)
        }
        list.addView(vista)
    }

    //cargar viajes desde firebase
    fun cargarViajes() {
        //obtiene las referencias a los viajes del usuario
        val viajesListener = object :  ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val id = snapshot.key
                viajesids.add(id!!)
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

        //obtiene los objetos viaje de la tabla viajes
        val viajeListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(viajesids.contains(snapshot.key)){
                    val viajeId = snapshot.key
                    val datosViaje = snapshot.value as HashMap<*, *>
                    val ubicacion = datosViaje["ubicacion"] as String
                    val fechaInicio = datosViaje["fechaInicio"] as String
                    val fechaFinal = datosViaje["fechaFinal"] as String

                    val nuevoViaje = Viaje(viajeId!!,fechaInicio, fechaFinal, ArrayList<String>(), ubicacion, ArrayList<Evento>())
                    addViaje(nuevoViaje)
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
            .child("viajesEnProceso").addChildEventListener(viajesListener)

        viajesRef.addChildEventListener(viajeListener)

    }
}