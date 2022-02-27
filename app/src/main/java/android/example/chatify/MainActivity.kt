package android.example.chatify

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.example.chatify.Chat.ChatMainActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile

class MainActivity : AppCompatActivity() {
    private lateinit var account: Auth0
    lateinit var code:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        code  =intent.getStringExtra("code").toString()


        account = Auth0(
            "M8YMrptgVcHkvgt9tjsGBKBkWB3qiNK2",
            "dev-6r7or1yw.us.auth0.com"
        )
        if(code == "exit")
            logout()


        val enter=findViewById<Button>(R.id.btnEnter)
        enter.setOnClickListener {
            loginWithBrowser()
        }



    }
    private fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.

        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(this, object : Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                }

                // Called when authentication completed successfully
                override fun onSuccess(result: Credentials) {

                    val accessToken = result.accessToken
                    showUserProfile(accessToken)
                }
            })
    }
    private fun showUserProfile(accessToken: String) {
        val client = AuthenticationAPIClient(account)

        // With the access token, call `userInfo` and get the profile from Auth0.
        client.userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                }

                override fun onSuccess(result: UserProfile) {
                    // We have the user's profile!
                    val email = result.email
                    val name = result.name
                    val pic=result.pictureURL
                    val status="online"
                    Toast.makeText(this@MainActivity,"Welcome Back",Toast.LENGTH_LONG).show()

                    val sharedPrefFile = "kotlinsharedpreference"
                    val sharedPreferences:SharedPreferences=getSharedPreferences(sharedPrefFile,
                        Context.MODE_PRIVATE)
                    val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                    editor.putString("name",name)
                    editor.putString("email",email)
                    editor.putString("pic",pic)
                    editor.apply()

                    val intent = Intent(this@MainActivity,ChatMainActivity::class.java)
                    intent.putExtra("name",name)
                    intent.putExtra("email",email)
                    intent.putExtra("pic",pic)
                    intent.putExtra("status",status)
                    startActivity(intent)
                }
            })
    }
    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(this, object: Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    Toast.makeText(this@MainActivity,"Waiting for You Bye ",Toast.LENGTH_LONG).show();
                }

                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                }
            })
    }
}