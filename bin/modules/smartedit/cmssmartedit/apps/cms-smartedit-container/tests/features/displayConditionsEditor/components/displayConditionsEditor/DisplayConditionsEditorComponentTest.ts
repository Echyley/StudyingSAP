/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import {
    HomepageService,
    DisplayConditionsEditorModel,
    CatalogHomepageDetailsStatus,
    ICatalogHomepageDetails
} from 'cmssmarteditcontainer';
import { DisplayConditionsEditorComponent } from 'cmssmarteditcontainer/components/pages/displayConditions/displayConditionsEditor/DisplayConditionsEditorComponent';
import { of } from 'rxjs';
import {
    GenericEditorWidgetData,
    LogService,
    L10nPipe,
    IPageService,
    ICMSPage
} from 'smarteditcommons';

describe('DisplayConditionsEditorComponent', () => {
    let component: DisplayConditionsEditorComponent;
    let activatedRoute: ActivatedRoute;
    let displayConditionsEditorModel: jasmine.SpyObj<DisplayConditionsEditorModel>;
    let homepageService: jasmine.SpyObj<HomepageService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let logService: jasmine.SpyObj<LogService>;
    let l10n: jasmine.SpyObj<L10nPipe>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const mockRouteParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };
    const injectedData = {
        model: {
            uid: 'uid',
            label: '',
            homepage: false,
            typeCode: 'ContentPage'
        }
    } as unknown as GenericEditorWidgetData<ICMSPage>;

    const mockUriContext = {
        SITE_ID: mockRouteParams.siteId,
        CATALOG_ID: mockRouteParams.catalogId,
        CATALOG_VERSION: mockRouteParams.catalogVersion
    };

    const mockHomepageDetails = {
        status: 'LOCAL',
        parentCatalogName: { en: 'parent catalog name' },
        parentCatalogVersion: 'parent catalog version',
        targetCatalogName: { en: 'target catalog name' },
        targetCatalogVersion: 'target catalog version',
        currentHomepageUid: 'current_uid',
        currentHomepageName: 'current name',
        oldHomepageUid: 'old_uid'
    } as ICatalogHomepageDetails;

    beforeEach(() => {
        activatedRoute = {
            snapshot: {
                params: mockRouteParams
            }
        } as unknown as ActivatedRoute;
        displayConditionsEditorModel = jasmine.createSpyObj<DisplayConditionsEditorModel>(
            'displayConditionsEditorModel',
            ['initModel']
        );
        displayConditionsEditorModel.initModel.and.returnValue(Promise.resolve());

        homepageService = jasmine.createSpyObj<HomepageService>('homepageService', [
            'hasFallbackHomePage',
            'getHomepageDetailsForContext',
            'sendEventHideReplaceParentHomePageInfo',
            'sendEventShowReplaceParentHomePageInfo'
        ]);
        homepageService.hasFallbackHomePage.and.returnValue(Promise.resolve(true));
        homepageService.getHomepageDetailsForContext.and.returnValue(
            Promise.resolve(mockHomepageDetails)
        );

        pageService = jasmine.createSpyObj<IPageService>('pageService', [
            'buildUriContextForCurrentPage',
            'isPagePrimaryWithContext'
        ]);
        pageService.buildUriContextForCurrentPage.and.returnValue(Promise.resolve(mockUriContext));
        pageService.isPagePrimaryWithContext.and.returnValue(Promise.resolve(true));

        logService = jasmine.createSpyObj<LogService>('logService', ['error']);

        l10n = jasmine.createSpyObj<L10nPipe>('l10n', ['transform']);
        l10n.transform.and.callFake((map: any) => of(map?.en || ''));

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
        translateService.instant.and.callFake((key, params) => {
            return key;
        });

        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new DisplayConditionsEditorComponent(
            injectedData,
            activatedRoute,
            displayConditionsEditorModel,
            homepageService,
            pageService,
            logService,
            l10n,
            translateService,
            cdr
        );
    });

    describe('initialization', () => {
        it('WHEN component is created THEN it should have basic values set', () => {
            expect(component.page).toEqual(injectedData.model);
            expect(component.hasFallback).toEqual(false);
            expect(component.isPrimaryPage).toEqual(false);
            expect(component.showReplaceLabel).toEqual(false);
            expect(component.homepageDetails).toEqual({
                status: 'PENDING' as CatalogHomepageDetailsStatus
            });
        });

        it('GIVEN pageService does not throw error WHEN ngOnInit is triggered THEN it should initialize displayConditionsEditorModel, set primary page, has fallback and homepage details values', async () => {
            await component.ngOnInit();

            expect(component.isPrimaryPage).toEqual(true);
            expect(component.hasFallback).toEqual(true);
            expect(component.homepageDetails).toEqual(mockHomepageDetails);

            expect(displayConditionsEditorModel.initModel).toHaveBeenCalledWith(
                injectedData.model.uid
            );
            expect(pageService.buildUriContextForCurrentPage).toHaveBeenCalledWith(
                mockRouteParams.siteId,
                mockRouteParams.catalogId,
                mockRouteParams.catalogVersion
            );
            expect(pageService.isPagePrimaryWithContext).toHaveBeenCalledWith(
                injectedData.model.uid,
                mockUriContext
            );
            expect(homepageService.hasFallbackHomePage).toHaveBeenCalledWith(mockUriContext);
            expect(homepageService.getHomepageDetailsForContext).toHaveBeenCalledWith(
                mockUriContext
            );
        });

        it('GIVEN pageService throw error WHEN ngOnInit is triggered THEN it should call log service', async () => {
            pageService.buildUriContextForCurrentPage.and.returnValue(Promise.reject());

            await component.ngOnInit();

            expect(displayConditionsEditorModel.initModel).toHaveBeenCalledWith(
                injectedData.model.uid
            );
            expect(pageService.buildUriContextForCurrentPage).toHaveBeenCalledWith(
                mockRouteParams.siteId,
                mockRouteParams.catalogId,
                mockRouteParams.catalogVersion
            );
            expect(logService.error).toHaveBeenCalled();
            expect(pageService.isPagePrimaryWithContext).not.toHaveBeenCalled();
            expect(homepageService.hasFallbackHomePage).not.toHaveBeenCalled();
            expect(homepageService.getHomepageDetailsForContext).not.toHaveBeenCalled();
        });
    });

    describe('disableHomepageCheckbox', () => {
        it('WHEN it has fallback THEN it should return false', () => {
            component.hasFallback = true;

            expect(component.disableHomepageCheckbox()).toEqual(false);
        });

        it('WHEN it does not have fallback AND homepage status is different than LOCAL THEN it should return true', () => {
            component.hasFallback = false;
            component.homepageDetails.status = CatalogHomepageDetailsStatus.PARENT;

            expect(component.disableHomepageCheckbox()).toEqual(true);
        });

        it('WHEN it does not have fallback, homepage status is equal LOCAL AND current homepage uid is equal page uid THEN it should return true', () => {
            component.hasFallback = false;
            component.homepageDetails.status = CatalogHomepageDetailsStatus.LOCAL;
            component.homepageDetails.currentHomepageUid = injectedData.model.uid;

            expect(component.disableHomepageCheckbox()).toEqual(true);
        });

        it('WHEN it does not have fallback, homepage status is equal LOCAL AND old homepage uid is equal page uid THEN it should return false', () => {
            component.hasFallback = false;
            component.homepageDetails.status = CatalogHomepageDetailsStatus.LOCAL;
            component.homepageDetails.oldHomepageUid = injectedData.model.uid;

            expect(component.disableHomepageCheckbox()).toEqual(false);
        });
    });

    describe('homepageChanged', () => {
        it('WHEN it is not a homepage THEN it should set that value, send event to hide replace label, and set replace label to false', async () => {
            await component.homePageChanged(false);

            expect(component.showReplaceLabel).toEqual(false);
            expect(component.page.homepage).toEqual(false);
            expect(homepageService.sendEventHideReplaceParentHomePageInfo).toHaveBeenCalled();
        });

        it('WHEN it is a homepage AND homepage status is NO_HOMEPAGE THEN it should hide replace parent homepage info', async () => {
            component.homepageDetails.status = CatalogHomepageDetailsStatus.NO_HOMEPAGE;
            await component.homePageChanged(true);

            expect(component.showReplaceLabel).toEqual(false);
            expect(component.page.homepage).toEqual(true);
            expect(homepageService.sendEventHideReplaceParentHomePageInfo).toHaveBeenCalled();
        });

        it('WHEN it is a homepage AND homepage status is PARENT THEN it should show replace parent homepage info', async () => {
            await component.ngOnInit();
            component.homepageDetails.status = CatalogHomepageDetailsStatus.PARENT;
            await component.homePageChanged(true);

            expect(component.showReplaceLabel).toEqual(false);
            expect(component.page.homepage).toEqual(true);
            expect(homepageService.sendEventShowReplaceParentHomePageInfo).toHaveBeenCalledWith({
                description: 'se.cms.display.conditions.homepage.replace.parent.info.header'
            });
            expect(translateService.instant).toHaveBeenCalledWith(
                'se.cms.display.conditions.homepage.replace.parent.info.header',
                {
                    parentCatalogName: 'parent catalog name',
                    parentCatalogVersion: 'parent catalog version',
                    targetCatalogName: 'target catalog name',
                    targetCatalogVersion: 'target catalog version'
                }
            );
        });

        it('WHEN it is a homepage AND homepage status is LOCAL THEN it should hide replace parent home page info and set showReplace label to true', async () => {
            component.homepageDetails.status = CatalogHomepageDetailsStatus.LOCAL;
            await component.homePageChanged(true);

            expect(component.showReplaceLabel).toEqual(true);
            expect(component.page.homepage).toEqual(true);
            expect(homepageService.sendEventHideReplaceParentHomePageInfo).toHaveBeenCalled();
        });
    });

    describe('showHomePageWidget', () => {
        it('WHEN homepage status is not pending, typecode is equal ContentPage and the page is primary THEN it should return true', () => {
            component.homepageDetails.status = CatalogHomepageDetailsStatus.LOCAL;
            component.page.typeCode = 'ContentPage';
            component.isPrimaryPage = true;

            expect(component.showHomePageWidget()).toEqual(true);
        });
    });

    describe('onPrimaryPageSelect', () => {
        it('WHEN called THEN it should assign page label', () => {
            const mockPage = {
                label: 'newLabel'
            } as ICMSPage;

            component.onPrimaryPageSelect(mockPage);

            expect(component.page.label).toEqual('newLabel');
        });
    });

    describe('getRouteParams', () => {
        let componentAny: any;

        it('WHEN first level route does not have firstChild THEN it should get params from that route', () => {
            activatedRoute = {
                firstChild: null,
                snapshot: { params: mockRouteParams }
            } as unknown as ActivatedRoute;
            component = new DisplayConditionsEditorComponent(
                injectedData,
                activatedRoute,
                displayConditionsEditorModel,
                homepageService,
                pageService,
                logService,
                l10n,
                translateService,
                cdr
            );
            componentAny = component;

            const actual = componentAny.getRouteParams();

            expect(actual).toEqual(mockRouteParams);
        });

        it('WHEN first level has firstChild and second level does not while it has no params THEN it should return empty object', () => {
            activatedRoute = {
                firstChild: {
                    snapshot: { params: {} }
                },
                snapshot: {}
            } as unknown as ActivatedRoute;
            component = new DisplayConditionsEditorComponent(
                injectedData,
                activatedRoute,
                displayConditionsEditorModel,
                homepageService,
                pageService,
                logService,
                l10n,
                translateService,
                cdr
            );
            componentAny = component;

            const actual = componentAny.getRouteParams();

            expect(actual).toEqual({});
        });

        it('WHEN first, second, and third levels have firstChild THEN it should return params from 4th level (which does not have firstChild)', () => {
            activatedRoute = {
                firstChild: {
                    snapshot: { params: {} },
                    firstChild: {
                        snapshot: { params: {} },
                        firstChild: {
                            snapshot: { params: mockRouteParams }
                        }
                    }
                },
                snapshot: {}
            } as unknown as ActivatedRoute;
            component = new DisplayConditionsEditorComponent(
                injectedData,
                activatedRoute,
                displayConditionsEditorModel,
                homepageService,
                pageService,
                logService,
                l10n,
                translateService,
                cdr
            );
            componentAny = component;

            const actual = componentAny.getRouteParams();

            expect(actual).toEqual(mockRouteParams);
        });
    });
});
