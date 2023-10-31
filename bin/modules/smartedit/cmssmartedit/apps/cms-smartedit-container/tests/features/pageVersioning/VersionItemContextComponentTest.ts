/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { VersionItemContextComponent } from 'cmssmarteditcontainer/components/versioning';
import { Observable, of } from 'rxjs';
import { PageVersionSelectionService, IPageVersion } from 'smarteditcommons';

describe('VersionItemContextComponent', () => {
    let pageVersionSelectionService: jasmine.SpyObj<PageVersionSelectionService>;

    let component: VersionItemContextComponent;
    beforeEach(() => {
        pageVersionSelectionService = jasmine.createSpyObj<PageVersionSelectionService>(
            'pageVersionSelectionService',
            ['hideToolbarContextIfNotNeeded', 'getSelectedPageVersion$', 'deselectPageVersion']
        );

        component = new VersionItemContextComponent(pageVersionSelectionService);
    });

    describe('init', () => {
        it('creates subscription for selected page version', () => {
            const mockPageVersion = of({}) as Observable<IPageVersion>;
            pageVersionSelectionService.getSelectedPageVersion$.and.returnValue(mockPageVersion);

            component.ngOnInit();

            expect(component.pageVersion$).toBe(mockPageVersion);
        });

        it('hides toolbar if needed', () => {
            component.ngOnInit();

            expect(pageVersionSelectionService.hideToolbarContextIfNotNeeded).toHaveBeenCalled();
        });
    });

    it('WHEN deselect button has been clicked THEN it delegates to the service', () => {
        component.deselectPageVersion();

        expect(pageVersionSelectionService.deselectPageVersion).toHaveBeenCalled();
    });
});
