/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { WORKFLOW_FINISHED_EVENT } from 'cmscommons';
import { PageApprovalSelectorComponent } from 'cmssmarteditcontainer/components/workflow/components/pageApprovalSelector/PageApprovalSelectorComponent';
import {
    CrossFrameEventService,
    EVENT_PERSPECTIVE_CHANGED,
    IIframeClickDetectionService,
    IWaitDialogService,
    LogService,
    SmarteditRoutingService,
    IPageService,
    CmsApprovalStatus,
    WorkflowService,
    IUserTrackingService
} from 'smarteditcommons';

describe('PageApprovalSelectorComponent', () => {
    const mockPageUuid = 'eyJpd';
    const SE_CMS_WORKFLOW_APPROVAL_INFO =
        'The status of current page can’t be changed to Ready to Synch. Please synchronize original-page first and try again.';
    let iframeClickDetectionService: jasmine.SpyObj<IIframeClickDetectionService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let waitDialogService: jasmine.SpyObj<IWaitDialogService>;
    let workflowService: jasmine.SpyObj<WorkflowService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let routingService: jasmine.SpyObj<SmarteditRoutingService>;
    let logService: jasmine.SpyObj<LogService>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let component: PageApprovalSelectorComponent;
    beforeEach(() => {
        iframeClickDetectionService = jasmine.createSpyObj('iframeClickDetectionService', [
            'registerCallback',
            'removeCallback'
        ]);

        pageService = jasmine.createSpyObj('pageService', [
            'getCurrentPageInfo',
            'forcePageApprovalStatus'
        ]);
        pageService.getCurrentPageInfo.and.returnValue(
            Promise.resolve<any>({ uuid: mockPageUuid })
        );

        waitDialogService = jasmine.createSpyObj('waitDialogService', [
            'showWaitModal',
            'hideWaitModal'
        ]);

        workflowService = jasmine.createSpyObj('workflowService', [
            'getActiveWorkflowForPageUuid',
            'pageHasUnavailableDependencies',
            'fetchPageTranslatedApprovalInfo'
        ]);
        workflowService.getActiveWorkflowForPageUuid.and.returnValue(Promise.resolve(null));

        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['subscribe']);

        routingService = jasmine.createSpyObj('routingService', ['reload']);

        logService = jasmine.createSpyObj('logService', ['warn']);

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new PageApprovalSelectorComponent(
            iframeClickDetectionService,
            pageService,
            waitDialogService,
            workflowService,
            crossFrameEventService,
            routingService,
            logService,
            cdr,
            userTrackingService
        );
    });

    describe('initialize ', () => {
        describe('', () => {
            beforeEach(async () => {
                await component.ngOnInit();
            });

            it('THEN it should subscribe to Workflow Finished event', () => {
                expect(
                    crossFrameEventService.subscribe.calls
                        .argsFor(0)[0]
                        .includes(WORKFLOW_FINISHED_EVENT)
                ).toBe(true);
            });

            it('THEN it should subscribe to Perspective Changed event', () => {
                expect(
                    crossFrameEventService.subscribe.calls
                        .argsFor(1)[0]
                        .includes(EVENT_PERSPECTIVE_CHANGED)
                ).toBe(true);
            });

            it('THEN it should register callback for Iframe click', () => {
                expect(iframeClickDetectionService.registerCallback).toHaveBeenCalled();
            });

            it('THEN it should set dropdown options', (done) => {
                component.pageApprovalOptions$.subscribe((options) => {
                    expect(options).toBeDefined();
                    done();
                });
            });
        });

        it('GIVEN workflow is not in progress WHEN initialized THEN it should show component', async () => {
            await component.ngOnInit();

            expect(component.showDropdown).toBe(true);
        });

        it('GIVEN workflow is in progress WHEN initialized THEN it should not show component', async () => {
            workflowService.getActiveWorkflowForPageUuid.and.returnValue({} as any);

            await component.ngOnInit();

            expect(component.showDropdown).toBe(false);
        });
    });

    describe('destroy', () => {
        let unRegPerspectiveChangedHandlerSpy: jasmine.Spy;
        let unRegWfFinishedHandlerSpy: jasmine.Spy;
        beforeEach(() => {
            unRegWfFinishedHandlerSpy = jasmine.createSpy('unRegWfFinishedHandler');
            unRegPerspectiveChangedHandlerSpy = jasmine.createSpy('unRegPerspectiveChangedHandler');

            crossFrameEventService.subscribe.and.returnValues(
                unRegWfFinishedHandlerSpy,
                unRegPerspectiveChangedHandlerSpy
            );
        });

        it('THEN it should unregister from Workflow Finished event', async () => {
            await component.ngOnInit();

            component.ngOnDestroy();

            expect(unRegWfFinishedHandlerSpy).toHaveBeenCalled();
        });

        it('THEN it should unregister from Perspective Changed event', async () => {
            await component.ngOnInit();

            component.ngOnDestroy();

            expect(unRegPerspectiveChangedHandlerSpy).toHaveBeenCalled();
        });

        it('THEN it should unregister from callback for Iframe click', async () => {
            await component.ngOnInit();

            component.ngOnDestroy();

            expect(iframeClickDetectionService.removeCallback).toHaveBeenCalled();
        });
    });

    describe('Perspective Changed event', () => {
        let perspectiveChangedCallback;
        beforeEach(async () => {
            await component.ngOnInit();
            perspectiveChangedCallback = crossFrameEventService.subscribe.calls.argsFor(1)[1];
        });
        it(
            'WHEN Perspective Changed event has been published AND there is no active workflow ' +
                'THEN it should display the dropdown',
            async () => {
                workflowService.getActiveWorkflowForPageUuid.and.returnValue(Promise.resolve(null));
                await perspectiveChangedCallback();

                expect(component.showDropdown).toBe(true);
            }
        );

        it(
            'WHEN Perspective Changed event has been published AND there is an active workflow ' +
                'THEN it should not display the dropdown',
            async () => {
                workflowService.getActiveWorkflowForPageUuid.and.returnValue(
                    Promise.resolve<any>({})
                );
                await perspectiveChangedCallback();

                expect(component.showDropdown).toBe(false);
            }
        );
    });

    it('GIVEN dropdown is open WHEN user clicks on iframe THEN it should close the dropdown', async () => {
        await component.ngOnInit();
        const registeredCallback = iframeClickDetectionService.registerCallback.calls.argsFor(0)[1];
        component.isOpen = true;

        registeredCallback();

        expect(component.isOpen).toBe(false);
    });

    it('WHEN dropdown is opened THEN it should filter out selected option from dropdown options', (done) => {
        component.ngOnInit().then(() => {
            // override the mock
            pageService.getCurrentPageInfo.and.returnValue(
                Promise.resolve<any>({
                    approvalStatus: CmsApprovalStatus.CHECK,
                    uuid: mockPageUuid
                })
            );
            component.isOpen = false;
            // WHEN
            component.onClickDropdown().then(() => {
                // THEN
                component.pageApprovalOptions$.subscribe((options) => {
                    expect(options.length).toEqual(1);
                    expect(options[CmsApprovalStatus.CHECK]).not.toBeDefined();
                    done();
                });
            });
        });
    });

    it('WHEN the orignal page of current page is never synced THEN it should show the approval info', (done) => {
        component.ngOnInit().then(() => {
            // override the mock
            workflowService.pageHasUnavailableDependencies.and.returnValue(Promise.resolve(true));
            component.isOpen = false;

            workflowService.fetchPageTranslatedApprovalInfo.and.returnValue(
                Promise.resolve(SE_CMS_WORKFLOW_APPROVAL_INFO)
            );
            // WHEN
            component.onClickDropdown().then(() => {
                // THEN
                expect(component.approvalInfo).toEqual(SE_CMS_WORKFLOW_APPROVAL_INFO);
                expect(component.showApprovalInfo).toEqual(true);
                done();
            });
        });
    });
});
