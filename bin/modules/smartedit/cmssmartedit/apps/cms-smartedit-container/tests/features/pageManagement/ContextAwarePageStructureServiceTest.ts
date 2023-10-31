/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TypeStructureRestService } from 'cmssmarteditcontainer/dao/TypeStructureRestService';
import { ContextAwarePageStructureService } from 'cmssmarteditcontainer/services/ContextAwarePageStructureService';
import { GenericEditorStructure, IPageService } from 'smarteditcommons';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const PageStructureMocks = require('../common/pageStructureMocks');

describe('contextAwarePageStructureService', () => {
    let service: ContextAwarePageStructureService;
    let typeStructureRestService: jasmine.SpyObj<TypeStructureRestService>;
    let pageService: jasmine.SpyObj<IPageService>;

    beforeEach(() => {
        typeStructureRestService = jasmine.createSpyObj<TypeStructureRestService>(
            'typeStructureRestService',
            ['getStructureByType']
        );
        pageService = jasmine.createSpyObj<IPageService>('pageService', ['isPagePrimary']);
        service = new ContextAwarePageStructureService(typeStructureRestService, pageService);
    });

    describe('getPageStructureForNewPage', () => {
        it('should return the re-ordered fields from the API, with the label disabled, for a variation page', async () => {
            // Arrange
            pageService.isPagePrimary.and.returnValue(Promise.resolve(false));
            typeStructureRestService.getStructureByType.and.returnValue(
                Promise.resolve(PageStructureMocks.getFields())
            );

            // Act
            const structure = await service.getPageStructureForNewPage('ContentPage', false);

            // Assert
            expect(structure).toEqual(
                getExpectedStructureForNewVariationPage() as GenericEditorStructure
            );
        });

        it('should return the re-ordered fields from the API, with the label enabled, for a primary page', async () => {
            // Arrange
            pageService.isPagePrimary.and.returnValue(Promise.resolve(true));
            typeStructureRestService.getStructureByType.and.returnValue(
                Promise.resolve(PageStructureMocks.getFields())
            );

            // Act
            const structure = await service.getPageStructureForNewPage('ContentPage', true);

            // Assert
            expect(structure).toEqual(
                getExpectedStructureForNewPrimaryPage() as GenericEditorStructure
            );
        });

        it('should return the re-ordered fields from the API, with the label disabled, for a primary page when user had read-only permission on label attribute', async () => {
            // Arrange
            pageService.isPagePrimary.and.returnValue(Promise.resolve(true));
            typeStructureRestService.getStructureByType.and.returnValue(
                Promise.resolve(PageStructureMocks.getFieldsWithReadOnly())
            );

            // Act
            const structure = await service.getPageStructureForNewPage('ContentPage', true);

            // Assert
            expect(structure).toEqual(
                getExpectedStructureForNewPrimaryPageWithLabelReadOnlyPermission() as GenericEditorStructure
            );
        });
    });

    describe('getPageStructureForPageEditing', () => {
        it('should return the re-ordered fields from the API, stripping the creation time and modified time fields, and setting label editable to false, for a variation page', async () => {
            // Arrange
            pageService.isPagePrimary.and.returnValue(Promise.resolve(false));
            typeStructureRestService.getStructureByType.and.returnValue(
                Promise.resolve(PageStructureMocks.getFields())
            );

            // Act
            const structure = await service.getPageStructureForPageEditing(
                'ContentPage',
                'dummyId'
            );

            // Assert
            expect(structure).toEqual(
                getExpectedStructureForEditingVariationPage() as GenericEditorStructure
            );
        });

        it('should return the re-ordered fields from the API, stripping the creation time and modified time fields, and setting label editable to true, for a primary page', async () => {
            // Arrange
            pageService.isPagePrimary.and.returnValue(Promise.resolve(true));
            typeStructureRestService.getStructureByType.and.returnValue(
                Promise.resolve(PageStructureMocks.getFields())
            );

            // Act
            const structure = await service.getPageStructureForPageEditing(
                'ContentPage',
                'dummyId'
            );

            // Assert
            expect(structure).toEqual(
                getExpectedStructureForEditingPrimaryPage() as GenericEditorStructure
            );
        });

        it('should return the re-ordered fields from the API, stripping the creation time and modified time fields, and setting label editable to false, for a primary page when user had read-only permission on label attribute', async () => {
            // Arrange
            pageService.isPagePrimary.and.returnValue(Promise.resolve(true));
            typeStructureRestService.getStructureByType.and.returnValue(
                Promise.resolve(PageStructureMocks.getFieldsWithReadOnly())
            );

            // Act
            const structure = await service.getPageStructureForPageEditing(
                'ContentPage',
                'dummyId'
            );

            // Assert
            expect(structure).toEqual(
                getExpectedStructureForEditingPrimaryPageWithLabelReadOnlyPermission() as GenericEditorStructure
            );
        });
    });

    describe('getPageStructureForViewing', () => {
        it('should return the re-ordered fields from the API, disabling all fields for editing, for a page', async () => {
            // Arrange
            pageService.isPagePrimary.and.returnValue(Promise.resolve(true));
            typeStructureRestService.getStructureByType.and.returnValue(
                Promise.resolve(PageStructureMocks.getFields())
            );

            // Act
            const structure = await service.getPageStructureForViewing('ContentPage');

            // Assert
            expect(structure).toEqual(getExpectedStructureForViewing() as GenericEditorStructure);
        });
    });

    function getExpectedStructureForNewVariationPage() {
        return {
            attributes: [
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'se.cms.pageinfo.page.type',
                    qualifier: 'typeCode',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.name.name',
                    localized: false,
                    qualifier: 'name'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.contentpage.label.name',
                    localized: false,
                    qualifier: 'label',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.uid.name',
                    localized: false,
                    qualifier: 'uid'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.title.name',
                    localized: true,
                    qualifier: 'title'
                }
            ],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForNewPrimaryPage() {
        return {
            attributes: [
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'se.cms.pageinfo.page.type',
                    qualifier: 'typeCode',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.name.name',
                    localized: false,
                    qualifier: 'name'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.contentpage.label.name',
                    localized: false,
                    qualifier: 'label',
                    editable: true
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.uid.name',
                    localized: false,
                    qualifier: 'uid'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.title.name',
                    localized: true,
                    qualifier: 'title'
                }
            ],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForNewPrimaryPageWithLabelReadOnlyPermission() {
        return {
            attributes: [
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'se.cms.pageinfo.page.type',
                    qualifier: 'typeCode',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.name.name',
                    localized: false,
                    qualifier: 'name'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.contentpage.label.name',
                    localized: false,
                    qualifier: 'label',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.uid.name',
                    localized: false,
                    qualifier: 'uid'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.title.name',
                    localized: true,
                    qualifier: 'title'
                }
            ],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForEditingVariationPage() {
        return {
            attributes: [
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'se.cms.pageinfo.page.type',
                    qualifier: 'typeCode',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.name.name',
                    localized: false,
                    qualifier: 'name'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.contentpage.label.name',
                    localized: false,
                    qualifier: 'label',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.uid.name',
                    localized: false,
                    qualifier: 'uid',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.title.name',
                    localized: true,
                    qualifier: 'title'
                },
                {
                    cmsStructureType: 'DisplayConditionEditor',
                    i18nKey: 'type.abstractpage.displayCondition.name',
                    qualifier: 'displayCondition',
                    editable: false
                },
                {
                    cmsStructureType: 'DateTime',
                    i18nKey: 'type.abstractpage.creationtime.name',
                    localized: false,
                    qualifier: 'creationtime',
                    editable: false
                },
                {
                    cmsStructureType: 'DateTime',
                    i18nKey: 'type.abstractpage.modifiedtime.name',
                    localized: false,
                    qualifier: 'modifiedtime',
                    editable: false
                }
            ],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForEditingPrimaryPage() {
        return {
            attributes: [
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'se.cms.pageinfo.page.type',
                    qualifier: 'typeCode',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.name.name',
                    localized: false,
                    qualifier: 'name'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.contentpage.label.name',
                    localized: false,
                    qualifier: 'label',
                    editable: true
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.uid.name',
                    localized: false,
                    qualifier: 'uid',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.title.name',
                    localized: true,
                    qualifier: 'title'
                },
                {
                    cmsStructureType: 'DisplayConditionEditor',
                    i18nKey: 'type.abstractpage.displayCondition.name',
                    qualifier: 'displayCondition',
                    editable: false
                },
                {
                    cmsStructureType: 'DateTime',
                    i18nKey: 'type.abstractpage.creationtime.name',
                    localized: false,
                    qualifier: 'creationtime',
                    editable: false
                },
                {
                    cmsStructureType: 'DateTime',
                    i18nKey: 'type.abstractpage.modifiedtime.name',
                    localized: false,
                    qualifier: 'modifiedtime',
                    editable: false
                }
            ],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForEditingPrimaryPageWithLabelReadOnlyPermission() {
        return {
            attributes: [
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'se.cms.pageinfo.page.type',
                    qualifier: 'typeCode',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.name.name',
                    localized: false,
                    qualifier: 'name'
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.contentpage.label.name',
                    localized: false,
                    qualifier: 'label',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.uid.name',
                    localized: false,
                    qualifier: 'uid',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.title.name',
                    localized: true,
                    qualifier: 'title'
                },
                {
                    cmsStructureType: 'DisplayConditionEditor',
                    i18nKey: 'type.abstractpage.displayCondition.name',
                    qualifier: 'displayCondition',
                    editable: false
                },
                {
                    cmsStructureType: 'DateTime',
                    i18nKey: 'type.abstractpage.creationtime.name',
                    localized: false,
                    qualifier: 'creationtime',
                    editable: false
                },
                {
                    cmsStructureType: 'DateTime',
                    i18nKey: 'type.abstractpage.modifiedtime.name',
                    localized: false,
                    qualifier: 'modifiedtime',
                    editable: false
                }
            ],
            category: 'PAGE'
        };
    }

    function getExpectedStructureForViewing() {
        return {
            attributes: [
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.name.name',
                    localized: false,
                    qualifier: 'name',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.contentpage.label.name',
                    localized: false,
                    qualifier: 'label',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.uid.name',
                    localized: false,
                    qualifier: 'uid',
                    editable: false
                },
                {
                    cmsStructureType: 'ShortString',
                    i18nKey: 'type.abstractpage.title.name',
                    localized: true,
                    qualifier: 'title',
                    editable: false
                },
                {
                    cmsStructureType: 'DateTime',
                    i18nKey: 'type.abstractpage.creationtime.name',
                    localized: false,
                    qualifier: 'creationtime',
                    editable: false
                },
                {
                    cmsStructureType: 'DateTime',
                    i18nKey: 'type.abstractpage.modifiedtime.name',
                    localized: false,
                    qualifier: 'modifiedtime',
                    editable: false
                }
            ],
            category: 'PAGE'
        };
    }
});
