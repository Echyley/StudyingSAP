/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import { Injectable } from '@angular/core';
import { SearchProfileActionContext } from 'personalizationsearchsmarteditcontainer/types';

@Injectable()
export class SearchProfilesContextService {
  public searchProfileActionContext: SearchProfileActionContext;

  constructor() {
    this.searchProfileActionContext = {
      customizationCode: undefined,
      variationCode: undefined,
      initialOrder: [],
      searchProfilesOrder: [],
    };
  }

  public searchProfileActionComparer = (a1: any, a2: any): boolean =>
    a1.type === a2.type &&
    a1.searchProfileCode === a2.searchProfileCode &&
    a1.searchProfileCatalog === a2.searchProfileCatalog;

  public updateSearchActionContext = (actions: any): any => {
    actions.forEach((action) => {
      const searchProfileActions = this.searchProfileActionContext.searchProfilesOrder.filter(
        (spAction) => this.searchProfileActionComparer(action, spAction)
      );

      if (searchProfileActions.length > 0) {
        searchProfileActions[0].code = action.code;
      }
    });
  };

  public isDirty = (): boolean => false;
}
