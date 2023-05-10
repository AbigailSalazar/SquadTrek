package mx.edu.potros.viajesengrupo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class Registrate : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
//    private val userRef = FirebaseDatabase.getInstance().getReference("Usuarios")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrate)

        auth = FirebaseAuth.getInstance()

        val tvIniciaSesion = findViewById<TextView>(R.id.registrateTextView)
        val btnRegistrate = findViewById<Button>(R.id.loginButton)
        btnRegistrate.setOnClickListener{registerUser()}

//        userRef.addChildEventListener(object : ChildEventListener {
//            override fun onCancelled(error: DatabaseError) {}
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
//            override fun onChildRemoved(snapshot: DataSnapshot) {}
//
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val usuario = snapshot.getValue(Credenciales::class.java)
////                if (usuario != null) writeMark(usuario)
//            }
//        })





//        btnRegistrate.setOnClickListener {
//            val intent = Intent(this, IniciarSesion::class.java)
//            startActivity(intent)
//        }

        tvIniciaSesion.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
        }


    }

    private fun registerUser() {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.contrasenaEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPassEditText)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // Verifica si los campos están vacíos
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifica si las contraseñas coinciden
        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Crea al usuario con su correo electrónico y contraseña
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    // Guarda la información del usuario en la base de datos
//                    val usuario = Credenciales(
//                        user?.email.toString(),
//                        password
//                    )
//                    userRef.child(user?.uid.toString()).setValue(usuario)

                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, IniciarSesion::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val exception = task.exception
                    // Muestra un mensaje de error al usuario
                    Toast.makeText(
                        this,
                        "Registro fallido: ${exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


}