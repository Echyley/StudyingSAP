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
 * Contains the response of the price calculation returned by the server.
 */
@Schema(description = "Contains the response of the price calculation returned by the server.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PriceCalculateResponse {
  @JsonProperty("ARTSHeader")
  private ARTSCommonHeaderType arTSHeader = null;

  @JsonProperty("PriceCalculateBody")
  private List<PriceCalculateBase> priceCalculateBody = null;

  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("InternalMajorVersion")
  private Integer internalMajorVersion = null;

  @JsonProperty("InternalMinorVersion")
  private Integer internalMinorVersion = null;

  public PriceCalculateResponse arTSHeader(ARTSCommonHeaderType arTSHeader) {
    this.arTSHeader = arTSHeader;
    return this;
  }

   /**
   * Get arTSHeader
   * @return arTSHeader
  **/
  @Schema(description = "")
  public ARTSCommonHeaderType getArTSHeader() {
    return arTSHeader;
  }

  public void setArTSHeader(ARTSCommonHeaderType arTSHeader) {
    this.arTSHeader = arTSHeader;
  }

  public PriceCalculateResponse priceCalculateBody(List<PriceCalculateBase> priceCalculateBody) {
    this.priceCalculateBody = priceCalculateBody;
    return this;
  }

  public PriceCalculateResponse addPriceCalculateBodyItem(PriceCalculateBase priceCalculateBodyItem) {
    if (this.priceCalculateBody == null) {
      this.priceCalculateBody = new ArrayList<PriceCalculateBase>();
    }
    this.priceCalculateBody.add(priceCalculateBodyItem);
    return this;
  }

   /**
   * Contains the items for which a price calculation should be performed and additional information about how the it is performed.
   * @return priceCalculateBody
  **/
  @Schema(description = "Contains the items for which a price calculation should be performed and additional information about how the it is performed.")
  public List<PriceCalculateBase> getPriceCalculateBody() {
    return priceCalculateBody;
  }

  public void setPriceCalculateBody(List<PriceCalculateBase> priceCalculateBody) {
    this.priceCalculateBody = priceCalculateBody;
  }

  public PriceCalculateResponse any(List<Object> any) {
    this.any = any;
    return this;
  }

  public PriceCalculateResponse addAnyItem(Object anyItem) {
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

  public PriceCalculateResponse internalMajorVersion(Integer internalMajorVersion) {
    this.internalMajorVersion = internalMajorVersion;
    return this;
  }

   /**
   * The same value as contained in the request.
   * @return internalMajorVersion
  **/
  @Schema(required = true, description = "The same value as contained in the request.")
  public Integer getInternalMajorVersion() {
    return internalMajorVersion;
  }

  public void setInternalMajorVersion(Integer internalMajorVersion) {
    this.internalMajorVersion = internalMajorVersion;
  }

  public PriceCalculateResponse internalMinorVersion(Integer internalMinorVersion) {
    this.internalMinorVersion = internalMinorVersion;
    return this;
  }

   /**
   * The same value as contained in the request.
   * @return internalMinorVersion
  **/
  @Schema(description = "The same value as contained in the request.")
  public Integer getInternalMinorVersion() {
    return internalMinorVersion;
  }

  public void setInternalMinorVersion(Integer internalMinorVersion) {
    this.internalMinorVersion = internalMinorVersion;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceCalculateResponse priceCalculateResponse = (PriceCalculateResponse) o;
    return Objects.equals(this.arTSHeader, priceCalculateResponse.arTSHeader) &&
        Objects.equals(this.priceCalculateBody, priceCalculateResponse.priceCalculateBody) &&
        Objects.equals(this.any, priceCalculateResponse.any) &&
        Objects.equals(this.internalMajorVersion, priceCalculateResponse.internalMajorVersion) &&
        Objects.equals(this.internalMinorVersion, priceCalculateResponse.internalMinorVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arTSHeader, priceCalculateBody, any, internalMajorVersion, internalMinorVersion);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceCalculateResponse {\n");
    
    sb.append("    arTSHeader: ").append(toIndentedString(arTSHeader)).append("\n");
    sb.append("    priceCalculateBody: ").append(toIndentedString(priceCalculateBody)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    internalMajorVersion: ").append(toIndentedString(internalMajorVersion)).append("\n");
    sb.append("    internalMinorVersion: ").append(toIndentedString(internalMinorVersion)).append("\n");
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
