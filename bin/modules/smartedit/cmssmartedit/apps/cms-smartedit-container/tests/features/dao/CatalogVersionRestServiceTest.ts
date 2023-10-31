/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';

import { ICloneableCatalogVersion } from 'cmscommons';
import { CatalogVersionRestService } from 'cmssmarteditcontainer/dao/CatalogVersionRestService';
import { IRestService, IUriContext, RestServiceFactory } from 'smarteditcommons';

describe('CatalogVersionRestService', () => {
    const mockUriContext: IUriContext = {
        CURRENT_CONTEXT_CATALOG: 'apparel-deContentCatalog',
        CURRENT_CONTEXT_CATALOG_VERSION: 'Staged',
        CURRENT_CONTEXT_SITE_ID: 'apparel-de'
    };

    const mockCatalogVersions: ICloneableCatalogVersion = {
        versions: [
            {
                pageDisplayConditions: null,
                active: false,
                name: { de: 'Deutscher Produktkatalog Kleidung - Staged' },
                uuid: 'apparel-deContentCatalog/Staged',
                version: 'Staged'
            }
        ]
    };

    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;
    let restService: jasmine.SpyObj<IRestService<ICloneableCatalogVersion[]>>;
    let catalogVersionRestService: CatalogVersionRestService;

    beforeEach(() => {
        restService = jasmine.createSpyObj('restService', ['get']);
        restService.get.and.returnValue(Promise.resolve<any>(mockCatalogVersions));

        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(restService);

        catalogVersionRestService = new CatalogVersionRestService(restServiceFactory);
    });

    it('should return Catalog Version for given URI Context', (done) => {
        catalogVersionRestService.getCloneableTargets(mockUriContext).then((result) => {
            expect(result).toEqual(mockCatalogVersions);
            done();
        });
    });
});
