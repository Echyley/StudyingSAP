/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';

import {
    CrossFrameEventService,
    IExperienceService,
    IPerspectiveService,
    BrowserService,
    IStorageService,
    PAGE_TREE_PANEL_WIDTH_COOKIE_NAME,
    CMSModesService,
    MessageGateway,
    SmarteditBootstrapGateway
} from 'smarteditcommons';
import { StorefrontPageComponent } from 'smarteditcontainer/components/pages/storefrontPage';
import { IframeManagerService } from 'smarteditcontainer/services';

describe('StorefrontPageComponent', () => {
    let browserService: jasmine.SpyObj<BrowserService>;
    let iframeManagerService: jasmine.SpyObj<IframeManagerService>;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let yjQuery: any;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let storageService: jasmine.SpyObj<IStorageService>;
    let perspectiveService: jasmine.SpyObj<IPerspectiveService>;
    let messageGateway: jasmine.SpyObj<MessageGateway>;
    let smarteditBootstrapGateway: jasmine.SpyObj<SmarteditBootstrapGateway>;

    let storefrontPageComponent: any;
    let documentElement: jasmine.SpyObj<any>;

    beforeEach(() => {
        yjQuery = jasmine.createSpy('yjQuery');
        browserService = jasmine.createSpyObj('browserService', ['isSafari']);
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['subscribe']);
        iframeManagerService = jasmine.createSpyObj('iframeManagerService', ['applyDefault']);
        experienceService = jasmine.createSpyObj('experienceService', ['initializeExperience']);
        documentElement = jasmine.createSpyObj('documentElement', ['addClass']);
        storageService = jasmine.createSpyObj('storageService', [
            'setValueInLocalStorage',
            'getValueFromLocalStorage'
        ]);
        perspectiveService = jasmine.createSpyObj('perspectiveService', ['getActivePerspective']);
        messageGateway = jasmine.createSpyObj('messageGateway', ['subscribe', 'publish']);
        smarteditBootstrapGateway = jasmine.createSpyObj('smarteditBootstrapGateway', [
            'getInstance'
        ]);
        smarteditBootstrapGateway.getInstance.and.returnValue(messageGateway);

        storefrontPageComponent = new StorefrontPageComponent(
            browserService,
            iframeManagerService,
            experienceService,
            yjQuery,
            crossFrameEventService,
            storageService,
            perspectiveService,
            smarteditBootstrapGateway
        );
    });

    describe('ngOnInit', () => {
        beforeEach(() => {
            spyOn(storefrontPageComponent, 'getPageTreePanelStatus').and.returnValue(
                Promise.resolve()
            );
            spyOn(storefrontPageComponent, 'getPageTreePanelWidth').and.returnValue(
                Promise.resolve()
            );
            yjQuery.withArgs(document.body).and.returnValue(documentElement);
        });

        it('WHEN browser is Safari and perspective is advanced edit THEN add the safari class and get status and width', async () => {
            // WHEN
            browserService.isSafari.and.returnValue(true);
            perspectiveService.getActivePerspective.and.returnValue({
                key: CMSModesService.ADVANCED_PERSPECTIVE_KEY,
                nameI18nKey: CMSModesService.ADVANCED_PERSPECTIVE_KEY,
                features: []
            });

            // THEN
            await storefrontPageComponent.ngOnInit();

            expect(iframeManagerService.applyDefault).toHaveBeenCalled();
            expect(experienceService.initializeExperience).toHaveBeenCalled();
            expect(documentElement.addClass).toHaveBeenCalledWith('is-storefront');
            expect(documentElement.addClass).toHaveBeenCalledWith('is-safari');
            expect(storefrontPageComponent.getPageTreePanelStatus).toHaveBeenCalled();
            expect(storefrontPageComponent.getPageTreePanelWidth).toHaveBeenCalled();
            expect(crossFrameEventService.subscribe).toHaveBeenCalledTimes(3);
        });

        it('WHEN browser is not Safari and perspective is not advanced edit THEN not add the safari class and not get status and width', async () => {
            // WHEN
            browserService.isSafari.and.returnValue(false);
            perspectiveService.getActivePerspective.and.returnValue({
                key: CMSModesService.VERSIONING_PERSPECTIVE_KEY,
                nameI18nKey: CMSModesService.VERSIONING_PERSPECTIVE_KEY,
                features: []
            });

            // THEN
            await storefrontPageComponent.ngOnInit();

            expect(iframeManagerService.applyDefault).toHaveBeenCalled();
            expect(experienceService.initializeExperience).toHaveBeenCalled();
            expect(documentElement.addClass).toHaveBeenCalledWith('is-storefront');
            expect(documentElement.addClass).not.toHaveBeenCalledWith('is-safari');
            expect(storefrontPageComponent.getPageTreePanelStatus).not.toHaveBeenCalled();
            expect(storefrontPageComponent.getPageTreePanelWidth).not.toHaveBeenCalled();
            expect(crossFrameEventService.subscribe).toHaveBeenCalledTimes(3);
        });
    });

    describe('handlePageTreePanelSwitch', () => {
        it('WHEN page tree panel is open THEN close the panel and save the panel status', async () => {
            // WHEN
            storefrontPageComponent.isPageTreePanelOpen = true;
            spyOn(storefrontPageComponent, 'getPageTreePanelWidth');

            // THEN
            await storefrontPageComponent.handlePageTreePanelSwitch();

            expect(storefrontPageComponent.isPageTreePanelOpen).toBeFalse();
            expect(storefrontPageComponent.getPageTreePanelWidth).not.toHaveBeenCalled();
            expect(storageService.setValueInLocalStorage).toHaveBeenCalledWith(
                storefrontPageComponent.PAGE_TREE_PANEL_OPEN_COOKIE_NAME,
                false,
                true
            );
        });

        it(
            'WHEN page tree panel is close and has permission to read ' +
                'THEN open the panel, get the width in local storage and save the panel status',
            async () => {
                // WHEN
                storefrontPageComponent.isPageTreePanelOpen = false;
                storageService.getValueFromLocalStorage
                    .withArgs(PAGE_TREE_PANEL_WIDTH_COOKIE_NAME, true)
                    .and.returnValue(Promise.resolve('600'));

                // THEN
                await storefrontPageComponent.handlePageTreePanelSwitch();

                expect(storefrontPageComponent.isPageTreePanelOpen).toBeTrue();
                expect(storefrontPageComponent.width).toEqual('600px');
                expect(storageService.setValueInLocalStorage).toHaveBeenCalledWith(
                    storefrontPageComponent.PAGE_TREE_PANEL_OPEN_COOKIE_NAME,
                    true,
                    true
                );
            }
        );
    });

    describe('handleOpenInPageTree', () => {
        it('WHEN page tree panel is open THEN do nothing', async () => {
            // WHEN
            storefrontPageComponent.isPageTreePanelOpen = true;
            spyOn(storefrontPageComponent, 'getPageTreePanelWidth');
            spyOn(storefrontPageComponent, 'updateStorage');

            // THEN
            await storefrontPageComponent.handleOpenInPageTree();

            expect(storefrontPageComponent.getPageTreePanelWidth).not.toHaveBeenCalled();
            expect(storefrontPageComponent.updateStorage).not.toHaveBeenCalledWith();
        });

        it('WHEN page tree panel is close THEN open panel, get the width and update status', async () => {
            // WHEN
            storefrontPageComponent.isPageTreePanelOpen = false;
            spyOn(storefrontPageComponent, 'getPageTreePanelWidth');
            spyOn(storefrontPageComponent, 'updateStorage');

            // THEN
            await storefrontPageComponent.handleOpenInPageTree();

            expect(storefrontPageComponent.isPageTreePanelOpen).toBeTrue();
            expect(storefrontPageComponent.getPageTreePanelWidth).toHaveBeenCalled();
            expect(storefrontPageComponent.updateStorage).toHaveBeenCalledWith();
        });
    });

    describe('handlePerspectiveChanged', () => {
        it('WHEN perspective change to advanced edit THEN open the panel by status in local storage', async () => {
            // WHEN
            perspectiveService.getActivePerspective.and.returnValue({
                key: CMSModesService.ADVANCED_PERSPECTIVE_KEY,
                nameI18nKey: CMSModesService.ADVANCED_PERSPECTIVE_KEY,
                features: []
            });
            storageService.getValueFromLocalStorage
                .withArgs(PAGE_TREE_PANEL_WIDTH_COOKIE_NAME, true)
                .and.returnValue(Promise.resolve(null));
            storageService.getValueFromLocalStorage
                .withArgs(storefrontPageComponent.PAGE_TREE_PANEL_OPEN_COOKIE_NAME, true)
                .and.returnValue(Promise.resolve(true));

            // THEN
            await storefrontPageComponent.handlePerspectiveChanged();

            expect(storefrontPageComponent.isPageTreePanelOpen).toBeTrue();
            expect(storefrontPageComponent.width).toEqual('20%');
        });

        it('WHEN perspective change to not advanced edit THEN close the page tree panel', async () => {
            // WHEN
            perspectiveService.getActivePerspective.and.returnValue({
                key: CMSModesService.VERSIONING_PERSPECTIVE_KEY,
                nameI18nKey: CMSModesService.VERSIONING_PERSPECTIVE_KEY,
                features: []
            });
            spyOn(storefrontPageComponent, 'getPageTreePanelStatus');
            spyOn(storefrontPageComponent, 'getPageTreePanelWidth');

            // THEN
            await storefrontPageComponent.handlePerspectiveChanged();

            expect(storefrontPageComponent.isPageTreePanelOpen).toBeFalse();
            expect(storefrontPageComponent.getPageTreePanelStatus).not.toHaveBeenCalled();
            expect(storefrontPageComponent.getPageTreePanelWidth).not.toHaveBeenCalled();
        });
    });
});
