package com.example.contmp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.contmp.databinding.ActivityFormTelaPrincBinding
import com.google.firebase.auth.FirebaseAuth

class FormTelaPrinc : AppCompatActivity() {

    private lateinit var binding: ActivityFormTelaPrincBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormTelaPrincBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()


        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")
        binding.BemEmail.text = email
        binding.BemName.text = displayName

        //Botão Voltar
        binding.btSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            VoltarLogin()
        }


    }

    //Intent para voltar Tela login
    private fun VoltarLogin(){
        val inten = Intent(this, FormLogin::class.java)
        startActivity(inten)
        finish()
    }
}