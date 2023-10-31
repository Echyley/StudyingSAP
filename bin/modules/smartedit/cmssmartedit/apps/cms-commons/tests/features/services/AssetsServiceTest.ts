/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AssetsService } from 'cmscommons';

describe('AssetsService', () => {
    let service: AssetsService;
    beforeEach(() => {
        service = new AssetsService();
    });

    it('GIVEN prod THEN it returns prod assets', () => {
        expect(service.getAssetsRoot()).toBe('/cmssmartedit');
    });
});
