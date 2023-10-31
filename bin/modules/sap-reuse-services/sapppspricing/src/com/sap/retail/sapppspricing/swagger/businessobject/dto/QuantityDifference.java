/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Objects;
/**
 * QuantityDifference
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class QuantityDifference {
  @JsonProperty("value")
  private BigDecimal value = null;

  @JsonProperty("Units")
  private BigDecimal units = null;

  @JsonProperty("UnitOfMeasureCode")
  private String unitOfMeasureCode = null;

  public QuantityDifference value(BigDecimal value) {
    this.value = value;
    return this;
  }

   /**
   * Must be an integer. The overall quantity is the product of value and units. Can be positive as well as negative.
   * minimum: -9999999999
   * maximum: 9999999999
   * @return value
  **/
  @Schema(description = "Must be an integer. The overall quantity is the product of value and units. Can be positive as well as negative.")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public QuantityDifference units(BigDecimal units) {
    this.units = units;
    return this;
  }

   /**
   * May be fractional. Set this to one for piece like items. The overall quantity is the product of value and units.
   * minimum: 0
   * maximum: 9999999999
   * @return units
  **/
  @Schema(description = "May be fractional. Set this to one for piece like items. The overall quantity is the product of value and units.")
  public BigDecimal getUnits() {
    return units;
  }

  public void setUnits(BigDecimal units) {
    this.units = units;
  }

  public QuantityDifference unitOfMeasureCode(String unitOfMeasureCode) {
    this.unitOfMeasureCode = unitOfMeasureCode;
    return this;
  }

   /**
   * Must match with the information specified in the promotions and regular prices. No conversion of unit of measures is done. Uppercase format.
   * @return unitOfMeasureCode
  **/
  @Schema(description = "Must match with the information specified in the promotions and regular prices. No conversion of unit of measures is done. Uppercase format.")
  public String getUnitOfMeasureCode() {
    return unitOfMeasureCode;
  }

  public void setUnitOfMeasureCode(String unitOfMeasureCode) {
    this.unitOfMeasureCode = unitOfMeasureCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuantityDifference quantityDifference = (QuantityDifference) o;
    return Objects.equals(this.value, quantityDifference.value) &&
        Objects.equals(this.units, quantityDifference.units) &&
        Objects.equals(this.unitOfMeasureCode, quantityDifference.unitOfMeasureCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, units, unitOfMeasureCode);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuantityDifference {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    units: ").append(toIndentedString(units)).append("\n");
    sb.append("    unitOfMeasureCode: ").append(toIndentedString(unitOfMeasureCode)).append("\n");
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
