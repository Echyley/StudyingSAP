/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';

import {
    CrossFrameEventService,
    EVENTS,
    EVENT_PERSPECTIVE_CHANGED,
    HIDE_TOOLBAR_ITEM_CONTEXT,
    IAlertService,
    IExperience,
    IExperienceService,
    IPageInfoService,
    SHOW_TOOLBAR_ITEM_CONTEXT
} from 'smarteditcommons';
import { IPageVersion } from '../interfaces';
import { CMSModesService } from '../modes';
import { PageVersioningService } from './PageVersioningService';
import { PageVersionSelectionService } from './PageVersionSelectionService';

const PAGE_VERSIONS_TOOLBAR_ITEM_KEY = 'se.cms.pageVersionsMenu';
const TRANSLATION = 'SOME TRANSLATION';

let perspectiveChangedCallback: (eventId: string) => Promise<void>;
let perspectiveRefreshedCallback: () => Promise<void>;
let pageChangeCallback: (eventId: string, experience: IExperience) => Promise<void>;

let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
let alertService: jasmine.SpyObj<IAlertService>;
let experienceService: jasmine.SpyObj<IExperienceService>;
let cMSModesService: jasmine.SpyObj<CMSModesService>;
let pageInfoService: jasmine.SpyObj<IPageInfoService>;
let pageVersioningService: jasmine.SpyObj<PageVersioningService>;
let translateService: jasmine.SpyObj<TranslateService>;
let pageVersion1: IPageVersion;
let mockUnSubEventPerspectiveChanged: jasmine.Spy;
let mockUnSubEventPerspectiveRefreshed: jasmine.Spy;
let mockUnSubEventPageChange: jasmine.Spy;

let service: PageVersionSelectionService;

describe('PageVersionSelectionService', () => {
    beforeEach(() => {
        createCommonSpyObject();
    });

    it('GIVEN no page version is selected WHEN hideToolbarContextIfNotNeeded is called THEN the toolbar context is hidden', () => {
        // WHEN
        service.hideToolbarContextIfNotNeeded();

        // THEN
        expect(crossFrameEventService.publish).toHaveBeenCalledWith(
            HIDE_TOOLBAR_ITEM_CONTEXT,
            PAGE_VERSIONS_TOOLBAR_ITEM_KEY
        );
    });

    it('GIVEN no page version is selected WHEN getSelectedPageVersion is called THEN it returns a null page', () => {
        // WHEN
        const result = service.getSelectedPageVersion();

        // THEN
        expect(result).toBeNull();
    });

    it('GIVEN a page version is selected THEN the same version can not be selected twice', () => {
        // GIVEN
        service.selectPageVersion(pageVersion1);

        // WHEN
        service.selectPageVersion(pageVersion1);

        // THEN
        expect(experienceService.updateExperience).toHaveBeenCalledTimes(1);
    });

    it('WHEN a page version is selected THEN it becomes the selected page AND the toolbar context is informed', () => {
        // GIVEN
        expect(crossFrameEventService.publish).not.toHaveBeenCalled();

        // WHEN
        service.selectPageVersion(pageVersion1);

        // THEN
        expect(experienceService.updateExperience).toHaveBeenCalledWith({
            versionId: pageVersion1.uid
        });
        expect(crossFrameEventService.publish).toHaveBeenCalledWith(
            SHOW_TOOLBAR_ITEM_CONTEXT,
            PAGE_VERSIONS_TOOLBAR_ITEM_KEY
        );
        expect(service.getSelectedPageVersion()).toBe(pageVersion1);
    });

    it('WHEN a page version is selected THEN the toolbar should get event to close all items', () => {
        // WHEN
        service.selectPageVersion(pageVersion1);

        // THEN
        expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PAGE_SELECTED);
    });

    it('GIVEN a page version is selected WHEN the version is removed THEN the toolbar context is informed AND an alert is shown', () => {
        // GIVEN
        service.selectPageVersion(pageVersion1);
        expect(alertService.showInfo).not.toHaveBeenCalled();

        // WHEN
        service.deselectPageVersion();

        // THEN
        expect(crossFrameEventService.publish).toHaveBeenCalledWith(
            HIDE_TOOLBAR_ITEM_CONTEXT,
            PAGE_VERSIONS_TOOLBAR_ITEM_KEY
        );
        expect(experienceService.updateExperience).toHaveBeenCalledWith({
            versionId: null
        });
        expect(alertService.showInfo).toHaveBeenCalledWith(TRANSLATION);
    });

    it('GIVEN a page version is selected WHEN the version is removed and false is passed as a parameter THEN the toolbar context is informed AND no alert is shown', () => {
        // GIVEN
        service.selectPageVersion(pageVersion1);
        expect(alertService.showInfo).not.toHaveBeenCalled();

        // WHEN
        service.deselectPageVersion(false);

        // THEN
        expect(crossFrameEventService.publish).toHaveBeenCalledWith(
            HIDE_TOOLBAR_ITEM_CONTEXT,
            PAGE_VERSIONS_TOOLBAR_ITEM_KEY
        );
        expect(experienceService.updateExperience).toHaveBeenCalledWith({
            versionId: null
        });
        expect(alertService.showInfo).not.toHaveBeenCalledWith(TRANSLATION);
    });

    describe('callbacks', () => {
        beforeEach(() => {
            expect(crossFrameEventService.subscribe.calls.count()).toBe(3);
            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                EVENT_PERSPECTIVE_CHANGED,
                jasmine.any(Function)
            );
            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                EVENT_PERSPECTIVE_CHANGED,
                jasmine.any(Function)
            );
            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                EVENTS.PAGE_CHANGE,
                jasmine.any(Function)
            );

            perspectiveChangedCallback = crossFrameEventService.subscribe.calls.argsFor(0)[1];
            perspectiveRefreshedCallback = crossFrameEventService.subscribe.calls.argsFor(
                1
            )[1] as any;
            pageChangeCallback = crossFrameEventService.subscribe.calls.argsFor(2)[1];
        });

        it(`GIVEN a versioned page is already selected and the same page is previewed in the versioning perspective 
			WHEN perspective changed event is called 
			THEN the version remains selected`, async () => {
            // GIVEN
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve('someItem'));
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(true));
            service.selectPageVersion(pageVersion1);

            // WHEN
            await perspectiveChangedCallback(EVENT_PERSPECTIVE_CHANGED);

            // THEN
            expect(service.getSelectedPageVersion()).toBe(pageVersion1);
        });

        it(`GIVEN a versioned page is already selected and a different page is previewed in the versioning perspective 
			WHEN perspective changed event is called 
			THEN no version is selected`, async () => {
            // GIVEN
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve('differentItem'));
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(true));
            service.selectPageVersion(pageVersion1);

            // WHEN
            await perspectiveChangedCallback(EVENT_PERSPECTIVE_CHANGED);

            // THEN
            expect(service.getSelectedPageVersion()).toBe(null);
        });

        it(`GIVEN a versioned page is already selected and a same page is previewed in a non-versioning perspective 
			WHEN perspective changed event is called 
			THEN no version is selected`, async () => {
            // GIVEN
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve('differentItem'));
            cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(false));
            service.selectPageVersion(pageVersion1);

            // WHEN
            await perspectiveChangedCallback(EVENT_PERSPECTIVE_CHANGED);

            // THEN
            expect(service.getSelectedPageVersion()).toBe(null);
        });

        it(`GIVEN a versioned page is already selected
            WHEN perspective refreshed event is called AND that experience is not versioned
            THEN the toolbar context is informed AND no version is selected`, async () => {
            service.selectPageVersion(pageVersion1);
            experienceService.getCurrentExperience.and.returnValue(Promise.resolve<any>({}));

            await perspectiveRefreshedCallback();

            expect(service.getSelectedPageVersion()).toBe(null);
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                HIDE_TOOLBAR_ITEM_CONTEXT,
                jasmine.any(String)
            );
        });

        it(`GIVEN a versioned page is already selected
        WHEN perspective refreshed event is called AND that experience is versioned
        THEN the toolbar context is informed
        `, async () => {
            service.selectPageVersion(pageVersion1);
            experienceService.getCurrentExperience.and.returnValue(
                Promise.resolve<any>({ versionId: '001' })
            );

            await perspectiveRefreshedCallback();

            expect(service.getSelectedPageVersion()).toBe(pageVersion1);
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                SHOW_TOOLBAR_ITEM_CONTEXT,
                jasmine.any(String)
            );
        });

        it(`
            GIVEN a versioned page is already selected AND user reloads the browser
            WHEN page change event is called
            THEN the version is selected
            AND the toolbar context is informed AND Page Selected event is published
        `, async () => {
            // when user reloads the browser, selectedPageVersion is empty

            const mockExperience = { versionId: '001' } as unknown as IExperience;
            pageVersioningService.getPageVersionForId.and.returnValue(
                Promise.resolve(pageVersion1)
            );

            await pageChangeCallback('', mockExperience);

            expect(service.getSelectedPageVersion()).toBe(pageVersion1);
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                SHOW_TOOLBAR_ITEM_CONTEXT,
                jasmine.any(String)
            );

            expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PAGE_SELECTED);
        });
    });

    describe('destroy', () => {
        it('should unsubscribe from Perspective Changed event', () => {
            service.ngOnDestroy();

            expect(mockUnSubEventPerspectiveChanged).toHaveBeenCalled();
        });

        it('should unsubscribe from Perspective Refreshed event', () => {
            service.ngOnDestroy();

            expect(mockUnSubEventPerspectiveRefreshed).toHaveBeenCalled();
        });

        it('should unsubscribe from Page Change event', () => {
            service.ngOnDestroy();

            expect(mockUnSubEventPageChange).toHaveBeenCalled();
        });

        it('should unsubscribe from Select Page Version subject', () => {
            const mockPageVersion1 = { uid: 'uid-1' } as IPageVersion;
            const mockPageVersion2 = { uid: 'uid-2' } as IPageVersion;
            service.updatePageVersionDetails(mockPageVersion1);

            service.ngOnDestroy();

            service.updatePageVersionDetails(mockPageVersion2);

            const selectedPageVersion = service.getSelectedPageVersion();
            expect(selectedPageVersion).toBe(mockPageVersion1);
        });
    });
});
function createCommonSpyObject() {
    pageVersion1 = {
        uid: 'someVersion',
        itemUUID: 'someItem',
        label: 'label',
        description: 'description',
        creationtime: new Date()
    };

    crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
        'crossFrameEventService',
        ['subscribe', 'publish']
    );
    mockUnSubEventPerspectiveChanged = jasmine.createSpy();
    mockUnSubEventPerspectiveRefreshed = jasmine.createSpy();
    mockUnSubEventPageChange = jasmine.createSpy();
    crossFrameEventService.subscribe.and.returnValues(
        mockUnSubEventPerspectiveChanged,
        mockUnSubEventPerspectiveRefreshed,
        mockUnSubEventPageChange
    );

    alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showInfo']);

    experienceService = jasmine.createSpyObj('experienceService', [
        'updateExperience',
        'getCurrentExperience'
    ]);

    cMSModesService = jasmine.createSpyObj<CMSModesService>('cMSModesService', [
        'isVersioningPerspectiveActive'
    ]);

    pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUUID']);

    pageVersioningService = jasmine.createSpyObj<PageVersioningService>('pageVersioningService', [
        'getPageVersionForId'
    ]);

    translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
    translateService.instant.and.returnValue(TRANSLATION);

    service = new PageVersionSelectionService(
        crossFrameEventService,
        alertService,
        experienceService,
        cMSModesService,
        pageInfoService,
        pageVersioningService,
        translateService
    );
}
