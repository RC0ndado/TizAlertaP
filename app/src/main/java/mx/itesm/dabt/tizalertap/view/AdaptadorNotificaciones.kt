package mx.itesm.dabt.tizalertap.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mx.itesm.dabt.tizalertap.model.DataNotificacion
import androidx.recyclerview.widget.RecyclerView
import mx.itesm.dabt.tizalertap.R

class AdaptadorNotificaciones(val context: Context,
                             var arrNotificaciones: Array<DataNotificacion>,
                             val onClickListener: OnClickListener) :
    RecyclerView.Adapter<AdaptadorNotificaciones.RenglonNotificaciones>(){


    interface OnClickListener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RenglonNotificaciones {
        val vista = LayoutInflater.from(context)
            .inflate(R.layout.renglon_notificaciones, parent, false)
        return RenglonNotificaciones(vista)
    }

    override fun onBindViewHolder(holder: RenglonNotificaciones, position: Int) {
        val notificacion = arrNotificaciones[position]
        holder.itemView.setOnClickListener{
            onClickListener.onItemClick(position)
        }
        holder.set(notificacion)
    }

    override fun getItemCount(): Int {
        return arrNotificaciones.size
    }

    class RenglonNotificaciones(var renglonNotificaciones: View): RecyclerView.ViewHolder(renglonNotificaciones) {
        fun set(notificacion: DataNotificacion){
            val tvTituloN = renglonNotificaciones.findViewById<TextView>(R.id.tvTituloN)
            val tvDescripcion = renglonNotificaciones.findViewById<TextView>(R.id.tvDesc)
            val tvFecha = renglonNotificaciones.findViewById<TextView>(R.id.tvFecha)
            val tvHora = renglonNotificaciones.findViewById<TextView>(R.id.tvHora)
            tvTituloN.text = notificacion.titulo
            tvDescripcion.text = notificacion.descripcion
            tvFecha.text = "Fecha: " + notificacion.fecha
            tvHora.text = "Hora: " + notificacion.hora
        }

    }

}
