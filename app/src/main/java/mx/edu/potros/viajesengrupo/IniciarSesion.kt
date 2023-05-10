package mx.edu.potros.viajesengrupo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mx.edu.potros.viajesengrupo.databinding.ActivityIniciarSesionBinding

class IniciarSesion : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityIniciarSesionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityIniciarSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.loginButton.setOnClickListener{
            val mEmail=binding.emailEditText.text.toString()
            val mPassword=binding.contrasenaEditText.text.toString()

            when{
                mEmail.isEmpty() || mPassword.isEmpty()->{
                    Toast.makeText(baseContext, "Mail o contraseña incorrecta.",
                        Toast.LENGTH_SHORT).show()
                } else ->{
                SignIn(mEmail, mPassword)
            }
            }
        }

        val tvRegistrate = findViewById<TextView>(R.id.registrateTextView)
//        val btnLogin = findViewById<Button>(R.id.loginButton)

//        btnLogin.setOnClickListener {
//            val intent = Intent(this, Home::class.java)
//            startActivity(intent)
//        }
//
        tvRegistrate.setOnClickListener {
            val intent = Intent(this, Registrate::class.java)
            startActivity(intent)
        }
    }

    private fun SignIn(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    Log.d("TAG", "signInWithEmail:success")
                    reaload()
                } else {
                    Log.w("TAG", "signInwithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun reaload(){
        val intent= Intent(this, Home::class.java)
        this.startActivity(intent)
    }
}