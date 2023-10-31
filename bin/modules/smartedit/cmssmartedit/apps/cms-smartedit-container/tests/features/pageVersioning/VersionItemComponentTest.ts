/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { VersionItemComponent } from 'cmssmarteditcontainer/components/versioning';
import {
    IPerspective,
    IPerspectiveService,
    CMSModesService,
    IPageVersion,
    PageVersionSelectionService
} from 'smarteditcommons';

describe('VersionItemComponent', () => {
    let pageVersionSelectionService: jasmine.SpyObj<PageVersionSelectionService>;
    let perspectiveService: jasmine.SpyObj<IPerspectiveService>;
    let cMSModesService: jasmine.SpyObj<CMSModesService>;

    let component: VersionItemComponent;
    beforeEach(() => {
        pageVersionSelectionService = jasmine.createSpyObj<PageVersionSelectionService>(
            'pageVersionSelectionService',
            ['selectPageVersion', 'getSelectedPageVersion']
        );
        perspectiveService = jasmine.createSpyObj<IPerspectiveService>('perspectiveService', [
            'getActivePerspective',
            'switchTo'
        ]);
        cMSModesService = jasmine.createSpyObj<CMSModesService>('cMSModesService', [
            'isVersioningPerspectiveActive'
        ]);

        component = new VersionItemComponent(
            pageVersionSelectionService,
            perspectiveService,
            cMSModesService
        );
    });

    describe('selectVersion', () => {
        it('GIVEN Versioning Perspective is active WHEN called THEN it selects page version properly', async () => {
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(true));

            await component.selectVersion();

            expect(perspectiveService.switchTo).not.toHaveBeenCalled();
            expect(pageVersionSelectionService.selectPageVersion).toHaveBeenCalled();
        });

        it('GIVEN Versioning Perspective is not active WHEN called THEN it activates Versioning Mode AND selects page version properly', async () => {
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(false));

            await component.selectVersion();

            expect(perspectiveService.switchTo).toHaveBeenCalled();
            expect(pageVersionSelectionService.selectPageVersion).toHaveBeenCalled();
        });
    });

    describe('isSelectedVersion', () => {
        const mockPageVersion = {
            uid: 'ver-1'
        } as IPageVersion;
        it('returns true if the selected version is the same as page version', () => {
            component.pageVersion = mockPageVersion;
            pageVersionSelectionService.getSelectedPageVersion.and.returnValue(mockPageVersion);

            expect(component.isSelectedVersion()).toBe(true);
        });

        it('returns false if the selected version is not the same as version item', () => {
            component.pageVersion = mockPageVersion;
            pageVersionSelectionService.getSelectedPageVersion.and.returnValue({
                uid: 'ver-2'
            } as any);

            expect(component.isSelectedVersion()).toBe(false);
        });
    });

    describe('isVersionMenuEnabled', () => {
        const mockPerspective = {
            key: 'se.cms.perspective.versioning'
        } as IPerspective;
        it('returns true if Versioning Perspective is active', () => {
            perspectiveService.getActivePerspective.and.returnValue(mockPerspective);

            expect(component.isVersionMenuEnabled()).toBe(true);
        });

        it('returns false if Versioning Perspective is not active', () => {
            perspectiveService.getActivePerspective.and.returnValue({
                key: 'se.cms.perspective.basic'
            } as any);

            expect(component.isVersionMenuEnabled()).toBe(false);
        });
    });
});
