package com.noobdev.Zibby.Dataclasses

data class ORSResponse(
    val features: List<FeatureData>
)

data class FeatureData(
    val geometry: GeometryData
)

data class GeometryData(
    val coordinates: List<List<Double>>
)

data class ORSRequestBody(
    val coordinates: List<List<Double>>
)

