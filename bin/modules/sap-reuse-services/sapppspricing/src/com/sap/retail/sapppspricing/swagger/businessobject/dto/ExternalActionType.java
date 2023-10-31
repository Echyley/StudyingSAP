/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Contains all information specified by a price derivation rule of type \&quot;External Action\&quot;.
 */
@Schema(description = "Contains all information specified by a price derivation rule of type \"External Action\".")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class ExternalActionType {
  @JsonProperty("Text")
  private List<ExternalActionTextType> text = null;

  @JsonProperty("Parameter")
  private List<ExternalActionParameterType> parameter = null;

  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("ID")
  private String ID = null;

  public ExternalActionType text(List<ExternalActionTextType> text) {
    this.text = text;
    return this;
  }

  public ExternalActionType addTextItem(ExternalActionTextType textItem) {
    if (this.text == null) {
      this.text = new ArrayList<ExternalActionTextType>();
    }
    this.text.add(textItem);
    return this;
  }

   /**
   * Get text
   * @return text
  **/
  @Schema(description = "")
  public List<ExternalActionTextType> getText() {
    return text;
  }

  public void setText(List<ExternalActionTextType> text) {
    this.text = text;
  }

  public ExternalActionType parameter(List<ExternalActionParameterType> parameter) {
    this.parameter = parameter;
    return this;
  }

  public ExternalActionType addParameterItem(ExternalActionParameterType parameterItem) {
    if (this.parameter == null) {
      this.parameter = new ArrayList<ExternalActionParameterType>();
    }
    this.parameter.add(parameterItem);
    return this;
  }

   /**
   * Get parameter
   * @return parameter
  **/
  @Schema(description = "")
  public List<ExternalActionParameterType> getParameter() {
    return parameter;
  }

  public void setParameter(List<ExternalActionParameterType> parameter) {
    this.parameter = parameter;
  }

  public ExternalActionType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public ExternalActionType addAnyItem(Object anyItem) {
    if (this.any == null) {
      this.any = new ArrayList<Object>();
    }
    this.any.add(anyItem);
    return this;
  }

   /**
   * This is currently not supported.
   * @return any
  **/
  @Schema(description = "This is currently not supported.")
  public List<Object> getAny() {
    return any;
  }

  public void setAny(List<Object> any) {
    this.any = any;
  }

  public ExternalActionType ID(String ID) {
    this.ID = ID;
    return this;
  }

   /**
   * Defines the external action.
   * @return ID
  **/
  @Schema(required = true, description = "Defines the external action.")
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
    ExternalActionType externalActionType = (ExternalActionType) o;
    return Objects.equals(this.text, externalActionType.text) &&
        Objects.equals(this.parameter, externalActionType.parameter) &&
        Objects.equals(this.any, externalActionType.any) &&
        Objects.equals(this.ID, externalActionType.ID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, parameter, any, ID);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalActionType {\n");
    
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    parameter: ").append(toIndentedString(parameter)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
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
