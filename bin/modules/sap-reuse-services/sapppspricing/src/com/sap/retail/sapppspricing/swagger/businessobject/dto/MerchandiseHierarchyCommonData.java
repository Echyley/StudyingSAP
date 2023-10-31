/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
/**
 * Unique in combination with the master data source system ID.
 */
@Schema(description = "Unique in combination with the master data source system ID.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class MerchandiseHierarchyCommonData {
  @JsonProperty("value")
  private String value = null;

  @JsonProperty("ID")
  private String ID = null;

  public MerchandiseHierarchyCommonData value(String value) {
    this.value = value;
    return this;
  }

   /**
   * Requires ID field to be unique.
   * @return value
  **/
  @Schema(description = "Requires ID field to be unique.")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public MerchandiseHierarchyCommonData ID(String ID) {
    this.ID = ID;
    return this;
  }

   /**
   * Several hierarchies may exist in parallel. In one request, at the most 2 different hierarchy identifiers can be specified.
   * @return ID
  **/
  @Schema(description = "Several hierarchies may exist in parallel. In one request, at the most 2 different hierarchy identifiers can be specified.")
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
    MerchandiseHierarchyCommonData merchandiseHierarchyCommonData = (MerchandiseHierarchyCommonData) o;
    return Objects.equals(this.value, merchandiseHierarchyCommonData.value) &&
        Objects.equals(this.ID, merchandiseHierarchyCommonData.ID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, ID);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MerchandiseHierarchyCommonData {\n");
    
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
