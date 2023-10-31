/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { CMSItemStructure, CMSItemStructureField } from 'cmscommons';
import {
    PageEditorModalService,
    PageInfoMenuService
} from 'cmssmarteditcontainer/components/pages/services';
import { TypeStructureRestService } from 'cmssmarteditcontainer/dao/TypeStructureRestService';
import {
    DisplayConditionsFacade,
    IDisplayConditionsPrimaryPage
} from 'cmssmarteditcontainer/facades';
import {
    LogService,
    PageVersionSelectionService,
    IPageVersion,
    IPageService,
    ICMSPage,
    CMSPageTypes,
    CmsApprovalStatus,
    CMSPageStatus
} from 'smarteditcommons';

describe('PageInfoMenuService', () => {
    const TEMPLATE_UID = 'SOME TEMPLATE UID';
    const PRIMARY_PAGE_NAME = 'SOME PRIMARY PAGE NAME';
    const PAGE_TYPE_CODE = CMSPageTypes.ContentPage;
    const PAGE_VERSION_ID = 'PAGE VERSION ID';
    const PAGE_UUID = 'PAGE UUID';

    const ANY_RESTRICTION_CRITERIA = 'se.cms.restrictions.criteria.any';
    const ALL_RESTRICTION_CRITERIA = 'se.cms.restrictions.criteria.all';

    const VARIATION_DISPLAY_CONDITION = 'page.displaycondition.variation';
    const PRIMARY_DISPLAY_CONDITION = 'page.displaycondition.primary';

    const CREATION_TIME = new Date();
    const MODIFICATION_TIME = new Date();

    const pageVersion: IPageVersion = {
        uid: PAGE_VERSION_ID,
        creationtime: null,
        itemUUID: PAGE_UUID,
        label: null
    };

    let displayConditionsFacade: jasmine.SpyObj<DisplayConditionsFacade>;
    let logService: jasmine.SpyObj<LogService>;
    let pageEditorModalService: jasmine.SpyObj<PageEditorModalService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let pageVersionSelectionService: jasmine.SpyObj<PageVersionSelectionService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let typeStructureRestService: jasmine.SpyObj<TypeStructureRestService>;

    let pageInfo: ICMSPage = null;
    let primaryPageInfo: IDisplayConditionsPrimaryPage;
    let structureRetrieved: CMSItemStructureField[];

    let service: PageInfoMenuService;
    let serviceAny: any;
    beforeEach(() => {
        structureRetrieved = [
            buildStructureField('restrictions'),
            buildStructureField('label'),
            buildStructureField('name'),
            buildStructureField('description'),
            buildStructureField('modifiedtime', 'Date', false),
            buildStructureField('title'),
            buildStructureField('other', 'Other'),
            buildStructureField('uid'),
            buildStructureField('creationtime', 'Date')
        ];

        pageInfo = {
            uid: 'some uid',
            uuid: 'some uuid',
            name: 'some name',
            approvalStatus: CmsApprovalStatus.APPROVED,
            pageStatus: CMSPageStatus.ACTIVE,
            type: null,
            typeCode: PAGE_TYPE_CODE,
            label: 'some label',
            masterTemplate: 'some master template',
            masterTemplateId: TEMPLATE_UID,
            title: null,
            defaultPage: true,
            creationtime: CREATION_TIME,
            modifiedtime: MODIFICATION_TIME,
            restrictions: [],
            onlyOneRestrictionMustApply: true,
            homepage: false,
            catalogVersion: 'some catalog version',
            displayStatus: 'draft'
        };

        primaryPageInfo = {
            name: PRIMARY_PAGE_NAME
        } as IDisplayConditionsPrimaryPage;
    });

    beforeEach(() => {
        displayConditionsFacade = jasmine.createSpyObj<DisplayConditionsFacade>(
            'displayConditionsFacade',
            ['getPrimaryPageForVariationPage']
        );

        logService = jasmine.createSpyObj<LogService>('logService', ['warn']);

        pageEditorModalService = jasmine.createSpyObj<PageEditorModalService>(
            'pageEditorModalService',
            ['open']
        );

        pageService = jasmine.createSpyObj<IPageService>('pageService', [
            'getCurrentPageInfoByVersion'
        ]);

        pageVersionSelectionService = jasmine.createSpyObj<PageVersionSelectionService>(
            'pageVersionSelectionService',
            ['getSelectedPageVersion']
        );

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        typeStructureRestService = jasmine.createSpyObj<TypeStructureRestService>(
            'typeStructureRestService',
            ['getStructureByType']
        );

        service = new PageInfoMenuService(
            displayConditionsFacade,
            logService,
            pageEditorModalService,
            pageService,
            pageVersionSelectionService,
            translateService,
            typeStructureRestService
        );
        serviceAny = service;
    });

    beforeEach(() => {
        displayConditionsFacade.getPrimaryPageForVariationPage.and.returnValue(
            Promise.resolve(primaryPageInfo)
        );

        pageEditorModalService.open.and.returnValue(Promise.resolve());

        pageService.getCurrentPageInfoByVersion.and.returnValue(Promise.resolve(pageInfo));

        pageVersionSelectionService.getSelectedPageVersion.and.returnValue(pageVersion);

        translateService.instant.and.callFake((label: string) => label);

        typeStructureRestService.getStructureByType.and.returnValue(
            Promise.resolve(structureRetrieved)
        );

        // Ensure that everything is properly clean.
        pageService.getCurrentPageInfoByVersion.calls.reset();
        pageEditorModalService.open.calls.reset();
    });

    it('GIVEN provided pageInfo is null WHEN editPage is called THEN a warning message is displayed in the console and no editor is opened', async () => {
        // GIVEN
        expect(logService.warn).not.toHaveBeenCalled();

        // WHEN
        await service.openPageEditor(null);

        // THEN
        expect(logService.warn).toHaveBeenCalledWith(
            '[PageInfoMenuService] - Cannot open page editor. Provided page is empty.'
        );
        expect(pageEditorModalService.open).not.toHaveBeenCalled();
    });

    it('GIVEN no page editor is opened WHEN editPage is called THEN it opens the page editor modal service', async () => {
        // GIVEN
        expect(pageEditorModalService.open).not.toHaveBeenCalled();

        // WHEN
        await service.openPageEditor(pageInfo);

        // THEN
        expect(pageEditorModalService.open).toHaveBeenCalled();
    });

    it('GIVEN a page editor is still opened WHEN editPage is called THEN no new modal is opened', async () => {
        // GIVEN
        serviceAny.isPageEditorOpened = true;

        // WHEN
        await service.openPageEditor(pageInfo);

        // THEN
        expect(pageEditorModalService.open).not.toHaveBeenCalled();
    });

    it('GIVEN current page info cannot be retrieved WHEN getCurrentPageInfo is called THEN a warning message is displayed in the console', async () => {
        // GIVEN
        pageService.getCurrentPageInfoByVersion.and.returnValue(Promise.reject());

        // WHEN
        await service.getCurrentPageInfo();

        // THEN
        expect(logService.warn).toHaveBeenCalledWith(
            '[PageInfoMenuService] - Cannot retrieve page info. Please try again later.'
        );
    });

    it('GIVEN current page is a variation AND the primary page for the current variation cannot be retrieved WHEN getCurrentPageInfo is called THEN a warning message is displayed in the console', async () => {
        // GIVEN
        markPageAsVariation();
        displayConditionsFacade.getPrimaryPageForVariationPage.and.returnValue(Promise.reject());

        // WHEN
        await service.getCurrentPageInfo();

        // THEN
        expect(logService.warn).toHaveBeenCalledWith(
            '[PageInfoMenuService] - Cannot retrieve page info. Please try again later.'
        );
    });

    it('GIVEN primary page WHEN getCurrentPageInfo is called THEN it returns a page with the appropriate information', async () => {
        // WHEN
        const currentPageInfo = await service.getCurrentPageInfo();
        // THEN
        expect(currentPageInfo).toEqual({
            uid: 'some uid',
            uuid: 'some uuid',
            name: 'some name',
            approvalStatus: CmsApprovalStatus.APPROVED,
            pageStatus: CMSPageStatus.ACTIVE,
            type: null,
            typeCode: PAGE_TYPE_CODE,
            label: 'some label',
            masterTemplate: 'some master template',
            masterTemplateId: TEMPLATE_UID,
            title: null,
            defaultPage: true,
            creationtime: CREATION_TIME,
            modifiedtime: MODIFICATION_TIME,
            restrictions: [],
            onlyOneRestrictionMustApply: true,
            primaryPage: null,
            restrictionsCriteria: null,
            template: TEMPLATE_UID,
            displayCondition: PRIMARY_DISPLAY_CONDITION,
            content: pageInfo,
            localizedType: PAGE_TYPE_CODE,
            homepage: false,
            catalogVersion: 'some catalog version',
            displayStatus: 'draft'
        });
    });

    it('GIVEN page is variation WHEN getCurrentPageInfo is called THEN it returns a page info with appropriate information', async () => {
        // GIVEN
        markPageAsVariation();

        // WHEN
        const currentPageInfo = await service.getCurrentPageInfo();
        // THEN
        expect(currentPageInfo).toEqual({
            uid: 'some uid',
            uuid: 'some uuid',
            name: 'some name',
            approvalStatus: CmsApprovalStatus.APPROVED,
            pageStatus: CMSPageStatus.ACTIVE,
            type: null,
            typeCode: PAGE_TYPE_CODE,
            label: 'some label',
            masterTemplate: 'some master template',
            masterTemplateId: TEMPLATE_UID,
            title: null,
            defaultPage: false,
            creationtime: CREATION_TIME,
            modifiedtime: MODIFICATION_TIME,
            restrictions: [],
            onlyOneRestrictionMustApply: true,
            template: TEMPLATE_UID,
            displayCondition: VARIATION_DISPLAY_CONDITION,
            primaryPage: PRIMARY_PAGE_NAME,
            content: pageInfo,
            localizedType: PAGE_TYPE_CODE,
            restrictionsCriteria: ANY_RESTRICTION_CRITERIA,
            homepage: false,
            catalogVersion: 'some catalog version',
            displayStatus: 'draft'
        });
    });

    it('GIVEN page is variation WHEN getCurrentPageInfo is called THEN it must return the right restrictions criteria', async () => {
        // GIVEN
        markPageAsVariation();
        markPageAsAllRestrictionsMustApply();

        // WHEN
        const currentPageInfo = await service.getCurrentPageInfo();
        // THEN
        expect(currentPageInfo).toEqual({
            uid: 'some uid',
            uuid: 'some uuid',
            name: 'some name',
            approvalStatus: CmsApprovalStatus.APPROVED,
            pageStatus: CMSPageStatus.ACTIVE,
            type: null,
            typeCode: PAGE_TYPE_CODE,
            label: 'some label',
            masterTemplate: 'some master template',
            masterTemplateId: TEMPLATE_UID,
            title: null,
            defaultPage: false,
            creationtime: CREATION_TIME,
            modifiedtime: MODIFICATION_TIME,
            restrictions: [],
            onlyOneRestrictionMustApply: false,
            template: TEMPLATE_UID,
            displayCondition: VARIATION_DISPLAY_CONDITION,
            primaryPage: PRIMARY_PAGE_NAME,
            content: pageInfo,
            localizedType: PAGE_TYPE_CODE,
            restrictionsCriteria: ALL_RESTRICTION_CRITERIA,
            homepage: false,
            catalogVersion: 'some catalog version',
            displayStatus: 'draft'
        });
    });

    it('GIVEN a page structure cannot be retrieved WHEN getPageStructureForViewing is called THEN a warning message is displayed in the console', async () => {
        // GIVEN
        typeStructureRestService.getStructureByType.and.returnValue(Promise.reject());

        // WHEN
        await service.getPageStructureForViewing(PAGE_TYPE_CODE, true);

        // THEN
        expect(logService.warn).toHaveBeenCalledWith(
            '[PageInfoMenuService] - Cannot retrieve page info structure. Please try again later.'
        );
    });

    it('GIVEN variation page WHEN getPageStructureForViewing is called THEN it returns the structure in the proper format for viewing', async () => {
        // GIVEN
        const structureExpected: CMSItemStructureField[] = [
            buildStructureField('name', 'InfoPageName', false),
            buildStructureField('displayCondition', 'ShortString', false),
            buildStructureField('description', 'InfoPageName', false),
            buildStructureField('title', 'InfoPageName', false),
            buildStructureField('label', 'InfoPageName', false),
            buildStructureField('localizedType', 'ShortString', false),
            buildStructureField('template', 'ShortString', false),
            buildStructureField('primaryPage', 'ShortString', false),
            buildStructureField('restrictions', 'RestrictionsList', false),
            buildStructureField('creationtime', 'Date', false),
            buildStructureField('modifiedtime', 'Date', false),
            buildStructureField('other', 'Other', false)
        ];

        // WHEN
        const structureActual = await service.getPageStructureForViewing(PAGE_TYPE_CODE, false);

        // THEN
        expect((structureActual as CMSItemStructure).attributes).toEqual(structureExpected);
    });

    it('GIVEN primary page WHEN getPageStructureForViewing is called THEN it returns the structure in the proper format for viewing', async () => {
        // GIVEN
        const structureExpected: CMSItemStructureField[] = [
            buildStructureField('name', 'InfoPageName', false),
            buildStructureField('displayCondition', 'ShortString', false),
            buildStructureField('description', 'InfoPageName', false),
            buildStructureField('title', 'InfoPageName', false),
            buildStructureField('label', 'InfoPageName', false),
            buildStructureField('localizedType', 'ShortString', false),
            buildStructureField('template', 'ShortString', false),
            buildStructureField('creationtime', 'Date', false),
            buildStructureField('modifiedtime', 'Date', false),
            buildStructureField('other', 'Other', false)
        ];

        // WHEN
        const structureActual = await service.getPageStructureForViewing(PAGE_TYPE_CODE, true);

        // THEN
        expect((structureActual as CMSItemStructure).attributes).toEqual(structureExpected);
    });

    // ---------------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------------
    const markPageAsVariation = () => {
        pageInfo.defaultPage = false;
    };

    const markPageAsAllRestrictionsMustApply = () => {
        pageInfo.onlyOneRestrictionMustApply = false;
    };

    function buildStructureField(
        qualifier: string,
        cmsStructureType = 'ShortString',
        editable = true
    ): CMSItemStructureField {
        return {
            cmsStructureType,
            qualifier,
            i18nKey: 'se.cms.pageinfo.page.' + qualifier.toLocaleLowerCase(),
            editable
        };
    }
});
