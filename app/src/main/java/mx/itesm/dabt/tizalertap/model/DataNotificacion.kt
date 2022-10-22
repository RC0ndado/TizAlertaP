package mx.itesm.dabt.tizalertap.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataNotificacion(
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_suceso")
    val id_suceso: Int,
    @SerializedName("id_entrada")
    val id_entrada: Int,
    @SerializedName("fecha")
    val fecha: String,
    @SerializedName("hora")
    val hora: String,
    @SerializedName("titulo")
    val titulo: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("latitude")
    val latitud: Float,
    @SerializedName("longitude")
    val longitud: Float,
) : Serializable
