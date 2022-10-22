package mx.itesm.dabt.tizalertap.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import mx.itesm.dabt.tizalertap.BuildConfig
import mx.itesm.dabt.tizalertap.databinding.ActivityContactosBinding



class Contactos : AppCompatActivity() {

    private lateinit var binding: ActivityContactosBinding
    // Código para solicitar permiso de usar la ubicación
    private val CODIGO_PERMISO_GPS = 200

    // Cliente proveedor de ubicación
    private lateinit var clienteLocalizacion: FusedLocationProviderClient

    // Callback para manejar las actualizaciones de ubicación
    private lateinit var locationCallback: LocationCallback

    // Para saber si las actualizaciones están activas entre corridas de la app
    private var actualizandoPosicion: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "TizAlerta"

        recuperarActualizandoPosicion(savedInstanceState)

        // Handler de actualizaciones. La función se ejecuta cada vez que hay una nueva posición
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val posicion = locationResult.locations.last()
                println("Nueva ubicación: $posicion")
                if (posicion != null) {
                    registrarEventos(posicion)
                    //mostrarMapa(posicion)
                    detenerActualizacionesPosicion()
                }
            }
        }
    }

    private fun registrarEventos(posicion: Location) {
        binding.llamarB.setOnClickListener { mostrarAdvertencia("(55)36221004") }
        binding.llamarM.setOnClickListener { mostrarAdvertencia("(55)58222547") }
        binding.llamarS.setOnClickListener { mostrarAdvertencia("(55)36222730") }
        binding.llamarPC.setOnClickListener { mostrarAdvertencia("(55)53581378") }

        binding.ubicacionB.setOnClickListener { mostrarUbicacion("bomberos", posicion) }
        binding.ubicacionM.setOnClickListener { mostrarUbicacion("cruz roja", posicion) }
        binding.ubicacionS.setOnClickListener { mostrarUbicacion("policia", posicion) }
        binding.ubicacionPC.setOnClickListener { mostrarUbicacion("proteccion civil", posicion) }
    }

    private fun mostrarAdvertencia(phonenumber: String) {
        val alerta = AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage("¿Seguro que quiere hacer la llamada de emergencia?")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { _, _ ->
                val callIntenet = Intent(Intent.ACTION_CALL)
                callIntenet.data = Uri.parse("tel:$phonenumber")
                startActivity(callIntenet)
            }
            .setNegativeButton("Cancelar"){_, _ -> }
        alerta.show() //lo hace visible
    }

    private fun mostrarUbicacion(busqueda: String, posicion: Location) {
        val uri = Uri.parse("geo:$posicion?q=$busqueda")
        val intMapa = Intent(Intent.ACTION_VIEW, uri)
        intMapa.setPackage("com.google.android.apps.maps")
        startActivity(intMapa)
    }


    private fun recuperarActualizandoPosicion(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        if (savedInstanceState.containsKey("ActualizandoPosicion")) {
            actualizandoPosicion = savedInstanceState.getBoolean("ActualizandoPosicion")
        }
    }

    private fun configurarGPS() {
        if (tienePermiso()) {
            iniciarActualizacionesPosicion()
        } else {
            solicitarPermisos()
        }
        leerUltimaUbicacion()
    }

    private fun leerUltimaUbicacion() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        clienteLocalizacion.lastLocation
            .addOnSuccessListener { location: Location? ->
                println("Ultima ubicación: $location")
            }
    }

    private fun solicitarPermisos() {
        val requiereJustificacion = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (requiereJustificacion) {
            mostrarDialogo()
        } else { // Pedir el permiso directo
            pedirPermisoUbicacion()
        }
    }

    // Resultado del permiso
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CODIGO_PERMISO_GPS) {
            if (grantResults.isEmpty()) {
            } else if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                iniciarActualizacionesPosicion()
            } else {
                // Permiso negado
                val dialogo = androidx.appcompat.app.AlertDialog.Builder(this)
                dialogo.setMessage("Esta app requiere GPS, ¿Quieres habilitarlo manualmente?")
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
                        println("No hay forma de usar gps, cerrar la actividad")
                        //Deshabilitar funcionalidad
                    })
                    .setCancelable(false)
                dialogo.show()
            }
        }
    }

    private fun mostrarDialogo() {
        val dialogo = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage("Necesitamos saber tu posición para generar alertas")
            .setPositiveButton("Aceptar") { dialog, which ->
                pedirPermisoUbicacion()
            }
            .setNeutralButton("Cancelar", null)
        dialogo.show()
    }

    private fun pedirPermisoUbicacion() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), CODIGO_PERMISO_GPS
        )
    }

    @SuppressLint("MissingPermission")
    private fun iniciarActualizacionesPosicion() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 20000
            //fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        clienteLocalizacion.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        actualizandoPosicion = true
    }

    private fun tienePermiso(): Boolean {
        val estado = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return estado == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        if (actualizandoPosicion) {
            iniciarActualizacionesPosicion()
        }
    }

    override fun onStart() {
        super.onStart()
        if (! this::clienteLocalizacion.isInitialized) {
            clienteLocalizacion = LocationServices.getFusedLocationProviderClient(this)
            configurarGPS()
        }
    }

    override fun onPause() {
        super.onPause()
        detenerActualizacionesPosicion()
    }

    override fun onStop() {
        super.onStop()
        println("DETENIENDO")
    }

    private fun detenerActualizacionesPosicion() {
        println("Detiene actualizaciones")
        clienteLocalizacion.removeLocationUpdates(locationCallback)
        actualizandoPosicion = false
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState?.putBoolean("ActualizandoPosicion", actualizandoPosicion)
        super.onSaveInstanceState(outState, outPersistentState)
    }
}