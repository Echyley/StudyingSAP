/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ExternalComponentButtonComponent } from 'cmscommons';
import { of } from 'rxjs';
import { ComponentAttributes, ICatalogService, L10nPipe } from 'smarteditcommons';

describe('ExternalComponentButtonComponent', () => {
    let contextualMenuItemData: ComponentAttributes;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let component: ExternalComponentButtonComponent;
    beforeEach(() => {
        contextualMenuItemData = {
            smarteditCatalogVersionUuid: '123'
        } as ComponentAttributes;

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionByUuid'
        ]);
        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);

        component = new ExternalComponentButtonComponent(
            {
                componentAttributes: contextualMenuItemData,
                setRemainOpen: undefined
            },
            catalogService,
            l10nPipe,
            cdr
        );
    });

    it('WHEN initialized THEN it sets catalog version properly', async () => {
        const catalogVersion = {
            catalogName: {
                en: 'Electronics Content Catalog'
            },
            version: 'Online'
        };
        catalogService.getCatalogVersionByUuid.and.returnValue(
            Promise.resolve<any>(catalogVersion)
        );
        l10nPipe.transform.and.callFake((catalogName: any) => of(catalogName.en));

        await component.ngOnInit();

        expect(component.catalogVersion).toBe('Electronics Content Catalog (Online)');
        expect(component.isReady).toBe(true);
    });
});
