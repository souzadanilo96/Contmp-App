package com.example.contmp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.contmp.databinding.ActivityFormLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FormLogin : AppCompatActivity() {

    private lateinit var binding: ActivityFormLoginBinding
    private lateinit var googleSigninClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        VerificarUserLogado()


        //Autenticação Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //Autenticação Google
        googleSigninClient = GoogleSignIn.getClient(this, gso)

        //Intent para a tela cadastro
        binding.txtTelaCadastro.setOnClickListener {
            val intent = Intent(this,FormCadastro::class.java)
            startActivity(intent)
        }

        //Botão entrar com Login padrão Email-Senha
        binding.btEntrar.setOnClickListener {

            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val erroms = binding.mensagemErro

            if (email.isEmpty() || senha.isEmpty()){
                erroms.setText("Preencha todos os campos!")
            }else{
                AutenticarUsuario()
            }
        }

        //Autenticação Google
        binding.btGoog.setOnClickListener {
            SignIn()
        }

    }

    //Autenticação Google
    private fun SignIn(){
        val intent = googleSigninClient.signInIntent
        abreActivity.launch(intent)
    }

    var abreActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            handleResult(task)
        }
    }

    //Verificação
    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(baseContext, task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credencial = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener {
            if (it.isSuccessful){
                val intent: Intent = Intent(this, FormTelaPrinc::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
            }else{
                Toast.makeText(baseContext, it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    //Autenticar Login padrão Email-Senha
    private fun AutenticarUsuario(){

        val email = binding.editEmail.text.toString()
        val senha = binding.editSenha.text.toString()
        val erroms = binding.mensagemErro

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this,"Login efetuado com sucesso!",Toast.LENGTH_LONG).show()
                intent.putExtra("email", FirebaseAuth.getInstance().currentUser?.email.toString())
                intent.putExtra("name", FirebaseAuth.getInstance().currentUser?.displayName.toString())
                IrparaTelaPrinc()
            }
        }.addOnFailureListener {

            var erro = it
            when{
                erro is FirebaseAuthInvalidCredentialsException -> erroms.setText("E-mail e Senha estão incorretos")
                erro is FirebaseNetworkException -> erroms.setText("Sem conexão com a internet!")
                else ->erroms.setText("Erro ao logar usuário")
            }
        }
    }

    //Verificar se está logado com Login padrão Email-Senha
    private fun VerificarUserLogado(){
        val usuarioLogado = FirebaseAuth.getInstance().currentUser

        if (usuarioLogado != null){
            IrparaTelaPrinc()
        }

    }

    //Intent para a Tela principal
    private fun IrparaTelaPrinc(){
        val intent = Intent(this,FormTelaPrinc::class.java)
        startActivity(intent)
        finish()
    }
}

