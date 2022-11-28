package com.example.contmp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.example.contmp.databinding.ActivityFormCadastroBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class FormCadastro : AppCompatActivity() {

    private lateinit var binding: ActivityFormCadastroBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        binding.btCadastrar.setOnClickListener {

            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val msgerro = binding.mensagemErro

            if (email.isEmpty() || senha.isEmpty()){
                msgerro.setText("Preencha todos os campos!")
            }else{
                CadastrarUsuario()
            }
        }
    }

    private fun CadastrarUsuario(){

        val email = binding.editEmail.text.toString()
        val senha = binding.editSenha.text.toString()
        val msgerro = binding.mensagemErro
        
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                binding.editEmail.setText("")
                binding.editSenha.setText("")
                msgerro.setText("")
                finish()
            }
        }.addOnFailureListener {

            var erro = it

            when{
                erro is FirebaseAuthWeakPasswordException -> msgerro.setText("Digite uma senha com no mínimo  6 caracteres")
                erro is FirebaseAuthUserCollisionException -> msgerro.setText("Esta conta já foi cadastrada")
                erro is FirebaseNetworkException -> msgerro.setText("Sem conexão com a internet")
                else -> msgerro.setText("Erro ao cadastrar usuário!")
            }

        }

    }
}