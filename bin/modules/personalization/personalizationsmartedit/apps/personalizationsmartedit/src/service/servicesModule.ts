/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { IPersonalizationsmarteditContextServiceProxy } from 'personalizationcommons';
import { moduleUtils } from 'smarteditcommons';
import { PersonalizationsmarteditComponentHandlerService } from './PersonalizationsmarteditComponentHandlerService';
import { PersonalizationsmarteditContextService } from './PersonalizationsmarteditContextServiceInner';
import { PersonalizationsmarteditContextServiceProxy } from './PersonalizationsmarteditContextServiceInnerProxy';
import { PersonalizationsmarteditContextServiceReverseProxy } from './PersonalizationsmarteditContextServiceInnerReverseProxy';
import { PersonalizationsmarteditContextualMenuService } from './PersonalizationsmarteditContextualMenuService';
import { PersonalizationsmarteditCustomizeViewHelper } from './PersonalizationsmarteditCustomizeViewHelper';
import { PersonalizationsmarteditRestService } from './PersonalizationsmarteditRestService';

@NgModule({
    providers: [
        PersonalizationsmarteditContextService,
        PersonalizationsmarteditRestService,
        PersonalizationsmarteditCustomizeViewHelper,
        PersonalizationsmarteditContextualMenuService,
        PersonalizationsmarteditComponentHandlerService,
        PersonalizationsmarteditContextServiceReverseProxy,
        {
            provide: IPersonalizationsmarteditContextServiceProxy,
            useClass: PersonalizationsmarteditContextServiceProxy
        },
        moduleUtils.initialize(
            (contextualMenuService: PersonalizationsmarteditContextualMenuService) =>
                contextualMenuService.updateWorkflowStatus(),
            [PersonalizationsmarteditContextualMenuService]
        )
    ]
})
export class servicesModule {}
