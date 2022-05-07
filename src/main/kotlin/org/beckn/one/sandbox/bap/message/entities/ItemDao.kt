package org.beckn.one.sandbox.bap.message.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.beckn.protocol.schemas.Default

data class ItemDao @Default constructor(
  val id: String? = null,
  val parentItemId: String? = null,
  val descriptor: DescriptorDao? = null,
  val price: PriceDao? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: TimeDao? = null,
  val matched: Boolean? = null,
  val related: Boolean? = null,
  val recommended: Boolean? = null,
  val tags: Map<String, String>? = null,
  @JsonProperty("./ondc-returnable") val ondcReturnable: Boolean? = true,
  @JsonProperty("./ondc-cancellable") val ondcCancellable: Boolean? = true,
  @JsonProperty("./ondc-seller_pickup_return") val ondcSellerPickupReturn: Boolean? = true,
  @JsonProperty("./ondc-return_window") val ondcReturnWindow: String?,
  @JsonProperty("./ondc-time_to_ship") val ondcTimeToShip: String? = null,
  @JsonProperty("./ondc-available_on_cod") val ondcAvailableOnCod: Boolean? = true,
  @JsonProperty("./ondc-statutory_reqs_packaged_commodities") val ondcStatutoryPackagedCommodities: OndcStatutoryPackagedCommoditiesDao?,
  @JsonProperty("./ondc-statutory_reqs_prepackaged_food") val ondcStatutoryPackagedFood: OndcStatutoryPackagedFoodDao?,
)

data class OndcStatutoryPackagedCommoditiesDao @Default constructor(
  val commonOrGenericNameOfCommodity: String? = null,
  val contactDetailsConsumerCare: String? = null,
  val importedProductCountryOfOrigin: String? = null,
  val manufacturerOrPackerAddress: String? = null,
  val manufacturerOrPackerName: String? = null,
  val monthYearOfManufacturePackingImport: String? = null,
  val multipleProductsNameNumberOrQty: String? = null,
  val netQuantityOrMeasureOfCommodityInPkg: String? = null,
)

data class OndcStatutoryPackagedFoodDao @Default constructor(
  @JsonProperty("additives_info") val additivesInfo: String? = null,
  @JsonProperty("brand_owner_address") val brandOwnerAddress: String? = null,
  @JsonProperty("brand_owner_FSSAI_license_no") val brandOwnerFssaiLicenseNo: String? = null,
  @JsonProperty("brand_owner_FSSAI_logo") val brandOwnerFssaiLogo: String? = null,
  @JsonProperty("brand_owner_name") val brandOwnerName: String? = null,
  @JsonProperty("contact_details_consumer_care") val contactDetailsConsumerCare: String? = null,
  @JsonProperty("imported_product_country_of_origin") val importterProductCountryOrigin: String? = null,
  @JsonProperty("importer_address") val importerAddress: String? = null,
  @JsonProperty("importer_FSSAI_license_no") val importerFssaiLicenseNo: String? = null,
  @JsonProperty("importer_FSSAI_logo") val importerFssaiLogo: String? = null,
  @JsonProperty("importer_name") val importerName: String? = null,
  @JsonProperty("ingredients_info") val ingredientsInfo: String? = null,
  @JsonProperty("manufacturer_or_packer_address") val ManufacturerPackerAddress: String? = null,
  @JsonProperty("manufacturer_or_packer_name") val ManufacturerPackerName: String? = null,
  @JsonProperty("net_quantity") val netQuantity: String? = null,
  @JsonProperty("nutritional_info") val nutritionalInfo: String? = null,
  @JsonProperty("other_FSSAI_license_no") val otherFssaiLicenseNo: String? = null,
  @JsonProperty("other_importer_address") val otherImporterAddress: String? = null,
  @JsonProperty("other_importer_country_of_origin") val otherImporterCountryOrigin: String? = null,
  @JsonProperty("other_importer_name") val otherImporterName: String? = null,
  @JsonProperty("other_premises") val otherPremises: String? = null,
)
