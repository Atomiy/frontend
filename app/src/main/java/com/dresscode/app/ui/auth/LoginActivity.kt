
package com.dresscode.app.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dresscode.app.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.login_fragment_container, LoginFragment())
                .commit()
        }
    }
}
