package mx.edu.potros.viajesengrupo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class navBar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_bar)

//
//        val btnNavAdd = findViewById<ImageButton>(R.id.btnNavAdd)
//        btnNavAdd.setOnClickListener {
//            val intent = Intent(this, SeleccionarUbicacion::class.java)
//            startActivity(intent)
//        }
//
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
//
//        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.btnViajes -> {
//                    val intent = Intent(this, Inbox::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.btnHome -> {
//                    val intent = Intent(this, Inbox::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.btnUser -> {
//                    val intent = Intent(this, Inbox::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.btnAmigos -> {
//                    val intent = Intent(this, Inbox::class.java)
//                    startActivity(intent)
//                    true
//                }
//                else -> false
//            }
//        }




    }



}