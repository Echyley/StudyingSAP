/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
/**
 * Specifies the value of an external action parameter if the applied price derivation rule has type \&quot;External Action\&quot;.
 */
@Schema(description = "Specifies the value of an external action parameter if the applied price derivation rule has type \"External Action\".")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class ExternalActionParameterType {
  @JsonProperty("value")
  private String value = null;

  @JsonProperty("ID")
  private String ID = null;

  public ExternalActionParameterType value(String value) {
    this.value = value;
    return this;
  }

   /**
   * The value as stored in the promotion master data.
   * @return value
  **/
  @Schema(description = "The value as stored in the promotion master data.")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ExternalActionParameterType ID(String ID) {
    this.ID = ID;
    return this;
  }

   /**
   * Identifier as stored in the promotion master data.
   * @return ID
  **/
  @Schema(required = true, description = "Identifier as stored in the promotion master data.")
  public String getID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalActionParameterType externalActionParameterType = (ExternalActionParameterType) o;
    return Objects.equals(this.value, externalActionParameterType.value) &&
        Objects.equals(this.ID, externalActionParameterType.ID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, ID);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalActionParameterType {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    ID: ").append(toIndentedString(ID)).append("\n");
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
