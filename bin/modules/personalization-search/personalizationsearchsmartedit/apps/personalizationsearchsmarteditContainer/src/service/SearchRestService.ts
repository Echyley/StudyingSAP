/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import { Injectable } from '@angular/core';
import { PersonalizationsmarteditUtils, SeData } from 'personalizationcommons';
import { IRestServiceFactory } from 'smarteditcommons';

@Injectable()
export class SearchRestService {
  private readonly SEARCH_PROFILES =
    "/adaptivesearchwebservices/v1/searchprofiles";
  private readonly UPDATE_CUSTOMIZATION_RANK =
    "/personalizationwebservices/v1/query/cxUpdateSearchProfileActionRank";
  private readonly GET_INDEX_TYPES_FOR_SITE =
    "/personalizationwebservices/v1/query/cxGetIndexTypesForSite";
  private seData: SeData;

  constructor(
    private personalizationsmarteditUtils: PersonalizationsmarteditUtils,
    private restServiceFactory: IRestServiceFactory
  ) {}

  public initSeData(seData: SeData): void {
    this.seData = seData;
  }

  public getSeExperienceData(): any {
    return this.seData.seExperienceData;
  }

  public getSearchProfiles(filter: any): any {
    const experienceData = this.getSeExperienceData();
    const catalogVersionsStr = (experienceData.productCatalogVersions || [])
      .map(function (cv) {
        return cv.catalog + ":" + cv.catalogVersion;
      })
      .join(",");
    const restService = this.restServiceFactory.get(this.SEARCH_PROFILES);

    filter = {
      catalogVersions: catalogVersionsStr,
      ...filter,
    };

    return restService.get(filter);
  }

  public updateSearchProfileActionRank(filter: any): any {
    const experienceData = this.getSeExperienceData();

    const restService = this.restServiceFactory.get(
      this.UPDATE_CUSTOMIZATION_RANK
    );
    const entries = [];
    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "customization",
      filter.customizationCode
    );
    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "variation",
      filter.variationCode
    );
    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "rankBeforeAction",
      filter.rankBeforeAction
    );
    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "rankAfterAction",
      filter.rankAfterAction
    );
    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "actions",
      filter.actions
    );

    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "catalog",
      experienceData.catalogDescriptor.catalogId
    );
    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "catalogVersion",
      experienceData.catalogDescriptor.catalogVersion
    );
    const requestParams = {
      params: {
        entry: entries,
      },
    };
    return restService.save(requestParams);
  }

  public getIndexTypesForCatalogVersion(productCV: any): any {
    const experienceData = this.getSeExperienceData();

    const restService = this.restServiceFactory.get(
      this.GET_INDEX_TYPES_FOR_SITE
    );
    const entries = [];

    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "baseSiteId",
      experienceData.catalogDescriptor.siteId
    );
    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "catalog",
      productCV.catalog
    );
    this.personalizationsmarteditUtils.pushToArrayIfValueExists(
      entries,
      "catalogVersion",
      productCV.catalogVersion
    );
    const requestParams = {
      params: {
        entry: entries,
      },
    };
    return restService.save(requestParams);
  }
}
