/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { NgModule } from "@angular/core";
import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { ISharedDataService, SeEntryModule } from "smarteditcommons";
import { LoadConfigManagerService } from "smarteditcontainer";
import { MerchandisingExperienceInterceptor } from "merchandisingsmarteditcommons";

@SeEntryModule("merchandisingsmarteditContainer")
@NgModule({
  imports: [],
  declarations: [],
  entryComponents: [],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: MerchandisingExperienceInterceptor,
      multi: true,
    },
  ],
})
export class MerchandisingSmartEditContainerModule {
  constructor(
    private loadConfigManagerService: LoadConfigManagerService,
    private sharedDataService: ISharedDataService
  ) {
    this.loadConfigManagerService.loadAsObject().then((configurations: any) => {
      this.sharedDataService.set(
        "merchandisingUrl",
        configurations.merchandisingUrl
      );
    });
  }
}
