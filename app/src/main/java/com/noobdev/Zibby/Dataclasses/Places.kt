package com.noobdev.Zibby.Dataclasses

import androidx.annotation.StringRes
import com.noobdev.Zibby.R

data class Places(@StringRes val nameRes: Int)
val placeList = listOf(
    Places(R.string.city_islamabad),
    Places(R.string.city_lahore),
    Places(R.string.city_karachi),
    Places(R.string.city_peshawar),
    Places(R.string.city_quetta),
    Places(R.string.city_multan),
    Places(R.string.city_faisalabad),
    Places(R.string.city_rawalpindi),
    Places(R.string.city_sialkot),
    Places(R.string.city_gujranwala),
    Places(R.string.city_sukkur),
    Places(R.string.city_hyderabad),
    Places(R.string.city_mardan),
    Places(R.string.city_bahawalpur),
    Places(R.string.city_swat),
    Places(R.string.city_murree),
    Places(R.string.city_abbottabad),
    Places(R.string.city_gilgit),
    Places(R.string.city_skardu)
)
