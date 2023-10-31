/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SimpleChange, ChangeDetectorRef } from '@angular/core';
import { IContextAwareEditableItemService } from 'cmscommons';
import { RestrictionsEditorComponent } from 'cmssmarteditcontainer/components/restrictions/components';
import { RestrictionPickerConfigService } from 'cmssmarteditcontainer/components/restrictions/services';
import { RestrictionCMSItem } from 'cmssmarteditcontainer/components/restrictions/types';
import {
    IPageRestrictionCriteria,
    PageRestrictionsCriteriaService
} from 'cmssmarteditcontainer/services/pageRestrictions/PageRestrictionsCriteriaService';
import { LogService, SystemEventService, CmsitemsRestService, CMSItem } from 'smarteditcommons';

describe('RestrictionsEditorComponent', () => {
    let component: RestrictionsEditorComponent;
    let logService: jasmine.SpyObj<LogService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let restrictionPickerConfigService: jasmine.SpyObj<RestrictionPickerConfigService>;
    let pageRestrictionsCriteriaService: jasmine.SpyObj<PageRestrictionsCriteriaService>;
    let contextAwareEditableItemService: jasmine.SpyObj<IContextAwareEditableItemService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    let indexedRestrictions;
    let targetRestrictions;
    const mockItem = {
        catalogVersion: 'staged',
        name: 'item',
        typeCode: 'itemcode',
        uid: 'itemUid',
        uuid: 'itemUuid',
        uriContext: { context: 'page' }
    } as CMSItem;
    const mockRestrictions = [
        {
            uuid: 'uuid1',
            uid: 'uid1',
            catalogVersion: 'staged',
            name: 'restriction1',
            typeCode: 'CMSRestriction'
        },
        {
            uuid: 'uuid2',
            uid: 'uid2',
            catalogVersion: 'staged',
            name: 'restriction2',
            typeCode: 'CMSRestriction'
        },
        {
            uuid: 'uuid3',
            uid: 'uid3',
            catalogVersion: 'staged',
            name: 'restriction3',
            typeCode: 'CMSRestriction'
        }
    ] as RestrictionCMSItem[];
    const mockRestrictionUuids = ['uuid1', 'uuid2', 'uuid3'] as string[];
    const mockCriteriaOptions = [
        {
            editLabel: 'editlabel1',
            id: '1',
            label: 'label1',
            value: false
        },
        {
            editLabel: 'editlabel2',
            id: '2',
            label: 'label2',
            value: true
        }
    ] as IPageRestrictionCriteria[];

    const data = {
        response: mockRestrictions
    } as any;

    beforeEach(() => {
        logService = jasmine.createSpyObj<LogService>('logService', ['warn']);
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync',
            'subscribe'
        ]);
        restrictionPickerConfigService = jasmine.createSpyObj<RestrictionPickerConfigService>(
            'restrictionPickerConfigService',
            ['getConfigForSelecting', 'getConfigForEditing']
        );
        pageRestrictionsCriteriaService = jasmine.createSpyObj<PageRestrictionsCriteriaService>(
            'pageRestrictionsCriteriaService',
            ['getRestrictionCriteriaOptions']
        );
        contextAwareEditableItemService = jasmine.createSpyObj<IContextAwareEditableItemService>(
            'contextAwareEditableItemService',
            ['isItemEditable']
        );
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'getByIdsNoCache'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        cmsitemsRestService.getByIdsNoCache.and.returnValue(Promise.resolve(data));
        pageRestrictionsCriteriaService.getRestrictionCriteriaOptions.and.returnValue(
            mockCriteriaOptions
        );
        contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(true));
        (restrictionPickerConfigService as any).MODE_EDITING = 'editing';

        indexedRestrictions = mockRestrictions.map((r, i) => ({
            ...r,
            restrictionIndex: i
        }));
        targetRestrictions = indexedRestrictions.map((r) => ({
            ...r,
            canBeEdited: true
        }));

        component = new RestrictionsEditorComponent(
            logService,
            systemEventService,
            restrictionPickerConfigService,
            pageRestrictionsCriteriaService,
            contextAwareEditableItemService,
            cmsitemsRestService,
            cdr
        );
        component.restrictionUuids = mockRestrictionUuids;
    });

    describe('initialize', () => {
        beforeEach(() => {
            spyOn(component.isDirtyFnChange, 'emit');
            spyOn(component.resetFnChange, 'emit');
            spyOn(component.cancelFnChange, 'emit');
            spyOn(component.onRestrictionsChange, 'emit');

            component.item = {
                ...mockItem,
                onlyOneRestrictionMustApply: true
            };
        });

        it('should set up initial values', async () => {
            await component.ngOnInit();

            expect(component.criteriaFetchStrategy).toEqual({
                fetchAll: jasmine.any(Function)
            });
            expect(component.sliderPanelConfiguration).toEqual({
                modal: {
                    showDismissButton: true,
                    title: '',
                    cancel: {
                        label: 'se.cms.restriction.management.panel.button.cancel',
                        onClick: jasmine.any(Function),
                        isDisabledFn: jasmine.any(Function)
                    },
                    save: {
                        label: '',
                        onClick: jasmine.any(Function),
                        isDisabledFn: jasmine.any(Function)
                    }
                },
                cssSelector: '#y-modal-dialog'
            });
            expect(component.restrictionManagement).toEqual({
                uriContext: { context: 'page' },
                submitFn: null,
                isDirtyFn: null,
                config: null
            });
        });

        it('should emit functions', async () => {
            await component.ngOnInit();

            expect(component.isDirtyFnChange.emit).toHaveBeenCalledWith(jasmine.any(Function));
            expect(component.resetFnChange.emit).toHaveBeenCalledWith(jasmine.any(Function));
            expect(component.cancelFnChange.emit).toHaveBeenCalledWith(jasmine.any(Function));
            expect(component.onRestrictionsChange.emit).toHaveBeenCalledWith({
                onlyOneRestrictionMustApply: true,
                restrictionUuids: ['uuid1', 'uuid2', 'uuid3'],
                alwaysEnableSubmit: false
            });
        });

        it('should set criteria options', async () => {
            await component.ngOnInit();

            expect(component.criteriaOptions).toEqual(mockCriteriaOptions);
            expect(component.criteria).toEqual(mockCriteriaOptions[1]);
            expect(component.originalCriteria).toEqual(mockCriteriaOptions[1]);
        });

        it('WHEN onlyOneRestrictionMustApply is set to false it should set first criteria option', async () => {
            component.item = {
                ...mockItem,
                onlyOneRestrictionMustApply: false
            };
            await component.ngOnInit();

            expect(component.criteriaOptions).toEqual(mockCriteriaOptions);
            expect(component.criteria).toEqual(mockCriteriaOptions[0]);
            expect(component.originalCriteria).toEqual(mockCriteriaOptions[0]);
        });

        it('should setup results', async () => {
            await component.ngOnInit();

            expect(component.restrictions).toEqual(targetRestrictions as RestrictionCMSItem[]);
            expect(component.originalRestrictions).toEqual(
                indexedRestrictions as RestrictionCMSItem[]
            );
            expect(component.onRestrictionsChange.emit).toHaveBeenCalledWith({
                onlyOneRestrictionMustApply: true,
                restrictionUuids: ['uuid1', 'uuid2', 'uuid3'],
                alwaysEnableSubmit: false
            });
            expect(component.isRestrictionsReady).toEqual(true);
        });

        it('should initialize even unsubscribers', async () => {
            await component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'UnrelatedValidationMessagesEvent',
                jasmine.any(Function)
            );
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'genericEditorLoadedEvent',
                jasmine.any(Function)
            );
        });
    });

    describe('onDestroy', () => {
        it('should unsubscribe events', async () => {
            const unsubscribe = jasmine.createSpy();
            systemEventService.subscribe.and.returnValue(unsubscribe);
            component.item = mockItem;

            await component.ngOnInit();

            component.ngOnDestroy();

            expect(unsubscribe).toHaveBeenCalledTimes(2);
        });
    });

    describe('onChanges', () => {
        describe('Restriction array dirtiness', () => {
            it('GIVEN it is a first change THEN it should mark restrictions array as not dirty', async () => {
                const changes = new SimpleChange([], [], true);
                component.originalRestrictions = [];
                component.restrictions = [];

                await component.ngOnChanges({ restrictionUuids: changes });

                expect((component as any).restrictionsArrayIsDirty).toEqual(false);
            });

            it('GIVEN it is NOT a first change WHEN original and current restrictions are the same THEN it should mark restrictions array as not dirty', async () => {
                const changes = new SimpleChange(mockRestrictionUuids, mockRestrictionUuids, false);
                component.originalRestrictions = mockRestrictions;
                component.restrictions = mockRestrictions;

                await component.ngOnChanges({ restrictionUuids: changes });

                expect((component as any).restrictionsArrayIsDirty).toEqual(false);
            });

            it('GIVEN it is NOT a first change WHEN original and current restrictions are different THEN it should mark restrictions array as dirty', async () => {
                const changes = new SimpleChange(
                    mockRestrictionUuids,
                    mockRestrictionUuids.slice(1, 3),
                    false
                );
                component.originalRestrictions = mockRestrictions;
                component.restrictions = mockRestrictions.slice(1, 3);

                await component.ngOnChanges({ restrictionUuids: changes });

                expect((component as any).restrictionsArrayIsDirty).toEqual(true);
            });
        });

        describe('Restrictions update', () => {
            it('WHEN previous restrictions are equal current restrictions THEN it should NOT update editability and reindex', async () => {
                spyOn(component as any, 'indexRestrictions');
                spyOn(component as any, 'applyIsRestrictionEditable');
                const changes = new SimpleChange(mockRestrictionUuids, mockRestrictionUuids, false);

                await component.ngOnChanges({ restrictionUuids: changes });

                expect((component as any).indexRestrictions).not.toHaveBeenCalled();
                expect((component as any).applyIsRestrictionEditable).toHaveBeenCalled();
            });

            it('WHEN previous restrictions are equal current restrictions THEN it should NOT update editability and reindex', async () => {
                const changes = new SimpleChange(
                    mockRestrictionUuids,
                    mockRestrictionUuids.slice(0, 2),
                    false
                );
                component.restrictions = mockRestrictions.slice(0, 2);

                await component.ngOnChanges({ restrictionUuids: changes });

                expect(component.restrictions).toEqual(targetRestrictions.slice(0, 2));
            });
        });
    });

    describe('onAddRestriction', () => {
        it('should update slider config, set restriction management config and call method to show slider panel', async () => {
            const mockConfig = {
                mode: 'select',
                getRestrictionTypesFn: jasmine.createSpy(),
                getSupportedRestrictionTypesFn: jasmine.createSpy(),
                existingRestrictions: mockRestrictions
            };
            const showPanelSpy = jasmine.createSpy();

            component.item = mockItem;
            component.sliderPanelShow = showPanelSpy;
            await component.ngOnInit();
            restrictionPickerConfigService.getConfigForSelecting.and.returnValue(mockConfig);

            component.onAddRestriction();

            expect(component.sliderPanelConfiguration.modal.title).toEqual(
                'se.cms.restriction.management.panel.title.add'
            );
            expect(component.sliderPanelConfiguration.modal.save.label).toEqual(
                'se.cms.restriction.management.panel.button.add'
            );
            expect(component.sliderPanelConfiguration.modal.save.isDisabledFn).toEqual(
                jasmine.any(Function)
            );
            expect(component.sliderPanelConfiguration.modal.save.onClick).toEqual(
                jasmine.any(Function)
            );
            expect(component.restrictionManagement.config).toEqual(mockConfig);
            expect(showPanelSpy).toHaveBeenCalled();
        });
    });

    describe('onEditRestriction', () => {
        it('should update slider config, set restriction management config and call method to show slider panel', async () => {
            const mockConfig = {
                mode: 'edit',
                getSupportedRestrictionTypesFn: jasmine.createSpy(),
                restriction: mockRestrictions[0]
            };
            const showPanelSpy = jasmine.createSpy();
            spyOn(component.onRestrictionsChange, 'emit');
            component.item = mockItem;
            component.getSupportedRestrictionTypes = jasmine.createSpy();
            component.restrictions = mockRestrictions;
            component.sliderPanelShow = showPanelSpy;
            await component.ngOnInit();
            restrictionPickerConfigService.getConfigForEditing.and.returnValue(mockConfig);

            component.onEditRestriction(mockRestrictions[0]);

            expect(component.sliderPanelConfiguration.modal.title).toEqual(
                'se.cms.restriction.management.panel.title.edit'
            );
            expect(component.sliderPanelConfiguration.modal.save.label).toEqual(
                'se.cms.restriction.management.panel.button.save'
            );
            expect(component.sliderPanelConfiguration.modal.save.isDisabledFn).toEqual(
                jasmine.any(Function)
            );
            expect(component.sliderPanelConfiguration.modal.save.onClick).toEqual(
                jasmine.any(Function)
            );
            expect(component.restrictionManagement.config).toEqual(mockConfig);
            expect(showPanelSpy).toHaveBeenCalled();
            expect(restrictionPickerConfigService.getConfigForEditing).toHaveBeenCalledWith(
                mockRestrictions[0],
                jasmine.any(Function)
            );
            expect(component.onRestrictionsChange.emit).toHaveBeenCalledWith({
                onlyOneRestrictionMustApply: false,
                restrictionUuids: ['uuid1', 'uuid2', 'uuid3'],
                alwaysEnableSubmit: false
            });
        });
    });

    describe('onRemoveRestriction', () => {
        it('should remove restriction from given index and update restrictions', async () => {
            spyOn(component.onRestrictionsChange, 'emit');
            component.item = mockItem;
            component.restrictions = mockRestrictions;

            await component.ngOnInit();

            component.onRemoveRestriction(2);
            expect(component.restrictions).toEqual(targetRestrictions.slice(0, 2));
            expect(component.onRestrictionsChange.emit).toHaveBeenCalledWith({
                onlyOneRestrictionMustApply: false,
                restrictionUuids: ['uuid1', 'uuid2'],
                alwaysEnableSubmit: false
            });
        });
    });

    describe('removeAllRestrictions', () => {
        it('should remove all restrictions and emit that changes', async () => {
            spyOn(component.onRestrictionsChange, 'emit');
            component.item = mockItem;
            component.criteriaOptions = mockCriteriaOptions;
            component.restrictions = mockRestrictions;
            component.removeValidationMessages = jasmine.createSpy();
            await component.ngOnInit();

            component.removeAllRestrictions();

            expect(component.restrictions).toEqual([]);
            expect(component.removeValidationMessages).toHaveBeenCalledWith();
            expect(component.onRestrictionsChange.emit).toHaveBeenCalledWith({
                onlyOneRestrictionMustApply: false,
                restrictionUuids: [],
                alwaysEnableSubmit: false
            });
        });
    });

    describe('showRemoveAllButton', () => {
        it('should show remove all button when there are restrictions and component is in edit mode', async () => {
            component.item = mockItem;
            component.restrictions = mockRestrictions;
            component.criteriaOptions = mockCriteriaOptions;
            component.editable = true;
            await component.ngOnInit();

            const actual = component.showRemoveAllButton();

            expect(actual).toEqual(true);
        });
    });

    describe('Errors handling', () => {
        let componentAny: any;

        beforeEach(() => {
            componentAny = component as any;
        });

        it('GIVEN error has subject including "restrictions" THEN it should return true', () => {
            const error = {
                subject: 'invalid.restrictions'
            };

            const actual = componentAny.isRestrictionRelatedError(error);

            expect(actual).toEqual(true);
        });

        it('should make sure that error position is integer and parse error subject', () => {
            const error = {
                subject: 'invalid.restrictions',
                position: '1'
            };

            const actual = componentAny.formatRestrictionRelatedError(error);

            expect(actual).toEqual({
                position: 1,
                subject: 'restrictions'
            });
        });

        it('GIVEN validation data includes various errors THEN it should filter and format only restriction errors and assign them to component instance', () => {
            const errors = [
                {
                    position: '0',
                    subject: 'invalid.restrictions'
                },
                {
                    position: '1',
                    subject: 'invalid.restrictions'
                },
                {
                    position: '2',
                    subject: 'invalid.form'
                }
            ];

            componentAny.handleUnrelatedValidationErrors({ messages: errors });

            expect(componentAny.errors).toEqual([
                {
                    position: 0,
                    subject: 'restrictions'
                },
                {
                    position: 1,
                    subject: 'restrictions'
                }
            ]);
        });

        it('should propagate errors to open editor', async () => {
            const genericEditorId = 'se-item-management-editor';
            spyOn(componentAny, 'clearEvents');
            spyOn(componentAny, 'initEvents');
            componentAny.restrictionManagement = {
                config: {
                    mode: 'editing',
                    restriction: {
                        ...mockRestrictions[0],
                        restrictionIndex: 0
                    }
                }
            };
            componentAny.errors = [
                { position: 0, subject: 'restrictions' },
                { position: 1, subject: 'restrictions' }
            ];

            await componentAny.propagateErrors(genericEditorId);

            expect(componentAny.clearEvents).toHaveBeenCalled();
            expect(componentAny.initEvents).toHaveBeenCalled();
            expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                'UnrelatedValidationMessagesEvent',
                {
                    messages: [{ position: 0, subject: 'restrictions' }],
                    targetGenericEditorId: genericEditorId
                }
            );
        });
    });
});
