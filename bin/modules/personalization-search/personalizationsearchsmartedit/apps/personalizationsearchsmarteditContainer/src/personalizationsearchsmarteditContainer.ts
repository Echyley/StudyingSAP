/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import { DragDropModule } from "@angular/cdk/drag-drop";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { PopoverModule } from "@fundamental-ngx/core";
import { TranslateService } from "@ngx-translate/core";
import {
  PersonalizationsmarteditCommerceCustomizationService,
  PersonalizationsmarteditCommonsComponentsModule,
  PersonalizationsmarteditMessageHandler,
  PERSONLIZATION_SEARCH_EXTENSION_TOKEN,
} from "personalizationcommons";
import { SearchProfilesSmarteditComponent } from "personalizationsearchsmarteditcontainer/searchSmarteditComponent/SearchProfilesSmarteditComponent";
import {
  SearchProfilesContextService,
  SearchRestService,
} from "personalizationsearchsmarteditcontainer/service";
import { SEARCH_PROFILE_ACTION_TYPE } from "personalizationsearchsmarteditcontainer/utils/constants";
import {
  SeEntryModule,
  promiseUtils,
  moduleUtils,
  TranslationModule,
  SelectModule,
  SharedComponentsModule,
} from "smarteditcommons";
import "../../styling/style.less";

@SeEntryModule("personalizationsearchsmarteditContainer")
@NgModule({
  imports: [
    TranslationModule.forChild(),
    SelectModule,
    PopoverModule,
    BrowserModule,
    SharedComponentsModule,
    DragDropModule,
    PersonalizationsmarteditCommonsComponentsModule,
  ],
  declarations: [SearchProfilesSmarteditComponent],
  entryComponents: [SearchProfilesSmarteditComponent],
  exports: [],
  providers: [
    SearchProfilesContextService,
    SearchRestService,
    {
      provide: PERSONLIZATION_SEARCH_EXTENSION_TOKEN,
      useValue: {
        component: SearchProfilesSmarteditComponent,
      },
    },
    moduleUtils.bootstrap(
      (
        translateService: TranslateService,
        searchProfilesContextService: SearchProfilesContextService,
        searchRestService: SearchRestService,
        personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
        personalizationsmarteditCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService
      ) => {
        personalizationsmarteditCommerceCustomizationService.registerType({
          type: SEARCH_PROFILE_ACTION_TYPE,
          text:
            "personalizationsearchsmartedit.commercecustomization.action.type.search",
          confProperty:
            "personalizationsearch.commercecustomization.search.profile.enabled",
          getName: (action) =>
            translateService.instant(
              "personalizationsearchsmartedit.commercecustomization.search.display.name"
            ) +
            " - " +
            action.searchProfileCode,
          updateActions(customizationCode, variationCode, actions, respCreate) {
            const deferred = promiseUtils.defer();

            if (respCreate !== undefined) {
              searchProfilesContextService.updateSearchActionContext(
                respCreate.actions
              );
            }

            const searchProfilesCtx =
              searchProfilesContextService.searchProfileActionContext;
            const rankAfterAction = searchProfilesCtx.searchProfilesOrder.splice(
              0,
              1
            )[0];
            const spActionCodes = searchProfilesCtx.searchProfilesOrder
              .map(function (sp) {
                return sp.code;
              })
              .join(",");

            const filter = {
              customizationCode: searchProfilesCtx.customizationCode,
              variationCode: searchProfilesCtx.variationCode,
              rankAfterAction: rankAfterAction.code,
              actions: spActionCodes,
            };

            searchRestService.updateSearchProfileActionRank(filter).then(
              function successCallback() {
                deferred.resolve();
              },
              function errorCallback() {
                personalizationsmarteditMessageHandler.sendError(
                  this.translateService.instant(
                    "personalization.error.updatingcustomization"
                  )
                );
                deferred.reject();
              }
            );

            return deferred.promise;
          },
        });
      },
      [
        TranslateService,
        SearchProfilesContextService,
        SearchRestService,
        PersonalizationsmarteditMessageHandler,
        PersonalizationsmarteditCommerceCustomizationService,
      ]
    ),
  ],
})
export class PersonalizationsearchSmarteditContainerModule {}
