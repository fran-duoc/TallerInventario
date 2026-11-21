package com.taller.inventario.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Modelos para la API externa de la NHTSA (National Highway Traffic Safety Administration).
 * Proporciona especificaciones técnicas de vehículos, motorización y modelos para compatibilidad de repuestos.
 */
data class NhtsaVinResponse(
    @SerializedName("Count") val count: Int?,
    @SerializedName("Message") val message: String?,
    @SerializedName("Results") val results: List<NhtsaVinItem>?
)

data class NhtsaVinItem(
    @SerializedName("VIN") val vin: String?,
    @SerializedName("Make") val make: String?,
    @SerializedName("Model") val model: String?,
    @SerializedName("ModelYear") val modelYear: String?,
    @SerializedName("EngineHP") val engineHp: String?,
    @SerializedName("EngineCylinders") val engineCylinders: String?,
    @SerializedName("DisplacementL") val displacementL: String?,
    @SerializedName("FuelTypePrimary") val fuelTypePrimary: String?,
    @SerializedName("DriveType") val driveType: String?,
    @SerializedName("PlantCountry") val plantCountry: String?,
    @SerializedName("VehicleType") val vehicleType: String?,
    @SerializedName("ErrorCode") val errorCode: String?,
    @SerializedName("ErrorText") val errorText: String?
)

data class NhtsaModelsResponse(
    @SerializedName("Count") val count: Int?,
    @SerializedName("Message") val message: String?,
    @SerializedName("Results") val results: List<NhtsaModelItem>?
)

data class NhtsaModelItem(
    @SerializedName("Make_ID") val makeId: Int?,
    @SerializedName("Make_Name") val makeName: String?,
    @SerializedName("Model_ID") val modelId: Int?,
    @SerializedName("Model_Name") val modelName: String?
)
