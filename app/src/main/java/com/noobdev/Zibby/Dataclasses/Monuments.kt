package com.noobdev.Zibby.Dataclasses

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.noobdev.Zibby.R

data class Monuments(@StringRes val nameRes : Int,@StringRes val cityName: Int, @DrawableRes val imageRes:Int )
fun getMonuments(): List<Monuments> {
    return listOf(
        Monuments(R.string.minar_e_pakistan,R.string.city_lahore,R.drawable.minar_e_pakistan),
        Monuments(R.string.pakistan_monument,R.string.city_islamabad,R.drawable.monument),
        Monuments(R.string.badshahi_mosque,R.string.city_lahore,R.drawable.bashahi_mosque),
        Monuments(R.string.mazar_e_quaid,R.string.city_karachi,R.drawable.mizar_e_quaid),
        Monuments(R.string.wazir_khan_mosque,R.string.city_lahore,R.drawable.wasir_khan_mosque)
    )
}
