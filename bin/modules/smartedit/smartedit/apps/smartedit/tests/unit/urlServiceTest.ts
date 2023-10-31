/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { UrlService } from 'smartedit/services';
import { annotationService, GatewayProxied } from 'smarteditcommons';

describe('test urlService ', () => {
    it('url service inits a private gateway', function () {
        expect(annotationService.getClassAnnotation(UrlService, GatewayProxied)).toEqual([
            'openUrlInPopup',
            'path'
        ]);
    });
});
