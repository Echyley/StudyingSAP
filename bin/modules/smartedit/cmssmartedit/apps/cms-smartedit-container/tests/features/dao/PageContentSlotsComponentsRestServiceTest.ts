/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageContentSlotsComponentsRestService } from 'cmssmarteditcontainer/dao/PageContentSlotsComponentsRestServiceOuter';
import { annotationService, GatewayProxied } from 'smarteditcommons';

describe('PageContentSlotsComponentsRestService - ', () => {
    it('checks GatewayProxied', () => {
        const decoratorObj = annotationService.getClassAnnotation(
            PageContentSlotsComponentsRestService,
            GatewayProxied
        );
        expect(decoratorObj).toEqual([
            'clearCache',
            'getSlotsToComponentsMapForPageUid',
            'getComponentsForSlot'
        ]);
    });
});
