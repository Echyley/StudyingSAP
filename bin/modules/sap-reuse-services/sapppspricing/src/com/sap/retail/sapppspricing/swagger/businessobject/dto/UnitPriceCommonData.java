/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Objects;
/**
 * Depending on the usage, this might refer to one single unit or the total quantity of the line item.
 */
@Schema(description = "Depending on the usage, this might refer to one single unit or the total quantity of the line item.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class UnitPriceCommonData {
  @JsonProperty("value")
  private BigDecimal value = null;

  @JsonProperty("Currency")
  private String currency = null;

  public UnitPriceCommonData value(BigDecimal value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * minimum: 0
   * @return value
  **/
  @Schema(description = "")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public UnitPriceCommonData currency(String currency) {
    this.currency = currency;
    return this;
  }

   /**
   * Uppercase currency is expected in uppercase ISO format.
   * @return currency
  **/
  @Schema(description = "Uppercase currency is expected in uppercase ISO format.")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnitPriceCommonData unitPriceCommonData = (UnitPriceCommonData) o;
    return Objects.equals(this.value, unitPriceCommonData.value) &&
        Objects.equals(this.currency, unitPriceCommonData.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, currency);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UnitPriceCommonData {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
