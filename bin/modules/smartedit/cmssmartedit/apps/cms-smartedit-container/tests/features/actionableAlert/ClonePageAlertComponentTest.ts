/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { AlertRef } from '@fundamental-ngx/core';
import { ClonePageAlertComponent } from 'cmssmarteditcontainer/services/actionableAlert/ClonePageAlertComponent';
import { of } from 'rxjs';
import { ICatalogVersion, IExperienceService, L10nPipe, ICMSPage } from 'smarteditcommons';

describe('ClonePageAlertComponent', () => {
    const mockPageInfo = {
        catalogVersion: 'Online',
        uid: 'uid'
    } as ICMSPage;

    const mockCatalogVersion = {
        uuid: 'uuidCatalog',
        siteId: 'siteId',
        catalogId: 'catalogId',
        version: 'Online',
        catalogName: {
            en: 'catalogName'
        }
    } as unknown as ICatalogVersion;

    class MockAlertRef extends AlertRef {
        data = {
            clonedPageInfo: mockPageInfo,
            catalogVersion: mockCatalogVersion
        };
    }

    let component: ClonePageAlertComponent;
    let alertRef: MockAlertRef;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;
    let l10n: jasmine.SpyObj<L10nPipe>;

    beforeEach(() => {
        alertRef = new MockAlertRef();
        experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', [
            'loadExperience'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);
        l10n = jasmine.createSpyObj<L10nPipe>('l10n', ['transform']);

        l10n.transform.and.callFake((map: any) => of(map?.en || ''));

        component = new ClonePageAlertComponent(alertRef, experienceService, cdr, l10n);
    });

    describe('init', () => {
        it('should set description and hyperlink translations', async () => {
            await component.ngOnInit();

            expect(component.description).toEqual('se.cms.clonepage.alert.info.description');
            expect(component.descriptionDetails).toEqual({
                catalogName: 'catalogName',
                catalogVersion: 'Online'
            });
            expect(component.hyperlinkLabel).toEqual('se.cms.clonepage.alert.info.hyperlink');
        });
    });

    describe('onClick', () => {
        it('WHEN onClick is triggered AND page info does not have uid THEN it should throw error', async () => {
            (component as any).alertRef.data = { clonedPageInfo: {}, catalogVersion: {} };
            await component.ngOnInit();

            expect(() => component.onClick()).toThrowError(
                "ClonePageAlertService.displayClonePageAlert - missing required parameter 'uid'"
            );
        });

        it('WHEN onClick is triggered AND page info uid is empty string THEN it should throw error', async () => {
            (component as any).alertRef.data = { clonedPageInfo: { uid: '' }, catalogVersion: {} };
            await component.ngOnInit();

            expect(() => component.onClick()).toThrowError(
                "ClonePageAlertService.displayClonePageAlert - missing required parameter 'uid'"
            );
        });

        it('should load experience for given params', async () => {
            await component.ngOnInit();
            component.onClick();

            expect(experienceService.loadExperience).toHaveBeenCalledWith({
                siteId: 'siteId',
                catalogId: 'catalogId',
                catalogVersion: 'Online',
                pageId: 'uid'
            });
        });
    });
});
