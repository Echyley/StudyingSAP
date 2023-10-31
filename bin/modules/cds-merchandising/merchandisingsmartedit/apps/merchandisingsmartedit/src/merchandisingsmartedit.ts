/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { UpgradeModule } from "@angular/upgrade/static";
import { NgModule } from "@angular/core";
import {
  IContextualMenuService,
  ISharedDataService,
  SeEntryModule,
} from "smarteditcommons";

@SeEntryModule("merchandisingsmartedit")
@NgModule({
  imports: [UpgradeModule],
  declarations: [],
  entryComponents: [],
  providers: [],
})
export class MerchandisingSmartEditModule {
  constructor(
    private contextualMenuService: IContextualMenuService,
    sharedDataService: ISharedDataService
  ) {
    this.contextualMenuService.addItems({
      MerchandisingCarouselComponent: [
        {
          key: "MerchandisingCarouselComponent",
          i18nKey: "Edit Strategy",
          action: {
            callback(configuration: any, event: any) {
              sharedDataService.get("merchandisingUrl").then(
                function (url: string) {
                  const appUrl = "https://" + url;
                  window.open(appUrl);
                }.bind(this)
              );
            },
          },
          displayClass: 'sap-icon sap-icon--activate',
        },
      ],
    });
  }
}
