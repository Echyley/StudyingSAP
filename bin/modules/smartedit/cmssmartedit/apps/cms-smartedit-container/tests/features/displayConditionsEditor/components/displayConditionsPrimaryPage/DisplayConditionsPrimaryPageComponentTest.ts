/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SimpleChange } from '@angular/core';
import { DisplayConditionsPrimaryPageComponent } from 'cmssmarteditcontainer/components/pages/displayConditions/displayConditionsPrimaryPage/DisplayConditionsPrimaryPageComponent';
import { DisplayConditionsFacade } from 'cmssmarteditcontainer/facades';
import { IPageService, ICMSPage } from 'smarteditcommons';

describe('DisplayConditionsPrimaryPageComponent', function () {
    let component: DisplayConditionsPrimaryPageComponent;
    let displayConditionsFacade: jasmine.SpyObj<DisplayConditionsFacade>;
    let pageService: jasmine.SpyObj<IPageService>;

    const mockPage = {
        uid: 'uid',
        name: 'name'
    } as ICMSPage;

    beforeEach(function () {
        displayConditionsFacade = jasmine.createSpyObj<DisplayConditionsFacade>(
            'displayConditionsFacade',
            ['getPrimaryPagesForPageType']
        );
        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getPageById']);
        pageService.getPageById.and.returnValue(Promise.resolve(mockPage));
        component = new DisplayConditionsPrimaryPageComponent(pageService, displayConditionsFacade);
    });

    describe('init', function () {
        it('should initialize the i18n keys and fetchStrategy', function () {
            expect(component.fetchStrategy).toEqual({
                fetchEntity: jasmine.any(Function),
                fetchPage: jasmine.any(Function)
            });
        });
    });

    describe('On change', () => {
        it('WHEN associatedPrimaryPageChange changed THEN it should associated page and emit primaryPageChange', () => {
            const change = new SimpleChange(null, mockPage, true);

            component.ngOnChanges({ associatedPrimaryPage: change });

            expect(component.associatedPrimaryPage).toEqual(mockPage);
            expect(component.associatedPrimaryPageUid).toEqual('uid');
        });

        it('WHEN associatedPrimaryPageChange changed AND its value is undefined THEN it should not trigger change', () => {
            const change = new SimpleChange(null, null, true);

            component.ngOnChanges({ associatedPrimaryPage: change });

            expect(component.associatedPrimaryPage).toEqual(undefined);
            expect(component.associatedPrimaryPageUid).toEqual(null);
        });
    });

    describe('associatedPrimaryPageModelOnChange', () => {
        it('WHEN called THEN it should get page and assign page as associated page and emit that page', async () => {
            spyOn(component.onPrimaryPageSelect, 'emit');

            await component.associatedPrimaryPageUidOnChange(mockPage.uid);

            expect(component.onPrimaryPageSelect.emit).toHaveBeenCalledWith(mockPage);
            expect(component.associatedPrimaryPage).toEqual(mockPage);
            expect(component.associatedPrimaryPageUid).toEqual('uid');
        });
    });

    describe('fetchStrategy', () => {
        it('WHEN fetchEntity is called THEN it should resolve to id / label object', async () => {
            component.associatedPrimaryPage = { uid: 'uid', name: 'name' } as ICMSPage;
            const fetchEntity = component.fetchStrategy.fetchEntity;

            const actual = await fetchEntity(null);

            expect(actual).toEqual({
                id: 'uid',
                label: 'name'
            });
        });

        it('WHEN fetchPage is called THEN call displayConditionFacade getPrimaryPagesForPageType method', () => {
            component.pageType = 'ContentPage';
            const fetchPage = component.fetchStrategy.fetchPage;

            fetchPage('', 10, 1);

            expect(displayConditionsFacade.getPrimaryPagesForPageType).toHaveBeenCalledWith(
                'ContentPage',
                null,
                {
                    search: '',
                    pageSize: 10,
                    currentPage: 1
                }
            );
        });
    });
});
