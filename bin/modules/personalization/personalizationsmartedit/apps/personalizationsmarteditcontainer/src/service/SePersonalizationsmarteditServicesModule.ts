/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
    IPersonalizationsmarteditContextServiceProxy,
    PersonalizationsmarteditCommonsComponentsModule
} from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditContextServiceProxy } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuterProxy';
import { PersonalizationsmarteditContextServiceReverseProxy } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuterReverseProxy';
import { PersonalizationsmarteditPreviewService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditPreviewService';
import { PersonalizationsmarteditRestService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService';
import { PersonalizationsmarteditRulesAndPermissionsRegistrationService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditRulesAndPermissionsRegistrationService';
import { PersonalizationsmarteditVersionCheckerService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditVersionCheckerService';
import { moduleUtils } from 'smarteditcommons';
@NgModule({
    imports: [CommonModule, PersonalizationsmarteditCommonsComponentsModule],
    providers: [
        {
            provide: IPersonalizationsmarteditContextServiceProxy,
            useClass: PersonalizationsmarteditContextServiceProxy
        },
        PersonalizationsmarteditContextService,
        PersonalizationsmarteditRestService,
        PersonalizationsmarteditPreviewService,
        PersonalizationsmarteditRulesAndPermissionsRegistrationService,
        PersonalizationsmarteditContextServiceReverseProxy,
        PersonalizationsmarteditVersionCheckerService,
        moduleUtils.bootstrap(
            (
                personalizationsmarteditRulesAndPermissionsRegistrationService: PersonalizationsmarteditRulesAndPermissionsRegistrationService
            ) => {
                personalizationsmarteditRulesAndPermissionsRegistrationService.registerRules();
            },
            [PersonalizationsmarteditRulesAndPermissionsRegistrationService]
        )
    ]
})
export class SePersonalizationsmarteditServicesModule {}
