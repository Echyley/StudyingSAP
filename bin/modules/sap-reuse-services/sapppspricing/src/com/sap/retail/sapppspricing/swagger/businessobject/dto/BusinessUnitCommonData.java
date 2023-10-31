/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
/**
 * An entity for which prices or promotions are valid.
 */
@Schema(description = "An entity for which prices or promotions are valid.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class BusinessUnitCommonData {
  @JsonProperty("value")
  private String value = null;

  /**
   * Specifies the type of a business unit. There may be several business units with the same identifier but different types. Default value is RetailStore. 
   */
  public enum TypeCodeEnum {
    RETAILSTORE("RetailStore"),
    CUSTOMER("Customer"),
    FACTORY("Factory"),
    DISTRIBUTIONCENTER("DistributionCenter"),
    VENDOR("Vendor"),
    DISTRIBUTIONCHAIN("DistributionChain");

    private String value;

    TypeCodeEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static TypeCodeEnum fromValue(String input) {
      for (TypeCodeEnum b : TypeCodeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("TypeCode")
  private TypeCodeEnum typeCode = null;

  public BusinessUnitCommonData value(String value) {
    this.value = value;
    return this;
  }

   /**
   * Identifies the business unit in combination with business unit type and master data source system identifier.
   * @return value
  **/
  @Schema(required = true, description = "Identifies the business unit in combination with business unit type and master data source system identifier.")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public BusinessUnitCommonData typeCode(TypeCodeEnum typeCode) {
    this.typeCode = typeCode;
    return this;
  }

   /**
   * Specifies the type of a business unit. There may be several business units with the same identifier but different types. Default value is RetailStore. 
   * @return typeCode
  **/
  @Schema(example = "RetailStore", description = "Specifies the type of a business unit. There may be several business units with the same identifier but different types. Default value is RetailStore. ")
  public TypeCodeEnum getTypeCode() {
    return typeCode;
  }

  public void setTypeCode(TypeCodeEnum typeCode) {
    this.typeCode = typeCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessUnitCommonData businessUnitCommonData = (BusinessUnitCommonData) o;
    return Objects.equals(this.value, businessUnitCommonData.value) &&
        Objects.equals(this.typeCode, businessUnitCommonData.typeCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, typeCode);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessUnitCommonData {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    typeCode: ").append(toIndentedString(typeCode)).append("\n");
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
