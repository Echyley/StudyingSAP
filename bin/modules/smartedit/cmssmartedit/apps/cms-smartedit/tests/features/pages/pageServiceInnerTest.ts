/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageService } from 'cmssmartedit/services';
import { annotationService, GatewayProxied } from 'smarteditcommons';

describe('pageService', () => {
    it('checks GatewayProxied', () => {
        expect(annotationService.getClassAnnotation(PageService, GatewayProxied)).toEqual([]);
    });
});
