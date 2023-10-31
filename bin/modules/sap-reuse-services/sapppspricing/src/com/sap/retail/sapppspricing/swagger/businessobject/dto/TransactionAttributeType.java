/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
/**
 * Attribute (key-value pair) on transaction level in the price calculation request as trigger for a promotional rule.
 */
@Schema(description = "Attribute (key-value pair) on transaction level in the price calculation request as trigger for a promotional rule.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class TransactionAttributeType {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("value")
  private String value = null;

  @JsonProperty("custom")
  private Boolean custom = null;

  public TransactionAttributeType name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @Schema(required = true, description = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TransactionAttributeType value(String value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @Schema(required = true, description = "")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public TransactionAttributeType custom(Boolean custom) {
    this.custom = custom;
    return this;
  }

   /**
   * Set to true if the attribute name is in the customer namespace.
   * @return custom
  **/
  @Schema(required = true, description = "Set to true if the attribute name is in the customer namespace.")
  public Boolean isCustom() {
    return custom;
  }

  public void setCustom(Boolean custom) {
    this.custom = custom;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionAttributeType transactionAttributeType = (TransactionAttributeType) o;
    return Objects.equals(this.name, transactionAttributeType.name) &&
        Objects.equals(this.value, transactionAttributeType.value) &&
        Objects.equals(this.custom, transactionAttributeType.custom);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value, custom);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionAttributeType {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    custom: ").append(toIndentedString(custom)).append("\n");
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
