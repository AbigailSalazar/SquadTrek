package mx.edu.potros.viajesengrupo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.concurrent.thread


class SeleccionarUbicacion : AppCompatActivity() {
    var ubicacionesPopulares = ArrayList<String>()
    var ubicaciones=ArrayList<String>()
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

        agregarUbicacionesPopulares()
        agregarUbicaciones()

        var adaptadorUbicaciones=UbicacionesAdapter(ubicacionesPopulares,this)
        var listUbica:ListView=findViewById(R.id.listUbicaciones)
        listUbica.adapter=adaptadorUbicaciones

        //Seleciionar una ubicación popular
        listUbica.setOnItemClickListener { adapterView, view, i, l ->
            SeleccionarFecha.setUbicacion(ubicacionesPopulares.get(i))
            val intent = Intent(this, SeleccionarFecha::class.java)
            startActivity(intent)
            finish()
        }

        //Ubicacion personalizada/Autocomplet text
        var txtUbicacion:AutoCompleteTextView = findViewById(R.id.txtSearch)
        val adapterUbicacion: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, ubicaciones
        )
        txtUbicacion.setAdapter(adapterUbicacion)

        //siguiente activity con ubicacion personalizada
        var btnContinuar:Button=findViewById(R.id.btnSiguiente)
        btnContinuar.setOnClickListener {
            if(txtUbicacion.text.isNotBlank()){
                SeleccionarFecha.setUbicacion(txtUbicacion.text.toString())
                val intent = Intent(this, SeleccionarFecha::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this, "No has elejido la ubicación de tu viaje",Toast.LENGTH_SHORT)
            }

        }


    }

    private fun agregarUbicacionesPopulares() {
        ubicacionesPopulares.add("Guadalajara, Jalisco")
        ubicacionesPopulares.add("Monterrey, Nuevo Leon")
        ubicacionesPopulares.add("Cancún, Cancún")
    }

    private fun agregarUbicaciones(){
        ubicaciones.add("Cd. Obregón, Son")
        ubicaciones.add("Hermosillo, Son")
        ubicaciones.add("Los Angeles, California")
        ubicaciones.add("Buenos Aires, Buenos Aires")
        ubicaciones.add("Nuevo leon, Monterrey")
        ubicaciones.add("Tijuana, BC")
        ubicaciones.add("Encenada, BC")
        ubicaciones.add("Cd. México, México")
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