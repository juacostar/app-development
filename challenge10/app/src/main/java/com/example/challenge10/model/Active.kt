package com.example.challenge10.model

import com.google.gson.annotations.SerializedName

data class Active
    (@SerializedName("periodo") val period: String,
    @SerializedName("nombre_del_apoyo") val name:String,
    @SerializedName("genero") val gender: String,
    @SerializedName("facultad") val school: String,
    @SerializedName("programa_academico") val program:String,
    @SerializedName("metodolog_a") val methodology: String,
   )