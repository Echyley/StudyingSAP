/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
export interface SearchProfileActionContext {
  customizationCode: string;
  variationCode: string;
  initialOrder: any[];
  searchProfilesOrder: any[];
}

export interface SearchProfileAction {
  baseIndex?: number;
  catalog?: string;
  catalogVersion?: string;
  code?: string;
  rank?: string;
  type: string;
  searchProfileCode: string;
  searchProfileCatalog: string;
}

export interface SearchProfileFilter {
  indexTypes?: string;
  code: string;
  pageSize: number;
  currentPage: number;
}

export interface SearchProfileItem {
  id?: string;
  label?: string;
  indexType: string;
  code: string;
  name: string;
  catalogVersion: string;
}
