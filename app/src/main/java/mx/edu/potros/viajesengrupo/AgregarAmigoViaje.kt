package mx.edu.potros.viajesengrupo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class AgregarAmigoViaje : AppCompatActivity() {
    private val viajesRef= FirebaseDatabase.getInstance().getReference("Viajes")
    lateinit var viajeKey:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_amigo_viaje)

        viajeKey= this.intent.getStringExtra("viajeKey").toString()
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

        generarCodigo()
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

        val btnSiguiente = findViewById<Button>(R.id.btnNext)
        btnSiguiente.setOnClickListener {
            val intent = Intent(this, TuViaje::class.java)

            intent.putExtra("viajeKey",viajeKey)
            if (viajeKey != null) {
                Log.d("VIAJE_ADDED", viajeKey)
            }

            startActivity(intent)
        }


    }

    //Generar el código del viaje
    private fun generarCodigo(){
        val txtCodigo: TextView =findViewById(R.id.txtCodigoViaje)
        val codigo=randomStringByKotlinRandom()
        txtCodigo.text=codigo

        viajesRef.child(viajeKey)
            .child("codigoViaje")
            .setValue(codigo)
    }

    private val charPool : List<Char> = ('A'..'Z') + ('0'..'9') + ('-')
    private fun randomStringByKotlinRandom() = (1..8)
        .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")

}