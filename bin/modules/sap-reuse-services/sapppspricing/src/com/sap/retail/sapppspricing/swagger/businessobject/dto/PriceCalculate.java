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
 * Request sent by the client. Contains all relevant information for the price calculation.
 */
@Schema(description = "Request sent by the client. Contains all relevant information for the price calculation.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PriceCalculate {
  @JsonProperty("ARTSHeader")
  private ARTSCommonHeaderType arTSHeader = null;

  @JsonProperty("PriceCalculateBody")
  private List<PriceCalculateBase> priceCalculateBody = new ArrayList<PriceCalculateBase>();

  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("InternalMajorVersion")
  private Integer internalMajorVersion = null;

  @JsonProperty("InternalMinorVersion")
  private Integer internalMinorVersion = null;

  public PriceCalculate arTSHeader(ARTSCommonHeaderType arTSHeader) {
    this.arTSHeader = arTSHeader;
    return this;
  }

   /**
   * Get arTSHeader
   * @return arTSHeader
  **/
  @Schema(required = true, description = "")
  public ARTSCommonHeaderType getArTSHeader() {
    return arTSHeader;
  }

  public void setArTSHeader(ARTSCommonHeaderType arTSHeader) {
    this.arTSHeader = arTSHeader;
  }

  public PriceCalculate priceCalculateBody(List<PriceCalculateBase> priceCalculateBody) {
    this.priceCalculateBody = priceCalculateBody;
    return this;
  }

  public PriceCalculate addPriceCalculateBodyItem(PriceCalculateBase priceCalculateBodyItem) {
    this.priceCalculateBody.add(priceCalculateBodyItem);
    return this;
  }

   /**
   * Contains the items for which a price calculation should be performed and additional information about how it is performed.
   * @return priceCalculateBody
  **/
  @Schema(required = true, description = "Contains the items for which a price calculation should be performed and additional information about how it is performed.")
  public List<PriceCalculateBase> getPriceCalculateBody() {
    return priceCalculateBody;
  }

  public void setPriceCalculateBody(List<PriceCalculateBase> priceCalculateBody) {
    this.priceCalculateBody = priceCalculateBody;
  }

  public PriceCalculate any(List<Object> any) {
    this.any = any;
    return this;
  }

  public PriceCalculate addAnyItem(Object anyItem) {
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

  public PriceCalculate internalMajorVersion(Integer internalMajorVersion) {
    this.internalMajorVersion = internalMajorVersion;
    return this;
  }

   /**
   * Major version of the Client API version to be used. For Client API version X.Y, this field corresponds to the X part. Currently allowed versions of the Client API are 1.0, 2.0, 2.1., 3.0, 4.0, 5.0, 6.0. In a cloud environment, only version 2.1 or higher may be used. 
   * minimum: 1
   * maximum: 6
   * @return internalMajorVersion
  **/
  @Schema(example = "3", required = true, description = "Major version of the Client API version to be used. For Client API version X.Y, this field corresponds to the X part. Currently allowed versions of the Client API are 1.0, 2.0, 2.1., 3.0, 4.0, 5.0, 6.0. In a cloud environment, only version 2.1 or higher may be used. ")
  public Integer getInternalMajorVersion() {
    return internalMajorVersion;
  }

  public void setInternalMajorVersion(Integer internalMajorVersion) {
    this.internalMajorVersion = internalMajorVersion;
  }

  public PriceCalculate internalMinorVersion(Integer internalMinorVersion) {
    this.internalMinorVersion = internalMinorVersion;
    return this;
  }

   /**
   * Minor version of the Client API version to be used. For Client API version X.Y, this field corresponds to the Y part.
   * minimum: 0
   * maximum: 1
   * @return internalMinorVersion
  **/
  @Schema(example = "0", description = "Minor version of the Client API version to be used. For Client API version X.Y, this field corresponds to the Y part.")
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
    PriceCalculate priceCalculate = (PriceCalculate) o;
    return Objects.equals(this.arTSHeader, priceCalculate.arTSHeader) &&
        Objects.equals(this.priceCalculateBody, priceCalculate.priceCalculateBody) &&
        Objects.equals(this.any, priceCalculate.any) &&
        Objects.equals(this.internalMajorVersion, priceCalculate.internalMajorVersion) &&
        Objects.equals(this.internalMinorVersion, priceCalculate.internalMinorVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arTSHeader, priceCalculateBody, any, internalMajorVersion, internalMinorVersion);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceCalculate {\n");
    
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
