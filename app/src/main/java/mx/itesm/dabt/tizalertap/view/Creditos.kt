package mx.itesm.dabt.tizalertap.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.itesm.dabt.tizalertap.R

class Creditos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creditos)
        supportActionBar?.title = "TizAlerta"
    }
}