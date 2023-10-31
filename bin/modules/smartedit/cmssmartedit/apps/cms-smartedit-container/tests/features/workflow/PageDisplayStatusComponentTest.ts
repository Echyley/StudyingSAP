/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { DEFAULT_SYNCHRONIZATION_POLLING as SYNCHRONIZATION_POLLING } from 'cmscommons';
import { PageDisplayStatusComponent } from 'cmssmarteditcontainer/components/workflow/components/pageDisplayStatus/PageDisplayStatusComponent';
import { noop } from 'lodash';
import {
    CrossFrameEventService,
    EVENTS,
    SystemEventService,
    IPageService,
    ICMSPage
} from 'smarteditcommons';

describe('PageDisplayStatusComponent', () => {
    let pageService: jasmine.SpyObj<IPageService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    const mockPageUuid = 'eyJpd';
    const mockCurrentPageInfo = {
        displayStatus: 'READY_TO_SYNC',
        uuid: mockPageUuid
    } as ICMSPage;

    let component: PageDisplayStatusComponent;
    beforeEach(() => {
        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getCurrentPageInfo']);

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publish'
        ]);

        component = new PageDisplayStatusComponent(
            pageService,
            crossFrameEventService,
            systemEventService,
            cdr
        );
    });

    beforeEach(() => {
        pageService.getCurrentPageInfo.and.returnValue(Promise.resolve(mockCurrentPageInfo));
    });

    describe('initialize', () => {
        it('GIVEN cmsPage is not provided WHEN initialized THEN the page is set to the current page', async () => {
            await component.ngOnInit();

            expect(pageService.getCurrentPageInfo).toHaveBeenCalled();
            expect(component.page).toBe(mockCurrentPageInfo);
        });

        it('GIVEN cmsPage is provided WHEN initialized THEN the page is set to cmsPage', async () => {
            const mockCmsPage = {
                uuid: mockPageUuid
            } as ICMSPage;
            component.cmsPage = mockCmsPage;

            await component.ngOnInit();

            expect(component.page).toBe(mockCmsPage);
        });

        it('WHEN initialized THEN it should subscribe to Synchronization Polling event', async () => {
            await component.ngOnInit();

            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                SYNCHRONIZATION_POLLING.FETCH_STATUS,
                jasmine.any(Function)
            );
        });

        it('WHEN initialized THEN it should subscribe to Page Updated event', async () => {
            await component.ngOnInit();

            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                EVENTS.PAGE_UPDATED,
                jasmine.any(Function)
            );
        });
    });

    describe('GIVEN Synchronization Polling event has been published ', () => {
        it('AND received Page is the same THEN it should update last synched date', async () => {
            await component.ngOnInit();

            const updateLastSynchedDate = crossFrameEventService.subscribe.calls.argsFor(0)[1];
            updateLastSynchedDate('eventId', {
                itemId: mockPageUuid,
                lastSyncStatus: 0
            });

            expect(component.lastSynchedDate).toEqual(0);
        });

        it('AND received Page is different THEN it should not update last synched date', async () => {
            await component.ngOnInit();
            const updateLastSynchedDate = crossFrameEventService.subscribe.calls.argsFor(0)[1];

            updateLastSynchedDate('eventId', {
                itemId: 'differentPageUuid',
                lastSyncStatus: 0
            });

            expect(component.lastSynchedDate).not.toBeDefined();
        });
    });

    describe('GIVEN Page Updated event has been published ', () => {
        it('AND received payload is null THEN it should not update page info', async () => {
            await component.ngOnInit();
            const updatePageInfo = crossFrameEventService.subscribe.calls.argsFor(1)[1];

            await updatePageInfo('eventId', {
                uuid: null
            });

            expect(pageService.getCurrentPageInfo).toHaveBeenCalledTimes(2);
        });

        it('AND received payload has different uuid THEN it should not update page info', async () => {
            await component.ngOnInit();

            const updatePageInfo = crossFrameEventService.subscribe.calls.argsFor(1)[1];
            await updatePageInfo('eventId', {
                uuid: 'differentPageUuid'
            });

            expect(pageService.getCurrentPageInfo).toHaveBeenCalledTimes(1);
        });

        it('AND received payload has the same uuid THEN it should update page info', async () => {
            await component.ngOnInit();

            const updatePageInfo = crossFrameEventService.subscribe.calls.argsFor(1)[1];
            updatePageInfo('eventId', {
                uuid: mockPageUuid
            });

            expect(pageService.getCurrentPageInfo).toHaveBeenCalledTimes(2);
        });

        it(`AND received payload has the same uuid AND has previous displayStatus AND the current displayStatus is DRAFT
        THEN it publishes Workflow Refresh Event`, async () => {
            await component.ngOnInit();

            const updatedPageInfo = {
                displayStatus: 'DRAFT',
                uuid: mockPageUuid
            } as ICMSPage;
            pageService.getCurrentPageInfo.and.returnValue(Promise.resolve(updatedPageInfo));
            const updatePageInfo = crossFrameEventService.subscribe.calls.argsFor(1)[1];
            await updatePageInfo('eventId', {
                uuid: mockPageUuid
            });

            expect(pageService.getCurrentPageInfo).toHaveBeenCalledTimes(2);
            expect(systemEventService.publish).toHaveBeenCalledWith('WORKFLOW_REFRESH_EVENT');
        });
    });

    describe('WHEN component is destroyed THEN ', () => {
        it('should unregister from Synchronization Polling event', () => {
            const unRegPageSyncEventSpy = jasmine.createSpy('unregisterErrorListener');
            crossFrameEventService.subscribe.and.returnValue(unRegPageSyncEventSpy);
            component.ngOnInit();

            component.ngOnDestroy();

            expect(unRegPageSyncEventSpy).toHaveBeenCalled();
        });

        it('should unregister from Page Updated event', () => {
            const unRegPageUpdatedEventSpy = jasmine.createSpy('unRegPageUpdatedEvent');
            crossFrameEventService.subscribe.and.returnValues(noop, unRegPageUpdatedEventSpy);
            component.ngOnInit();

            component.ngOnDestroy();

            expect(unRegPageUpdatedEventSpy).toHaveBeenCalled();
        });
    });

    describe('hasBeenSynchedBefore', () => {
        it('GIVEN the last synched date was updated WHEN called THEN it should return true', () => {
            component.lastSynchedDate = 1000;
            expect(component.hasBeenSynchedBefore()).toBe(true);

            component.lastSynchedDate = null;
            expect(component.hasBeenSynchedBefore()).toBe(true);
        });

        it('GIVEN the last synched date was not updated WHEN called THEN it should return false', () => {
            expect(component.hasBeenSynchedBefore()).toBe(false);
        });
    });
});
