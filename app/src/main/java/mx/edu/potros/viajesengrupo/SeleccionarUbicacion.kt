package mx.edu.potros.viajesengrupo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class SeleccionarUbicacion : AppCompatActivity() {
    var ubicacionesPopulares = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_ubicacion)

      //var calendario:DatePicker=findViewById(R.id.calendario)


        var btnSiguiente: Button = findViewById(R.id.btnSiguiente)
        btnSiguiente.setOnClickListener {
            var intent= Intent(this,SeleccionarFecha::class.java)
            startActivity(intent)
        }
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

        agregarUbicaciones()
       var  adaptadorUbicaciones=UbicacionesAdapter(ubicacionesPopulares,this)
        var listUbica:ListView=findViewById(R.id.listUbicaciones)
        listUbica.adapter=adaptadorUbicaciones

    }

    private fun agregarUbicaciones() {
        ubicacionesPopulares.add("Guadalajara, Jalisco")
        ubicacionesPopulares.add("Monterrey, Nuevo Leon")
        ubicacionesPopulares.add("Cancún, Cancún")
    }

    private class UbicacionesAdapter:BaseAdapter{
        var ubPopulares = ArrayList<String>()
        var contexto: Context?=null

        constructor(ubPopulares: ArrayList<String>, contexto: Context?) : super() {
            this.ubPopulares = ubPopulares
            this.contexto = contexto
        }

        override fun getCount(): Int {
            return ubPopulares.size
        }

        override fun getItem(position: Int): Any {
            return ubPopulares[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var ubicacion=ubPopulares[p0]
            var inflador= LayoutInflater.from(contexto)
            var vista=inflador.inflate(R.layout.activity_ubicacion_item, null)

            var nombre:TextView = vista.findViewById(R.id.txtUbicacion)
            nombre.setText(ubicacion)

            return vista
        }

    }
}