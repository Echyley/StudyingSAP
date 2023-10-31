/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
/**
 * Language-dependent description.
 */
@Schema(description = "Language-dependent description.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class DescriptionCommonData {
  @JsonProperty("value")
  private String value = null;

  @JsonProperty("Language")
  private String language = null;

  public DescriptionCommonData value(String value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @Schema(description = "")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public DescriptionCommonData language(String language) {
    this.language = language;
    return this;
  }

   /**
   * Provided in format ISO 639 as uppercase.
   * @return language
  **/
  @Schema(description = "Provided in format ISO 639 as uppercase.")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DescriptionCommonData descriptionCommonData = (DescriptionCommonData) o;
    return Objects.equals(this.value, descriptionCommonData.value) &&
        Objects.equals(this.language, descriptionCommonData.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, language);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DescriptionCommonData {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
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
