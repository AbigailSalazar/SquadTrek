package mx.edu.potros.viajesengrupo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SeleccionarFecha : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    //val usuarioId="-NUileJDCu_cQMfcael9"
    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    private val viajesRef= FirebaseDatabase.getInstance().getReference("Viajes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_fecha)

        var ubicacion=intent.getStringExtra("ubicacion")
        var calendarView: CalendarView = findViewById(R.id.calendarioInicio)
        val editar = intent.getStringExtra("editar")


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

        //si viene a editar
        var edFechaInicio = intent.getStringExtra("fechaInicio")
        var edFechaFinal = intent.getStringExtra("fechaFinal")


        var dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy");

        if (edFechaInicio != null) {
            calendarInicio.date = dateFormat.parse(edFechaInicio).time
            calendarFinal.date = dateFormat.parse(edFechaFinal).time
        }

        var fechaInicio = dateFormat.format(calendarInicio.date)
        var fechaFinal = dateFormat.format(calendarFinal.date)


        var date1 = Calendar.getInstance()
        var date2 = Calendar.getInstance()
        calendarInicio.setOnDateChangeListener { calendarView, i, i2, i3 ->
            fechaInicio = "$i3/$i2/$i"
            date1.set(i, i2, i3)
        }
        calendarFinal.setOnDateChangeListener { calendarView, i, i2, i3 ->
            fechaFinal = "$i3/$i2/$i"
            date2.set(i, i2, i3)
        }

        val btnContinuar: Button = findViewById(R.id.btnSiguiente2)
        btnContinuar.setOnClickListener {
            // Valida la fecha
            if (date1.before(date2)) {
                val viaje = Viaje(
                    "",
                    fechaInicio,
                    fechaFinal,
                    ArrayList(),
                    ubicacionSel,
                    ArrayList()
                )
                viaje.amigos.add(usuarioId!!)

                if (editar=="editar"){
                    userRef.child(usuarioId).child("viajesEnProceso")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val viajesEnProcesoMap = dataSnapshot.value as? Map<*, *>
                                    val viajeKey = viajesEnProcesoMap?.keys?.firstOrNull() as? String

                                    if (viajeKey != null) {
                                        val updateMap: MutableMap<String, Any> = HashMap()
                                        updateMap["fechaInicio"] = viaje.fechaInicio
                                        updateMap["fechaFinal"] = viaje.fechaFinal
                                        updateMap["ubicacion"] = viaje.ubicacion

                                        viajesRef.child(viajeKey).updateChildren(updateMap)
                                            .addOnSuccessListener {
                                                val usuariosMap: MutableMap<String, Any> = HashMap()
                                                usuariosMap["$usuarioId/viajesEnProceso/$viajeKey"] =
                                                    viaje.ubicacion

                                                userRef.updateChildren(usuariosMap)
                                                    .addOnSuccessListener {
                                                        TuViaje.viajeSeleccionado(viaje)
                                                        val intent = Intent(
                                                            this@SeleccionarFecha,
                                                            AgregarAmigoViaje::class.java
                                                        )
                                                        intent.putExtra("viajeKey", viajeKey)
                                                        Log.d("VIAJE_EDITED", viajeKey)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            this@SeleccionarFecha,
                                                            "Error al actualizar la información en Usuarios",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                            }
                                    } else {
                                        Toast.makeText(
                                            this@SeleccionarFecha,
                                            "No se encontró el viaje correspondiente en viajesEnProceso",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    val viajeRefKey = viajesRef.push()
                                    val newViajeKey = viajeRefKey.key

                                    if (newViajeKey != null) {
                                        viajeRefKey.setValue(viaje).addOnSuccessListener {
                                            val usuariosMap: MutableMap<String, Any> = HashMap()
                                            usuariosMap["$usuarioId/viajesEnProceso/$newViajeKey"] =
                                                viaje.ubicacion
                                            userRef.updateChildren(usuariosMap)
                                                .addOnSuccessListener {
                                                    TuViaje.viajeSeleccionado(viaje)
                                                    val intent = Intent(
                                                        this@SeleccionarFecha,
                                                        AgregarAmigoViaje::class.java
                                                    )
                                                    intent.putExtra("viajeKey", newViajeKey)
                                                    Log.d("VIAJE_ADDED", newViajeKey)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(
                                                        this@SeleccionarFecha,
                                                        "Error al actualizar la información en Usuarios",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@SeleccionarFecha,
                                            "Error al generar el ID del nuevo viaje",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(
                                    this@SeleccionarFecha,
                                    "Error al leer los datos de viajesEnProceso",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                }
                else{
                    var cantViajes=0
                    //obtiene el id del nuevo viaje y lo guarda en el usuario

                    var viajeRefKey = viajesRef
                        .push()

                    var viajeKey = viajeRefKey.key

                    val map: MutableMap<String, Any> = HashMap()
                    map[viajeKey!!] = viaje.ubicacion
                    userRef.child(usuarioId!!).child("viajesEnProceso").updateChildren(map)
                    //guarda viaje en tabla viajes

                    //guardar viaje en bd
                    if (viajeKey != null) {
                        viajesRef.child(viajeKey!!).setValue(viaje).addOnSuccessListener {
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
                }

            } else {
                val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                builder.setMessage("Fechas inválidas")
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                    }
                builder.create().show()
            }
        }
    }

    companion object{

        private var ubicacionSel: String=""
        fun setUbicacion(ubicacion: String){
            ubicacionSel=ubicacion
        }
    }
}