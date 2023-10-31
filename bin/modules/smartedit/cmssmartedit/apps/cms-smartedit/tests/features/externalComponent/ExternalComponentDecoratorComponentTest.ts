/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ElementRef } from '@angular/core';
import { ExternalComponentDecoratorComponent } from 'cmssmartedit/components';
import { of } from 'rxjs';
import {
    ICatalogService,
    L10nPipe,
    LogService,
    CMSModesService,
    IComponentHandlerService
} from 'smarteditcommons';

describe('ExternalComponentDecoratorComponent', () => {
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let cMSModesService: jasmine.SpyObj<CMSModesService>;
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;
    let logService: jasmine.SpyObj<LogService>;
    const element: ElementRef = {
        nativeElement: null
    };
    const catalogVersion = {
        catalogName: {
            en: 'Electronics Content Catalog'
        },
        version: 'Online'
    };

    let component: ExternalComponentDecoratorComponent;
    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionByUuid'
        ]);

        cMSModesService = jasmine.createSpyObj<CMSModesService>('cMSModesService', [
            'isVersioningPerspectiveActive'
        ]);

        componentHandlerService = jasmine.createSpyObj<IComponentHandlerService>(
            'componentHandlerService',
            ['getParentSlotForComponent', 'isExternalComponent']
        );

        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);

        logService = jasmine.createSpyObj<LogService>('LogService', ['error']);

        component = new ExternalComponentDecoratorComponent(
            catalogService,
            cMSModesService,
            componentHandlerService,
            l10nPipe,
            logService,
            element
        );
    });

    describe('initialization', () => {
        beforeEach(() => {
            componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(true));

            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(true));

            l10nPipe.transform.and.callFake((catalogName: any) => of(catalogName.en));
        });

        it('GIVEN catalog version exists for the given catalog version uuid THEN it sets catalogVersionText properly', async () => {
            catalogService.getCatalogVersionByUuid.and.returnValue(
                Promise.resolve<any>(catalogVersion)
            );

            await component.ngOnInit();

            expect(component.catalogVersionText).toBe('Electronics Content Catalog (Online)');
        });

        it('GIVEN catalog version does not exists for the given catalog version uuid THEN it logs an error', async () => {
            catalogService.getCatalogVersionByUuid.and.returnValue(Promise.reject());

            await component.ngOnInit();

            expect(component.isReady).toBe(false);
            expect(logService.error).toHaveBeenCalled();
        });
    });
});
