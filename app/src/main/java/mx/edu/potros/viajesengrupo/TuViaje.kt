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

class TuViaje : AppCompatActivity() {
    lateinit var viaje:Viaje

    var tuViajes: ArrayList<Viajes> = ArrayList<Viajes>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tu_viaje)

        agregarViajes()
        var listview: ListView = findViewById(R.id.listview_tu_viaje) as ListView
        var adaptador: TuViaje.AdaptadorViajes = TuViaje.AdaptadorViajes(this, tuViajes)
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
                    val intent = Intent(this, Amigo::class.java)
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

        val btnTodolist = findViewById<TextView>(R.id.tu_viaje_todolist_tv)
        val btnEvento1 = findViewById<Button>(R.id.btnEvento1)
        val btnEvento2 = findViewById<Button>(R.id.btnEvento2)

        btnTodolist.setOnClickListener {
            val intent = Intent(this, ToDoList::class.java)
            startActivity(intent)
        }
        btnEvento1.setOnClickListener {
            val intent = Intent(this, Eventos::class.java)

            //agregar la fecha, para poderlo buscar los eventos en la bd
            intent.putExtra("dia","11 feb,2023")
            startActivity(intent)
        }
        btnEvento2.setOnClickListener {
            val intent = Intent(this, Eventos::class.java)
            startActivity(intent)
        }


    }

    fun viajeSeleccionado(viajeObject: Viaje){
        this.viaje=viajeObject
    }
    fun agregarViajes(){
        tuViajes.add(Viajes("Guadalajara, Jalisco",R.drawable.guadalajara,R.drawable.round_circle,"Fechas", "Del 20 a 25 de marzo."))
        tuViajes.add(Viajes("Monterrey, Nuevo León",R.drawable.monterrey,R.drawable.round_circle,"Fechas", "Del 11 a 15 de junio."))
        tuViajes.add(Viajes("Cd. Mexico, Mexico",R.drawable.cdmx,R.drawable.round_circle,"Fechas", "Del 11 a 14 de febrero."))
    }

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
            var vista=inflador.inflate(R.layout.tu_viaje_view, null)
            var lugar=vista.findViewById(R.id.tu_viaje_lugar_tv) as TextView
            var imagen=vista.findViewById(R.id.tu_viaje_imagen_iv) as ImageView
            var fecha=vista.findViewById(R.id.tu_viaje_rango_fechas_tv) as TextView
            var amigos=vista.findViewById(R.id.amigo1_tu_viaje) as ImageView
            var btnAmigos = vista.findViewById(R.id.btnAddFriends) as ImageButton

            lugar.setText(viaj.lugar)
            imagen.setImageResource(viaj.imagen)
            amigos.setImageResource(viaj.amigos)
            fecha.setText(viaj.fechas)

            btnAmigos.setOnClickListener {
                val intent = Intent(contexto, AgregarAmigoViaje::class.java)
                contexto!!.startActivity(intent)
            }
            return vista

        }
    }


}