/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ClonePageBuilder } from 'cmssmarteditcontainer/components/pages/clonePageWizard';
import { RestrictionCMSItem } from 'cmssmarteditcontainer/components/restrictions/types';
import { TypeStructureRestService } from 'cmssmarteditcontainer/dao/TypeStructureRestService';
import { IRestrictionsStepHandler } from 'cmssmarteditcontainer/interfaces';
import { ContextAwarePageStructureService } from 'cmssmarteditcontainer/services';
import { RestrictionsStepHandlerFactory } from 'cmssmarteditcontainer/services/pages/RestrictionsStepHandlerFactory';
import {
    GenericEditorStructure,
    ICatalogService,
    ICatalogVersion,
    IPageInfoService,
    CmsitemsRestService,
    ICMSPage,
    CmsApprovalStatus,
    CMSPageStatus
} from 'smarteditcommons';

function mockPage(): ICMSPage {
    return {
        uuid: '',
        uid: '',
        creationtime: new Date(0),
        modifiedtime: new Date(0),
        pk: '',
        masterTemplate: '',
        masterTemplateId: '',
        name: '',
        label: '',
        typeCode: '',
        pageUuid: '',
        itemtype: '',
        type: '',
        catalogVersion: '',
        pageStatus: CMSPageStatus.ACTIVE,
        approvalStatus: CmsApprovalStatus.APPROVED,
        displayStatus: '',
        title: null,
        defaultPage: null,
        restrictions: [],
        homepage: null
    };
}
describe('ClonePageBuilderFactory', () => {
    let pageInfo: ICMSPage;
    let newPageInfo: ICMSPage;
    let pageBuilder: ClonePageBuilder;

    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let contextAwarePageStructureService: jasmine.SpyObj<ContextAwarePageStructureService>;
    let typeStructureRestService: jasmine.SpyObj<TypeStructureRestService>;
    let restrictionsStepHandlerFactory: jasmine.SpyObj<RestrictionsStepHandlerFactory>;
    let catalogService: jasmine.SpyObj<ICatalogService>;

    // eslint-disable-next-line @typescript-eslint/no-var-requires
    const PageStructureMocks = require('../../common/pageStructureMocks');

    const restrictionsStepHandler: IRestrictionsStepHandler = {
        hideStep() {
            //
        },
        showStep() {
            //
        },
        getStepId: jasmine.createSpy(),
        goToStep: jasmine.createSpy(),
        isStepValid: jasmine.createSpy()
    };

    const primaryPageDisplayConditionData = {
        ...mockPage(),
        isPrimary: true
    } as ICMSPage;

    const variationPageDisplayConditionData = {
        ...mockPage(),
        isPrimary: false,
        primaryPage: {
            label: 'newPrimaryLabel'
        }
    } as ICMSPage;

    const restrictionsData = [
        {
            uid: 'restrictionId1',
            uuid: 'restrictionUuid1',
            name: '',
            type: null,
            typeCode: '',
            description: '',
            language: null
        },
        {
            uid: 'restrictionId2',
            uuid: 'restrictionUuid2',
            name: '',
            type: null,
            typeCode: '',
            description: '',
            language: null
        }
    ] as unknown as RestrictionCMSItem[];

    beforeEach(() => {
        pageInfo = {
            ...mockPage(),
            pk: 'some pk',
            masterTemplate: 'PageTemplateUuid',
            masterTemplateId: 'PageTemplateUid',
            name: 'pageName',
            label: 'pageLabel',
            typeCode: 'pageTypeCode',
            uid: 'pageUid',
            uuid: 'somePageUUID',
            itemtype: 'pageType'
        } as ICMSPage;

        newPageInfo = {
            ...mockPage(),
            pk: 'some pk',
            masterTemplate: 'PageTemplateUuid',
            masterTemplateId: 'PageTemplateUid',
            name: 'pageName',
            label: 'pageLabel',
            typeCode: 'pageTypeCode',
            uid: 'pageUid',
            pageUuid: 'somePageUUID',
            itemtype: 'pageType',
            template: 'PageTemplateUid',
            type: 'somePageType',
            catalogVersion: 'someCatalogVersionUUID'
        } as ICMSPage;

        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve('somePageUUID'));

        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'getById'
        ]);

        contextAwarePageStructureService = jasmine.createSpyObj<ContextAwarePageStructureService>(
            'mockContextAwarePageStructureService',
            ['getPageStructureForNewPage']
        );
        contextAwarePageStructureService.getPageStructureForNewPage.and.returnValue(
            Promise.resolve(PageStructureMocks.getFields())
        );

        typeStructureRestService = jasmine.createSpyObj<TypeStructureRestService>(
            'mockTypeStructureRestService',
            ['getStructureByTypeAndMode']
        );
        typeStructureRestService.getStructureByTypeAndMode.and.returnValue(
            Promise.resolve<any>({
                type: 'somePageType'
            })
        );
        restrictionsStepHandlerFactory = jasmine.createSpyObj<RestrictionsStepHandlerFactory>(
            'restrictionsStepHandlerFactory',
            ['createRestrictionsStepHandler']
        );
        restrictionsStepHandlerFactory.createRestrictionsStepHandler.and.returnValue(
            restrictionsStepHandler as any
        );

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionUUid'
        ]);
        catalogService.getCatalogVersionUUid.and.returnValue(
            Promise.resolve('someCatalogVersionUUID')
        );
        cmsitemsRestService.getById.and.callFake((itemId: string): any => {
            if (itemId === 'somePageUUID') {
                return Promise.resolve(pageInfo);
            }
            return Promise.resolve({});
        });

        pageBuilder = new ClonePageBuilder(
            restrictionsStepHandler,
            '',
            {},
            contextAwarePageStructureService,
            typeStructureRestService,
            cmsitemsRestService,
            catalogService,
            pageInfoService
        );
    });

    beforeEach(async () => {
        await pageBuilder.init();
    });

    it('WHEN basePageUid is not passed THEN clonePageBuilder will call pageInfoService to fetch pageUid and then fetch page details', () => {
        expect(cmsitemsRestService.getById).toHaveBeenCalledWith('somePageUUID');
    });

    it('WHEN clonePageBuilder is called THEN basic page information is fetch and set to the page object', () => {
        const info = { ...newPageInfo };
        delete info.uuid;

        expect(pageBuilder.getPageInfo()).toEqual(info);
        expect(pageBuilder.getPageTypeCode()).toEqual('pageTypeCode');
        expect(pageBuilder.getPageTemplate()).toEqual('PageTemplateUid');
    });

    it('WHEN displayConditionSelected for primary is called THEN page structure is fetched based on the type code', async () => {
        spyOn(restrictionsStepHandler, 'hideStep');
        spyOn(restrictionsStepHandler, 'showStep');

        await pageBuilder.displayConditionSelected(primaryPageDisplayConditionData);

        expect(restrictionsStepHandler.hideStep).toHaveBeenCalled();
        expect(restrictionsStepHandler.showStep).not.toHaveBeenCalled();
        expect(contextAwarePageStructureService.getPageStructureForNewPage).toHaveBeenCalledWith(
            'pageTypeCode',
            true
        );
        expect(pageBuilder.getPageInfoStructure()).toEqual(PageStructureMocks.getFields());
    });

    it('WHEN displayConditionSelected for variation is called THEN clone page label is set to the selected primaryPage and the page structure is fetched based on the type code', async () => {
        spyOn(restrictionsStepHandler, 'hideStep');
        spyOn(restrictionsStepHandler, 'showStep');

        await pageBuilder.displayConditionSelected(variationPageDisplayConditionData);

        expect(pageBuilder.getPageInfo().label).toEqual('newPrimaryLabel');
        expect(restrictionsStepHandler.hideStep).not.toHaveBeenCalled();
        expect(restrictionsStepHandler.showStep).toHaveBeenCalled();
        expect(contextAwarePageStructureService.getPageStructureForNewPage).toHaveBeenCalledWith(
            'pageTypeCode',
            false
        );
        expect(pageBuilder.getPageInfoStructure()).toEqual(PageStructureMocks.getFields());
    });

    it('WHEN displayConditionSelected for primary/variation is called and if page has no typeCode THEN page structure is set to empty', () => {
        delete (pageBuilder as any).pageData.typeCode;

        pageBuilder.displayConditionSelected({ isPrimary: true } as unknown as ICMSPage);

        expect(contextAwarePageStructureService.getPageStructureForNewPage).not.toHaveBeenCalled();
        expect(pageBuilder.getPageInfoStructure()).toEqual({} as GenericEditorStructure);
    });

    it('WHEN componentCloneOptionSelected is called with "clone" option THEN getComponentCloneOption will return "clone" ', () => {
        pageBuilder.componentCloneOptionSelected('clone');

        expect(pageBuilder.getComponentCloneOption()).toEqual('clone');
    });

    it('WHEN componentCloneOptionSelected is called with "reference" option THEN getComponentCloneOption will return "reference" ', () => {
        pageBuilder.componentCloneOptionSelected('reference');

        expect(pageBuilder.getComponentCloneOption()).toEqual('reference');
    });

    it('WHEN restrictionsSelected is called with onlyOneRestrictionMustApply and list of restrictions THEN corresponding values are set to the page object ', () => {
        const restrictions = ['uuidOfRestrictionOne', 'uuidOfRestrictionTwo'] as string[];
        const info = {
            ...newPageInfo,
            restrictions,
            onlyOneRestrictionMustApply: true
        };
        delete info.uuid;

        pageBuilder.restrictionsSelected(true, restrictions);

        expect(pageBuilder.getPageRestrictions()).toEqual(restrictions);
        expect(pageBuilder.getPageInfo()).toEqual(info);
    });

    it('WHEN getPageProperties is called THEN it return basic page info such as type, typeCode, template and onlyOneRestrictionMustApply', () => {
        pageBuilder.restrictionsSelected(false, []);

        expect(pageBuilder.getPageProperties()).toEqual({
            type: 'somePageType',
            typeCode: 'pageTypeCode',
            template: 'PageTemplateUid',
            catalogVersion: 'someCatalogVersionUUID',
            onlyOneRestrictionMustApply: false
        });
    });

    it('WHEN targetCatalogVersionSelected is called with selected catalog version THEN catalogVersion value is set in the page object', () => {
        const catalogVersion = {
            uuid: 'someCatalogVersionUUID'
        } as ICatalogVersion;

        pageBuilder.onTargetCatalogVersionSelected(catalogVersion);

        expect(pageBuilder.getTargetCatalogVersion()).toEqual(catalogVersion);
        expect(pageBuilder.getPageInfo().catalogVersion).toEqual(catalogVersion.uuid);
    });
});
