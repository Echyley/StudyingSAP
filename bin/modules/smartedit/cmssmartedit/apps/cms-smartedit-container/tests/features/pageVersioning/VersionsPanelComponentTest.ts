/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { VersionsPanelComponent } from 'cmssmarteditcontainer/components/versioning/';
import {
    IPerspectiveService,
    LogService,
    CMSModesService,
    IPageVersion,
    PageVersioningService
} from 'smarteditcommons';

describe('VersionsPanelComponent', () => {
    let pageVersioningService: jasmine.SpyObj<PageVersioningService>;
    let perspectiveSerivce: jasmine.SpyObj<IPerspectiveService>;
    let logService: jasmine.SpyObj<LogService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let component: VersionsPanelComponent;
    beforeEach(() => {
        pageVersioningService = jasmine.createSpyObj<PageVersioningService>(
            'pageVersioningService',
            ['findPageVersions']
        );

        perspectiveSerivce = jasmine.createSpyObj<IPerspectiveService>('perspectiveSerivce', [
            'switchTo',
            'getActivePerspective'
        ]);

        logService = jasmine.createSpyObj<LogService>('logService', ['error']);

        component = new VersionsPanelComponent(
            pageVersioningService,
            perspectiveSerivce,
            logService,
            cdr
        );
    });

    describe('shows manage link properly', () => {
        beforeEach(() => {});
        it('GIVEN current mode is non versioning mode WHEN called THEN it can show Manage Link', () => {
            perspectiveSerivce.getActivePerspective.and.returnValue({
                key: CMSModesService.ADVANCED_PERSPECTIVE_KEY
            } as any);

            component.ngOnInit();

            expect(component.showManageLink).toBe(true);
        });
        it('GIVEN current mode is versioning mode WHEN called THEN Manage Link is not shown', () => {
            perspectiveSerivce.getActivePerspective.and.returnValue({
                key: CMSModesService.VERSIONING_PERSPECTIVE_KEY
            } as any);

            component.ngOnInit();

            expect(component.showManageLink).toBe(false);
        });
    });

    describe('fetching page of versions', () => {
        it(`GIVEN component was initialized AND current mode is non versioning WHEN called THEN it
            returns versions
            AND sets totalPageVersions
            AND shows Manage Button
        `, async () => {
            perspectiveSerivce.getActivePerspective.and.returnValue({
                key: CMSModesService.ADVANCED_PERSPECTIVE_KEY
            } as any);
            component.ngOnInit();

            const mockResults = [{ uid: 'version1' }] as IPageVersion[];
            pageVersioningService.findPageVersions.and.returnValue(
                Promise.resolve({
                    pagination: {
                        totalCount: 1
                    },
                    results: mockResults
                }) as any
            );

            const page = await component.fetchPageOfVersions();

            expect(page.results).toBe(mockResults);
            expect(component.pageHasVersions()).toBe(true);
            expect(component.showManageButton).toBe(true);
        });

        it(`GIVEN component was initialized AND current mode is versioning WHEN called THEN it
            returns versions
            AND sets totalPageVersions
            AND hides Manage Button
        `, async () => {
            perspectiveSerivce.getActivePerspective.and.returnValue({
                key: CMSModesService.VERSIONING_PERSPECTIVE_KEY
            } as any);
            component.ngOnInit();

            const mockResults = [{ uid: 'version1' }] as IPageVersion[];
            pageVersioningService.findPageVersions.and.returnValue(
                Promise.resolve({
                    pagination: {
                        totalCount: 1
                    },
                    results: mockResults
                }) as any
            );

            const page = await component.fetchPageOfVersions();

            expect(page.results).toBe(mockResults);
            expect(component.pageHasVersions()).toBe(true);
            expect(component.showManageButton).toBe(false);
        });

        it('WHEN pages cannot  be fetched THEN it handles the error properly', async () => {
            pageVersioningService.findPageVersions.and.returnValue(Promise.reject());

            const page = await component.fetchPageOfVersions(null, null, null);

            expect(page).toBeNull();
            expect(logService.error).toHaveBeenCalled();
        });
    });

    it('WHEN version items has been loaded THEN it sets the value properly', () => {
        const mockVersionItems = [{ itemUUID: '1' }] as IPageVersion[];
        component.onVersionItemsLoaded(mockVersionItems);

        expect(component.versionItems).toEqual(mockVersionItems);
    });

    it('WHEN user enters data into the search field THEN it sets the value properly', () => {
        component.onSearchTermChanged('version1');

        expect(component.searchTerm).toBe('version1');
    });

    it('WHEN manage buttons is clicked THEN it changes to version mode AND notifies parent component', () => {
        const switchModeEmitSpy = spyOn(component.switchMode, 'emit');

        component.switchToVersioningMode();

        expect(perspectiveSerivce.switchTo).toHaveBeenCalled();
        expect(switchModeEmitSpy).toHaveBeenCalled();
    });
});
