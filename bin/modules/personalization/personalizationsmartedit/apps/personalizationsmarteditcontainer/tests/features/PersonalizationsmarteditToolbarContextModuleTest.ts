/*
* Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
*/

import { CombinedViewToolbarContextComponent } from 'personalizationsmarteditcontainer/toolbarContext/CombinedViewToolbarContextComponent';
import { CustomizeToolbarContextComponent } from 'personalizationsmarteditcontainer/toolbarContext/CustomizeToolbarContextComponent';
import { ManageCustomizationViewMenuComponent } from 'personalizationsmarteditcontainer/toolbarContext/ManageCustomizationViewMenuComponent';
import { ToolbarItemInternal } from 'smarteditcommons';

describe('PersonalizationsmarteditToolbarContextComponent', () => {
    let personalizationsmarteditCombinedViewCommonsService: jasmine.SpyObj<any>;
    let personalizationsmarteditContextService: jasmine.SpyObj<any>;
    let personalizationsmarteditContextUtils: jasmine.SpyObj<any>;
    let personalizationsmarteditManager: jasmine.SpyObj<any>;
    let personalizationsmarteditManagerView: jasmine.SpyObj<any>;
    let crossFrameEventService: jasmine.SpyObj<any>;
    let personalizationsmarteditPreviewService: jasmine.SpyObj<any>;

    // Components being tested
    let customizeToolbarContextComponent: CustomizeToolbarContextComponent;
    let combinedViewToolbarContextComponent: CombinedViewToolbarContextComponent;
    let manageCustomizationViewMenuComponent: ManageCustomizationViewMenuComponent;

    // === SETUP ===
    beforeEach(() => {
        personalizationsmarteditCombinedViewCommonsService = jasmine.createSpyObj(
            'personalizationsmarteditCombinedViewCommonsService',
            ['updatePreview']
        );
        personalizationsmarteditContextService = jasmine.createSpyObj(
            'personalizationsmarteditContextService',
            ['getCustomize', 'getCombinedView']
        );
        personalizationsmarteditContextUtils = jasmine.createSpyObj(
            'personalizationsmarteditContextUtils',
            [
                'clearCustomizeContextAndReloadPreview',
                'clearCombinedViewCustomizeContext',
                'clearCombinedViewContextAndReloadPreview'
            ]
        );
        personalizationsmarteditManager = jasmine.createSpyObj('personalizationsmarteditManager', [
            'openCreateCustomizationModal'
        ]);
        personalizationsmarteditManagerView = jasmine.createSpyObj(
            'personalizationsmarteditManagerView',
            ['openManagerAction']
        );
        personalizationsmarteditPreviewService = jasmine.createSpyObj(
            'personalizationsmarteditPreviewService',
            ['updatePreviewTicketWithVariations', 'removePersonalizationDataFromPreview']
        );
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);

        customizeToolbarContextComponent = new CustomizeToolbarContextComponent(
            personalizationsmarteditContextService,
            personalizationsmarteditContextUtils,
            personalizationsmarteditPreviewService,
            crossFrameEventService
        );
        combinedViewToolbarContextComponent = new CombinedViewToolbarContextComponent(
            personalizationsmarteditCombinedViewCommonsService,
            personalizationsmarteditContextService,
            personalizationsmarteditContextUtils,
            crossFrameEventService
        );
        manageCustomizationViewMenuComponent = new ManageCustomizationViewMenuComponent(
            personalizationsmarteditContextService,
            personalizationsmarteditContextUtils,
            personalizationsmarteditPreviewService,
            personalizationsmarteditManager,
            personalizationsmarteditManagerView,
            {
                isOpen: false
            } as ToolbarItemInternal
        );
    });

    it('Public API', () => {
        expect(customizeToolbarContextComponent.ngOnInit).toBeDefined();
        expect(customizeToolbarContextComponent.ngDoCheck).toBeDefined();
        expect(customizeToolbarContextComponent.clear).toBeDefined();

        expect(combinedViewToolbarContextComponent.ngOnInit).toBeDefined();
        expect(combinedViewToolbarContextComponent.ngDoCheck).toBeDefined();
        expect(combinedViewToolbarContextComponent.clear).toBeDefined();

        expect(manageCustomizationViewMenuComponent.createCustomizationClick).toBeDefined();
        expect(manageCustomizationViewMenuComponent.managerViewClick).toBeDefined();
    });

    it('manageCustomizationViewMenuComponent.createCustomizationClick triggers proper modal', () => {
        manageCustomizationViewMenuComponent.createCustomizationClick();
        expect(personalizationsmarteditManager.openCreateCustomizationModal).toHaveBeenCalled();
    });

    it('manageCustomizationViewMenuComponent.managerViewClick triggers proper modal', () => {
        manageCustomizationViewMenuComponent.managerViewClick();
        expect(personalizationsmarteditManagerView.openManagerAction).toHaveBeenCalled();
    });
});
