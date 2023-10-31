/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, SimpleChange } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NewPageDisplayConditionComponent } from 'cmssmarteditcontainer/components/pages/addPageWizard/components/newPageDisplayCondition/NewPageDisplayConditionComponent';
import { DisplayConditionsFacade } from 'cmssmarteditcontainer/facades';
import {
    HomepageService,
    IDisplayCondition,
    PageDisplayConditionsService
} from 'cmssmarteditcontainer/services';
import { of } from 'rxjs';
import { IUrlService, L10nPipe, LogService, IPageService, ICMSPage } from 'smarteditcommons';

describe('NewPageDisplayConditionComponent', () => {
    let component: NewPageDisplayConditionComponent;
    let componentAny: any;
    let urlService: jasmine.SpyObj<IUrlService>;
    let homepageService: jasmine.SpyObj<HomepageService>;
    let displayConditionsFacade: jasmine.SpyObj<DisplayConditionsFacade>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let logService: jasmine.SpyObj<LogService>;
    let pageDisplayConditions: jasmine.SpyObj<PageDisplayConditionsService>;
    let l10n: jasmine.SpyObj<L10nPipe>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const mockConditions: IDisplayCondition[] = [
        {
            isPrimary: true,
            description: 'description1',
            label: 'label1'
        },
        {
            isPrimary: false,
            description: 'description2',
            label: 'label2'
        }
    ];

    beforeEach(() => {
        urlService = jasmine.createSpyObj<IUrlService>('urlService', ['buildUriContext']);
        homepageService = jasmine.createSpyObj<HomepageService>('homepageService', [
            'getHomepageDetailsForContext',
            'sendEventHideReplaceParentHomePageInfo',
            'sendEventShowReplaceParentHomePageInfo',
            'sendEventHideReplaceParentHomePageInfo'
        ]);
        displayConditionsFacade = jasmine.createSpyObj<DisplayConditionsFacade>(
            'displayConditionsFacade',
            ['getPrimaryPagesForPageType']
        );
        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
        pageService = jasmine.createSpyObj<IPageService>('pageService', [
            'getPaginatedPrimaryPagesForPageType',
            'getPageById',
            'getPageByUuid',
            'getPrimaryPage'
        ]);
        logService = jasmine.createSpyObj<LogService>('logService', ['error']);
        pageDisplayConditions = jasmine.createSpyObj<PageDisplayConditionsService>(
            'pageDisplayConditions',
            ['getNewPageConditions']
        );
        l10n = jasmine.createSpyObj<L10nPipe>('l10n', ['transform']);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new NewPageDisplayConditionComponent(
            urlService,
            homepageService,
            displayConditionsFacade,
            translateService,
            pageService,
            logService,
            pageDisplayConditions,
            l10n,
            cdr
        );
        componentAny = component;
    });

    describe('initialize', () => {
        it('should set condition related fields', () => {
            component.initialConditionSelectedKey = 'page.displaycondition.variation';
            component.ngOnInit();

            expect(component.conditionSelected).toEqual({} as IDisplayCondition);
            expect(component.conditionSelectorFetchStrategy).toEqual({
                fetchAll: jasmine.any(Function)
            });
            expect(component.initialConditionSelectedKey).toEqual(
                'page.displaycondition.variation'
            );
        });

        it('GIVEN initially selected condition is not selected it should fallback to primary', () => {
            component.ngOnInit();

            expect(component.conditionSelected).toEqual({} as IDisplayCondition);
            expect(component.conditionSelectorFetchStrategy).toEqual({
                fetchAll: jasmine.any(Function)
            });
            expect(component.initialConditionSelectedKey).toEqual('page.displaycondition.primary');
        });

        it('should define method that will return mapped condition in condition selector strategy', async () => {
            component.conditions = mockConditions;
            component.ngOnInit();

            const method = component.conditionSelectorFetchStrategy.fetchAll;

            const actual = await method();

            expect(actual).toEqual([
                {
                    id: 'label1',
                    label: 'label1'
                },
                {
                    id: 'label2',
                    label: 'label2'
                }
            ]);
        });

        it('should set primary page fetch strategy', () => {
            component.ngOnInit();

            expect(component.primaryPageChoicesFetchStrategy).toEqual({
                fetchEntity: jasmine.any(Function),
                fetchPage: jasmine.any(Function)
            });
        });
    });

    describe('onChanges', () => {
        it('GIVEN pageTypeCode has changed AND there is target catalogVersion but uriContext is equal to catalogVersion THEN it should get selected primary page and display condition', async () => {
            componentAny.targetCatalogVersion = { catalogVersion: 'staged' };
            const change = new SimpleChange('oldcode', 'newcode', true);
            spyOn(componentAny, 'isUriContextEqualToCatalogVersion').and.returnValue(true);
            spyOn(componentAny, 'getSelectedPrimaryPageAndDisplayCondition').and.returnValue(
                Promise.resolve()
            );

            await componentAny.ngOnChanges({ pageTypeCode: change });

            expect(componentAny.getSelectedPrimaryPageAndDisplayCondition).toHaveBeenCalled();
        });

        it('GIVEN targetCatalogVersion has changed AND there is target catalogVersion THEN it should get only primary page', async () => {
            const change = new SimpleChange(
                { catalogVersion: 'staged' },
                { catalogVersion: 'online' },
                true
            );
            componentAny.targetCatalogVersion = { catalogVersion: 'staged' };
            spyOn(componentAny, 'getOnlyPrimaryDisplayCondition').and.returnValue(
                Promise.resolve()
            );

            await componentAny.ngOnChanges({ targetCatalogVersion: change });

            expect(componentAny.getOnlyPrimaryDisplayCondition).toHaveBeenCalled();
        });

        it('GIVEN pageTypeCode has changed AND there is target catalogVersion AND uriContext is different than catalogVersion THEN it should get only primary display condition', async () => {
            componentAny.targetCatalogVersion = { catalogVersion: 'staged' };
            const change = new SimpleChange('oldcode', 'newcode', true);
            spyOn(componentAny, 'isUriContextEqualToCatalogVersion').and.returnValue(false);
            spyOn(componentAny, 'getOnlyPrimaryDisplayCondition').and.returnValue(
                Promise.resolve()
            );

            await componentAny.ngOnChanges({ pageTypeCode: change });

            expect(componentAny.getOnlyPrimaryDisplayCondition).toHaveBeenCalled();
        });

        it('GIVEN pageTypeHasChanged THEN it should get homepageDetails and update ui properties', async () => {
            const change = new SimpleChange('oldcode', 'newcode', true);
            spyOn(componentAny, 'updateHomepageUiProperties').and.returnValue(Promise.resolve());
            homepageService.getHomepageDetailsForContext.and.returnValue(
                Promise.resolve<any>({ status: 'LOCAL' })
            );

            await componentAny.ngOnChanges({ pageTypeCode: change });

            expect(componentAny.updateHomepageUiProperties).toHaveBeenCalled();
        });

        it('GIVEN neither pageTypeHasChanged nor targetCatalogVersion THEN it should only update ui properties', async () => {
            spyOn(componentAny, 'updateHomepageUiProperties').and.returnValue(Promise.resolve());
            await componentAny.ngOnChanges({});

            expect(componentAny.updateHomepageUiProperties).toHaveBeenCalled();
        });
    });

    describe('showPrimarySelector', () => {
        it('should return true when selected condition is not primary', () => {
            component.conditionSelected = {
                label: 'label',
                isPrimary: false,
                description: 'description'
            };

            expect(component.showPrimarySelector()).toEqual(true);
        });
    });

    describe('onConditionChange', () => {
        it('should find condition by label and assign it to selected condition', () => {
            component.conditions = mockConditions;

            component.onConditionChange('label1');

            expect(component.conditionSelected).toEqual(mockConditions[0]);
        });
    });

    describe('onHomePageChange', () => {
        it('should assign new value and call data change', () => {
            spyOn(componentAny, 'dataChanged');

            component.onHomePageChange(true);

            expect(component.homepage).toEqual(true);
            expect(componentAny.dataChanged).toHaveBeenCalled();
        });
    });

    describe('showHomePageWidget', () => {
        it('should show home page widget only if selected condition is primary and page type code is equal ContentPage', () => {
            component.conditionSelected = {
                label: 'label',
                description: 'description',
                isPrimary: true
            };
            component.pageTypeCode = 'ContentPage';

            expect(component.showHomePageWidget()).toEqual(true);
        });
    });

    describe('primarySelectedModelOnChange', () => {
        it('should get page, set primary selected page and call data change', async () => {
            pageService.getPageById.and.returnValue(
                Promise.resolve({
                    uid: 'uid'
                } as ICMSPage)
            );

            await component.primarySelectedModelOnChange('uid');

            expect(component.primarySelected).toEqual({ uid: 'uid' } as ICMSPage);
            expect(component.primarySelectedModel).toEqual('uid');
            expect(pageService.getPageById).toHaveBeenCalledWith('uid');
        });
    });

    describe('dataChanged', () => {
        it('GIVEN condition is not primary THEN it should reset homepage value update homepage ui properties', () => {
            spyOn(componentAny, 'updateHomepageUiProperties');
            componentAny.conditionSelected = { isPrimary: false };
            componentAny.pageTypeCode = 'ContentPage';

            componentAny.dataChanged();

            expect(componentAny.updateHomepageUiProperties).toHaveBeenCalled();
            expect(componentAny.homepage).toEqual(undefined);
        });

        it('GIVEN condition is primary and page code is ContentPage AND resultFn is defined THEN it should update homepage UI properties and call resultFn with param', () => {
            spyOn(componentAny, 'updateHomepageUiProperties');
            const resultFn = jasmine.createSpy();
            componentAny.homepage = true;
            componentAny.conditionSelected = { isPrimary: true };
            componentAny.pageTypeCode = 'ContentPage';
            componentAny.resultFn = resultFn;

            componentAny.dataChanged();

            expect(componentAny.updateHomepageUiProperties).toHaveBeenCalled();
            expect(resultFn).toHaveBeenCalledWith({
                homepage: true,
                isPrimary: true,
                primaryPage: null
            });
        });

        it('GIVEN condition is not primary and page code is ContentPage AND resultFn is defined THEN it should update homepage UI properties and call resultFn with param', () => {
            spyOn(componentAny, 'updateHomepageUiProperties');
            const resultFn = jasmine.createSpy();
            componentAny.homepage = true;
            componentAny.conditionSelected = { isPrimary: false, label: 'label' };
            componentAny.pageTypeCode = 'ContentPage';
            componentAny.resultFn = resultFn;

            componentAny.dataChanged();

            expect(componentAny.updateHomepageUiProperties).toHaveBeenCalled();
            expect(resultFn).toHaveBeenCalledWith({
                homepage: undefined,
                isPrimary: false,
                primaryPage: null
            });
        });
    });

    describe('updateHomepageUiProperties', () => {
        it('GIVEN homepage is falsy THEN it should send even to hide replace parent homepage and set showReplaceLabel to false', async () => {
            componentAny.homepage = false;

            await componentAny.updateHomepageUiProperties();

            expect(homepageService.sendEventHideReplaceParentHomePageInfo).toHaveBeenCalled();
            expect(componentAny.showReplaceLabel).toEqual(false);
        });

        it('GIVEN homepage status is NO_HOMEPAGE THEN  it should send even to hide replace parent homepage and set showReplaceLabel to false', async () => {
            componentAny.homepage = true;
            componentAny.homepageDetails = { status: 'NO_HOMEPAGE' };

            await componentAny.updateHomepageUiProperties();

            expect(homepageService.sendEventHideReplaceParentHomePageInfo).toHaveBeenCalled();
            expect(componentAny.showReplaceLabel).toEqual(false);
        });

        it('GIVEN homepage status is LOCAL THEN it should send even to hide replace parent homepage, set current home page name and set showReplaceLabel to true', async () => {
            componentAny.homepage = true;
            componentAny.homepageDetails = { status: 'LOCAL', currentHomepageName: 'homepagename' };

            await componentAny.updateHomepageUiProperties();

            expect(homepageService.sendEventHideReplaceParentHomePageInfo).toHaveBeenCalled();
            expect(componentAny.showReplaceLabel).toEqual(true);
            expect(componentAny.currentHomePageName).toEqual('homepagename');
        });

        it('GIVEN homepage status is PARENT THEN it should send even to show replace parent homepage, set current home page name and set showReplaceLabel to false', async () => {
            l10n.transform.and.callFake((arg) => {
                if (arg === 'parentCatalog') {
                    return of('parent catalog name');
                }
                if (arg === 'targetCatalog') {
                    return of('target catalog name');
                }
            });
            componentAny.homepage = true;
            componentAny.homepageDetails = {
                status: 'PARENT',
                parentCatalogName: 'parentCatalog',
                parentCatalogVersion: 'online',
                targetCatalogName: 'targetCatalog',
                targetCatalogVersion: 'online'
            };

            await componentAny.updateHomepageUiProperties();

            expect(homepageService.sendEventShowReplaceParentHomePageInfo).toHaveBeenCalled();
            expect(translateService.instant).toHaveBeenCalledWith(
                'se.cms.display.conditions.homepage.replace.parent.info.header',
                {
                    parentCatalogName: 'parent catalog name',
                    parentCatalogVersion: 'online',
                    targetCatalogName: 'target catalog name',
                    targetCatalogVersion: 'online'
                }
            );
            expect(componentAny.showReplaceLabel).toEqual(false);
        });
    });

    describe('getSelectedPrimaryPageAndDisplayCondition', () => {
        it('GIVEN there is no page type code THEN it should call getAllPrimaryDisplayCondition', async () => {
            spyOn(componentAny, 'getAllPrimaryDisplayCondition');

            componentAny.pageTypeCode = null;

            await componentAny.getSelectedPrimaryPageAndDisplayCondition();

            expect(componentAny.getAllPrimaryDisplayCondition).toHaveBeenCalled();
        });

        it('GIVEN there is page typecode AND given page is a primary page WHEN page service resolves THEN it should set this page as a primary page', async () => {
            const primaryPage = {
                uid: 'primaryPageUid',
                defaultPage: true
            };
            pageService.getPageByUuid.and.returnValue(Promise.resolve<any>(primaryPage));
            component.pageTypeCode = 'code';
            component.uriContext = { context: 'context' };
            component.pageUuid = 'primaryPageUuid';

            await componentAny.getSelectedPrimaryPageAndDisplayCondition();

            expect(componentAny.primarySelected).toEqual(primaryPage);
            expect(componentAny.primarySelectedModel).toEqual(primaryPage.uid);
        });

        it('GIVEN there is a page typecode AND given page is a variation page WHEN page service resolves THEN it should fetch primary page and set it', async () => {
            const variationPageUid = 'variationPageUid';
            const variationPageUuid = 'variationPageUuid';
            const primaryPage = {
                uid: 'primaryPageUid'
            } as ICMSPage;

            pageService.getPageByUuid.and.returnValue(
                Promise.resolve<any>({ uid: variationPageUid, defaultPage: false })
            );
            pageService.getPrimaryPage.and.returnValue(Promise.resolve(primaryPage));
            component.pageTypeCode = 'code';
            component.uriContext = { context: 'context' };
            component.pageUuid = variationPageUuid;

            await componentAny.getSelectedPrimaryPageAndDisplayCondition();

            expect(component.primarySelected).toEqual(primaryPage);
            expect(component.primarySelectedModel).toEqual(primaryPage.uid);
        });

        it('WHEN it sets primary page it should also call getAllPrimaryDisplayCondition', async () => {
            component.pageTypeCode = 'code';
            component.uriContext = { context: 'context' };
            component.pageUuid = 'pageUuid';

            spyOn(componentAny, 'getAllPrimaryDisplayCondition');

            await componentAny.getSelectedPrimaryPageAndDisplayCondition();

            expect(componentAny.getAllPrimaryDisplayCondition).toHaveBeenCalled();
        });

        it('GIVEN there is a page typecode and pageUuid WHEN page service rejects THEN it should call log service and call getAllPrimaryDisplayCondition', async () => {
            const error = new Error('rejected');
            const pageUuid = 'pageUuid';
            pageService.getPageByUuid.and.returnValue(Promise.reject(error));
            spyOn(componentAny, 'getAllPrimaryDisplayCondition');

            component.pageTypeCode = 'code';
            component.uriContext = { context: 'context' };
            component.pageUuid = pageUuid;

            await componentAny.getSelectedPrimaryPageAndDisplayCondition();

            expect(pageService.getPageByUuid).toHaveBeenCalledWith(pageUuid);
            expect(logService.error).toHaveBeenCalled();
            expect(componentAny.getAllPrimaryDisplayCondition).toHaveBeenCalled();
        });
    });

    describe('getAllPrimaryDisplayCondition', () => {
        it('WHEN pageDisplayConditions resolves and response is empty THEN it should do nothing', async () => {
            pageDisplayConditions.getNewPageConditions.and.returnValue(Promise.resolve([]));

            await componentAny.getAllPrimaryDisplayCondition();

            // component didn't reach is ready part
            expect(componentAny.isReady).toEqual(false);
            // component didn't reach catch block
            expect(logService.error).not.toHaveBeenCalled();
        });

        it('WHEN pageDisplayConditions resolves and response has exactly one record THEN it should assign conditions and condition selected and set isReady to true', async () => {
            pageDisplayConditions.getNewPageConditions.and.returnValue(
                Promise.resolve([mockConditions[0]])
            );

            await componentAny.getAllPrimaryDisplayCondition();

            expect(componentAny.conditions).toEqual([mockConditions[0]]);
            expect(componentAny.conditionSelected).toEqual(mockConditions[0]);
            expect(componentAny.isReady).toEqual(true);
        });

        it('WHEN pageDisplayConditions resolves and response has more than one record THEN it should assign conditions and find condition equal to initial condition selected, assign it to condition selected, and set isReady to true', async () => {
            pageDisplayConditions.getNewPageConditions.and.returnValue(
                Promise.resolve(mockConditions)
            );
            componentAny.initialConditionSelectedKey = mockConditions[1].label;

            await componentAny.getAllPrimaryDisplayCondition();

            expect(componentAny.conditions).toEqual(mockConditions);
            expect(componentAny.conditionSelected).toEqual(mockConditions[1]);
            expect(componentAny.isReady).toEqual(true);
        });

        it('WHEN pageDisplayConditions rejects THEN it should call log service', async () => {
            const error = new Error('rejected');
            pageDisplayConditions.getNewPageConditions.and.returnValue(Promise.reject(error));

            await componentAny.getAllPrimaryDisplayCondition();

            expect(logService.error).toHaveBeenCalledWith(error);
        });

        describe('finally block', () => {
            it('WHEN pageDisplayConditions resolves and response has exactly one record THEN it if there is target catalog version and uri context is not equal to catalog version it should get only primary display condition', async () => {
                pageDisplayConditions.getNewPageConditions.and.returnValue(Promise.resolve([]));
                spyOn(componentAny, 'isUriContextEqualToCatalogVersion').and.returnValue(false);
                spyOn(componentAny, 'getOnlyPrimaryDisplayCondition');
                componentAny.targetCatalogVersion = { catalogVersion: 'online' };

                await componentAny.getAllPrimaryDisplayCondition();

                expect(componentAny.getOnlyPrimaryDisplayCondition).toHaveBeenCalled();
            });

            it('WHEN pageDisplayConditions resolves and response has exactly one record THEN it if there is target catalog version and uri context is equal to catalog version it should call data change', async () => {
                pageDisplayConditions.getNewPageConditions.and.returnValue(Promise.resolve([]));
                spyOn(componentAny, 'isUriContextEqualToCatalogVersion').and.returnValue(true);
                spyOn(componentAny, 'dataChanged');
                componentAny.targetCatalogVersion = { catalogVersion: 'online' };

                await componentAny.getAllPrimaryDisplayCondition();

                expect(componentAny.dataChanged).toHaveBeenCalled();
            });
        });
    });

    describe('getOnlyPrimaryDisplayCondition', () => {
        it('should set only one condition, condition selected, isReady to true and call data changed', () => {
            spyOn(componentAny, 'dataChanged');

            componentAny.getOnlyPrimaryDisplayCondition();

            expect(componentAny.conditions).toEqual([
                {
                    description: 'page.displaycondition.primary.description',
                    isPrimary: true,
                    label: 'page.displaycondition.primary'
                }
            ]);
            expect(componentAny.conditionSelected).toEqual({
                description: 'page.displaycondition.primary.description',
                isPrimary: true,
                label: 'page.displaycondition.primary'
            });
            expect(componentAny.isReady).toEqual(true);
            expect(componentAny.dataChanged).toHaveBeenCalled();
        });
    });

    describe('isUriContextEqualToCatalogVersion', () => {
        it('should return true only when catalogVersion siteId, catalogId, version are equal to corresponding values in given uricontext', () => {
            const catalogVersion = {
                siteId: 'siteId',
                catalogId: 'catalogId',
                version: 'version'
            };
            const context = {
                CURRENT_CONTEXT_SITE_ID: 'siteId',
                CURRENT_CONTEXT_CATALOG: 'catalogId',
                CURRENT_CONTEXT_CATALOG_VERSION: 'version'
            };

            expect(componentAny.isUriContextEqualToCatalogVersion(context, catalogVersion)).toEqual(
                true
            );
        });
    });
});
