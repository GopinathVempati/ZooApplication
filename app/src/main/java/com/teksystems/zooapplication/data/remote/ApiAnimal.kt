package com.teksystems.zooapplication.data.remote

import com.google.gson.annotations.SerializedName

data class AnimalItem(

	@SerializedName("characteristics")
	val characteristics: Characteristics,

	@SerializedName("name")
	val name: String,

	@SerializedName("locations")
	val locations: List<String>,

	@SerializedName("taxonomy")
	val taxonomy: Taxonomy
)

data class Characteristics(

	@SerializedName("most_distinctive_feature")
	val mostDistinctiveFeature: String,

	@SerializedName("prey")
	val prey: String,

	@SerializedName("habitat")
	val habitat: String,

	@SerializedName("average_litter_size")
	val averageLitterSize: String,

	@SerializedName("group_behavior")
	val groupBehavior: String,

	@SerializedName("color")
	val color: String,

	@SerializedName("lifespan")
	val lifespan: String,

	@SerializedName("estimated_population_size")
	val estimatedPopulationSize: String,

	@SerializedName("biggest_threat")
	val biggestThreat: String,

	@SerializedName("weight")
	val weight: String,

	@SerializedName("age_of_sexual_maturity")
	val ageOfSexualMaturity: String,

	@SerializedName("skin_type")
	val skinType: String,

	@SerializedName("lifestyle")
	val lifestyle: String,

	@SerializedName("number_of_species")
	val numberOfSpecies: String,

	@SerializedName("age_of_weaning")
	val ageOfWeaning: String,

	@SerializedName("name_of_young")
	val nameOfYoung: String,

	@SerializedName("gestation_period")
	val gestationPeriod: String,

	@SerializedName("top_speed")
	val topSpeed: String,

	@SerializedName("location")
	val location: String,

	@SerializedName("diet")
	val diet: String,

	@SerializedName("common_name")
	val commonName: String,

	@SerializedName("slogan")
	val slogan: String,

	@SerializedName("group")
	val group: String,

	@SerializedName("height")
	val height: String
)

data class Taxonomy(

	@SerializedName("phylum")
	val phylum: String,

	@SerializedName("genus")
	val genus: String,

	@SerializedName("scientific_name")
	val scientificName: String? = null,

	@SerializedName("family")
	val family: String,

	@SerializedName("kingdom")
	val kingdom: String,

	@SerializedName("class")
	val jsonMemberClass: String,

	@SerializedName("order")
	val order: String
)
