package mx.edu.potros.viajesengrupo

import android.content.ContentValues
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SeleccionarFecha : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    //val usuarioId="-NUileJDCu_cQMfcael9"
    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_fecha)

        var calendarView: CalendarView = findViewById(R.id.calendarioInicio)

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

        // Empieza :D
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

        //obtener datos de viaje
        var calendarInicio: CalendarView = findViewById(R.id.calendarioInicio)
        var calendarFinal: CalendarView = findViewById(R.id.calendarioFinal)

        var dateFormat:DateFormat = SimpleDateFormat("dd/MM/yyy");

        var fechaInicio=dateFormat.format(calendarInicio.date)
        var fechaFinal=dateFormat.format(calendarFinal.date)
        calendarInicio.setOnDateChangeListener { calendarView, i, i2, i3 ->
            fechaInicio = "$i3/$i2/$i"
        }
        calendarFinal.setOnDateChangeListener { calendarView, i, i2, i3 ->
            fechaFinal = "$i3/$i2/$i"
        }

        //pasar id del viaje guardado

        val btnContinuar:Button=findViewById(R.id.btnSiguiente2)
        btnContinuar.setOnClickListener {

            var viaje = Viaje(
                fechaInicio,
                fechaFinal,
                ArrayList(),
                ubicacionSel,
                ArrayList()
            )

            //obtiene el id del nuevo viaje
            var viajeRef = userRef.child(usuarioId!!)
                .child("viajesEnProceso")
                .push()

            var viajeKey = viajeRef.key

            //guardar viaje en bd
            if (viajeKey != null) {
                viajeRef.setValue(viaje).addOnSuccessListener {
                    TuViaje.viajeSeleccionado(viaje)
                    val intent = Intent(this, AgregarAmigoViaje::class.java)
                    intent.putExtra("viajeKey",viajeKey)
                    if (viajeKey != null) {
                        Log.d("VIAJE_ADDED", viajeKey)
                    }
                    startActivity(intent)
                    finish()
                }
            }
            Toast.makeText(this,"Hubo un error, prueba más tarde",Toast.LENGTH_LONG)

        }

    }

    companion object{

        private var ubicacionSel: String=""
        fun setUbicacion(ubicacion: String){
            ubicacionSel=ubicacion
        }
    }
}