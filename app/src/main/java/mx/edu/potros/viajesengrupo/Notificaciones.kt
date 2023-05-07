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

class Notificaciones : AppCompatActivity() {

    var notificaciones: ArrayList<Notifications> = ArrayList<Notifications>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        agregarNotificacion()

        var listview: ListView = findViewById(R.id.listViewInbox) as ListView

        var adapador: AdaptadorNotificaciones = AdaptadorNotificaciones(this, notificaciones)
        listview.adapter = adapador


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

        val selectAmigo = findViewById<ListView>(R.id.listViewInbox)
        selectAmigo.setOnItemClickListener { parent, view, position, id ->

            when(position) {
                0 -> {
                    val intent = Intent(this, Eventos::class.java)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(this, ToDoList::class.java)
                    startActivity(intent)
                }
                else -> {
                    // acción por defecto si se selecciona un elemento no previsto
                }
            }
        }




    }

    fun agregarNotificacion(){
        notificaciones.add(Notifications(R.drawable.round_circle, "Guadalajara", "Fátima Macias agregó algo en el Día 1 de Guadalajara"))
        notificaciones.add(Notifications(R.drawable.round_circle, "Guadalajara", "Isaac Perez marcó una actividad realizada en el To-do list de Guadalajara"))
    }

    private class AdaptadorNotificaciones: BaseAdapter{
        var notificaciones=ArrayList<Notifications>()
        var contexto: Context?=null

        constructor(contexto: Context, notificaciones: ArrayList<Notifications>){
            this.notificaciones=notificaciones
            this.contexto=contexto
        }

        override fun getCount(): Int {
            return notificaciones.size
        }

        override fun getItem(position: Int): Any {
            return notificaciones[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var notf=notificaciones[position]
            var inflador=LayoutInflater.from(contexto)
            var vista=inflador.inflate(R.layout.notificaciones_view, null)

            var img=vista.findViewById(R.id.notificaciones_img) as ImageView
            var lugar=vista.findViewById(R.id.tv_nombre_notificaciones) as TextView
            var desc=vista.findViewById(R.id.tv_mensajes_notificaciones) as TextView

            img.setImageResource(notf.img)
            lugar.setText(notf.lugar)
            desc.setText(notf.descripcion)
            return vista

        }

    }




}