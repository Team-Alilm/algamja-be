package domain.product

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductId(@JsonProperty("value") val value: Long)