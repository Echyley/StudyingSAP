/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    Customize,
    CombinedView,
    SeData,
    Personalization,
    IPersonalizationsmarteditContextServiceProxy
} from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import { CrossFrameEventService, GatewayProxied } from 'smarteditcommons';

@GatewayProxied('setPersonalization', 'setCustomize', 'setCombinedView', 'setSeData')
@Injectable()
export class PersonalizationsmarteditContextServiceProxy extends IPersonalizationsmarteditContextServiceProxy {
    constructor(
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        protected crossFrameEventService: CrossFrameEventService
    ) {
        super();
    }

    setPersonalization(newPersonalization: Personalization): void {
        this.personalizationsmarteditContextService.setPersonalization(newPersonalization);
    }

    setCustomize(newCustomize: Customize): void {
        this.personalizationsmarteditContextService.setCustomize(newCustomize);
        this.crossFrameEventService.publish('PERSONALIZATION_CUSTOMIZE_CONTEXT_SYNCHRONIZED');
    }

    setCombinedView(newCombinedView: CombinedView): void {
        this.personalizationsmarteditContextService.setCombinedView(newCombinedView);
        this.crossFrameEventService.publish('PERSONALIZATION_COMBINEDVIEW_CONTEXT_SYNCHRONIZED');
    }

    setSeData(newSeData: SeData): void {
        this.personalizationsmarteditContextService.setSeData(newSeData);
    }
}
