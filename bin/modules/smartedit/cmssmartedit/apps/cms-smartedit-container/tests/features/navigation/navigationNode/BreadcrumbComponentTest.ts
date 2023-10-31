/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { BreadcrumbComponent } from 'cmssmarteditcontainer/components/navigation';
import { NavigationEditorNodeService } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorNodeService';
import {
    IUriContext,
    functionsUtils,
    TreeNodeWithLevel,
    CmsitemsRestService
} from 'smarteditcommons';

describe('BreadcrumbComponent', () => {
    let component: BreadcrumbComponent;

    let navigationEditorNodeService: jasmine.SpyObj<NavigationEditorNodeService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const mockUid = 'uid';
    const mockUuid = 'uuid';
    const mockUriContext: IUriContext = {
        context: 'context'
    };
    const mockTree = [
        { level: 1, formattedLevel: '1' },
        { level: 2, formattedLevel: '2' }
    ] as TreeNodeWithLevel[];

    beforeEach(() => {
        navigationEditorNodeService = jasmine.createSpyObj<NavigationEditorNodeService>(
            'navigationEditorNodeService',
            ['getNavigationNodeAncestry']
        );
        navigationEditorNodeService.getNavigationNodeAncestry.and.returnValue(
            Promise.resolve(mockTree)
        );

        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'getById'
        ]);
        cmsitemsRestService.getById.and.returnValue(
            Promise.resolve<any>({
                uid: mockUid
            })
        );

        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new BreadcrumbComponent(navigationEditorNodeService, cmsitemsRestService, cdr);

        component.uriContext = mockUriContext;
    });

    it('GIVEN neither nodeUid nor nodeUuid are provided WHEN component is initialized THEN it should throw an error', async () => {
        component.nodeUid = null;
        component.nodeUuid = null;

        try {
            await component.ngOnInit();

            functionsUtils.assertFail();
        } catch (error) {
            expect(error.message).toEqual(
                'BreadcrumbComponent requires either nodeUid or nodeUuid'
            );
        }
    });

    it('GIVEN only nodeUid is not provided WHEN component is initialized THEN it should get uid from cmsitemsRestService', async () => {
        component.nodeUuid = mockUuid;

        await component.ngOnInit();

        expect(cmsitemsRestService.getById).toHaveBeenCalledWith(mockUuid);
        expect(navigationEditorNodeService.getNavigationNodeAncestry).toHaveBeenCalledWith(
            mockUid,
            mockUriContext
        );
        expect(component.breadcrumb).toEqual(mockTree);
    });

    it('GIVEN nodeUid is provided WHEN component is initialized THEN it should use it instead of calling rest api', async () => {
        component.nodeUuid = mockUuid;
        component.nodeUid = mockUid;

        await component.ngOnInit();

        expect(cmsitemsRestService.getById).not.toHaveBeenCalled();
        expect(navigationEditorNodeService.getNavigationNodeAncestry).toHaveBeenCalledWith(
            mockUid,
            mockUriContext
        );
        expect(component.breadcrumb).toEqual(mockTree);
    });
});
