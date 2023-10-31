/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ActivatedRoute } from '@angular/router';
import { PagesLinkComponent } from 'cmssmarteditcontainer/components/pages/pagesLink/PagesLinkComponent';
import { SmarteditRoutingService } from 'smarteditcommons';

describe('PagesLinkComponent', () => {
    let component: PagesLinkComponent;
    let route: ActivatedRoute;
    let seRouting: jasmine.SpyObj<SmarteditRoutingService>;

    const siteIdMock = 'siteId';
    const catalogIdMock = 'catalogId';
    const catalogVersionMock = 'catalogVersion';

    beforeEach(() => {
        route = {
            snapshot: {
                params: {
                    siteId: siteIdMock,
                    catalogId: catalogIdMock,
                    catalogVersion: catalogVersionMock
                }
            }
        } as unknown as ActivatedRoute;
        seRouting = jasmine.createSpyObj<SmarteditRoutingService>('seRouting', ['go']);
        component = new PagesLinkComponent(route, seRouting);
    });

    it('WHEN component is initialized THEN it should set siteId, catalogId and catalogVersion', () => {
        component.ngOnInit();

        expect((component as any).siteId).toEqual(siteIdMock);
        expect((component as any).catalogId).toEqual(catalogIdMock);
        expect((component as any).catalogVersion).toEqual(catalogVersionMock);
    });

    it('GIVEN component is initialized WHEN backToPagelist is called THEN it should call seRoutingService with go method', () => {
        component.ngOnInit();

        component.backToPagelist();

        const expectedRoute = 'pages/:siteId/:catalogId/:catalogVersion'
            .replace(':siteId', siteIdMock)
            .replace(':catalogId', catalogIdMock)
            .replace(':catalogVersion', catalogVersionMock);

        expect(seRouting.go).toHaveBeenCalledWith(expectedRoute);
    });
});
