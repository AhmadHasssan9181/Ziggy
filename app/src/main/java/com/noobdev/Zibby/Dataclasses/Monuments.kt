package com.noobdev.Zibby.Dataclasses

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.noobdev.Zibby.R

data class Monuments(@StringRes val nameRes : Int,@StringRes val cityName: Int, @DrawableRes val imageRes:Int )
fun getMonuments(): List<Monuments> {
    return listOf(
        Monuments(R.string.pakistan_monument,R.string.city_islamabad,R.drawable.monument),
        Monuments(R.string.wazir_khan_mosque,R.string.city_lahore,R.drawable.wasir_khan_mosque)
    )
}
