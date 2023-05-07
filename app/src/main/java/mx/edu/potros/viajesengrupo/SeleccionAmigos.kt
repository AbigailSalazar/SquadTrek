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

class SeleccionAmigos : AppCompatActivity() {

    var selecAmigos: ArrayList<Amigos> = ArrayList<Amigos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_amigos)


        agregarAmigos()


        var listview: ListView = findViewById(R.id.select_amigos_lv)

        var  adaptador: AdaptadorAmigos = AdaptadorAmigos(this,selecAmigos)

        listview.adapter=adaptador


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


        val selectAmigo = findViewById<ListView>(R.id.select_amigos_lv)
        selectAmigo.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, PerfilAmigo::class.java)
            startActivity(intent)
        }


    }

    fun agregarAmigos(){
        selecAmigos.add(Amigos(R.drawable.round_circle,"Eva Aldrete",""))
        selecAmigos.add(Amigos(R.drawable.round_circle,"Fátima Macias",""))
        selecAmigos.add(Amigos(R.drawable.round_circle,"Alejandro Valencia",""))
        selecAmigos.add(Amigos(R.drawable.round_circle,"Isaac Perez",""))
        selecAmigos.add(Amigos(R.drawable.round_circle,"Jesús Lares",""))
        selecAmigos.add(Amigos(R.drawable.round_circle,"Miguel Sanez",""))

    }

    private class AdaptadorAmigos: BaseAdapter {
        var amigos=ArrayList<Amigos>()
        var contexto: Context?=null

        constructor(contexto: Context, amigos: ArrayList<Amigos>){
            this.amigos=amigos
            this.contexto=contexto
        }

        override fun getCount(): Int {
            return amigos.size
        }

        override fun getItem(position: Int): Any {
            return amigos[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var amig=amigos[position]
            var inflador= LayoutInflater.from(contexto)
            var vista=inflador.inflate(R.layout.seleccion_amigos_view, null)

            var imagen=vista.findViewById(R.id.select_amigos_img) as ImageView
            var nombre=vista.findViewById(R.id.select_amigos_nombre_tv) as TextView

            imagen.setImageResource(amig.imagen)
            nombre.setText(amig.nombre)

            return vista

        }
    }


}