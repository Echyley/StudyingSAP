/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils
} from 'personalizationcommons';
import { ShowComponentInfoListComponent } from 'personalizationsmartedit/contextMenu';
import {
    ActionsForContainerPage,
    PersonalizationsmarteditRestService
} from 'personalizationsmartedit/service';
import { PersonalizationsmarteditComponentHandlerService } from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';
import { PersonalizationsmarteditContextService } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import { PersonalizationsmarteditContextualMenuService } from 'personalizationsmartedit/service/PersonalizationsmarteditContextualMenuService';
import { IPermissionService, ContextualMenuItemData } from 'smarteditcommons';

describe('ShowComponentInfoListComponent', () => {
    let persoContextService: PersonalizationsmarteditContextService;
    let persoContextualMenuService: jasmine.SpyObj<PersonalizationsmarteditContextualMenuService>;
    let persoUtils: PersonalizationsmarteditUtils;
    let persoRestService: jasmine.SpyObj<PersonalizationsmarteditRestService>;
    let persoMessageHandler: jasmine.SpyObj<PersonalizationsmarteditMessageHandler>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let persoComponentHandlerService: jasmine.SpyObj<PersonalizationsmarteditComponentHandlerService>;
    let permissionService: jasmine.SpyObj<IPermissionService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const injectedData = {
        componentAttributes: {
            smarteditCatalogVersionUuid: '',
            smarteditComponentId: '',
            smarteditComponentType: '',
            smarteditComponentUuid: '',
            smarteditElementUuid: ''
        }
    } as ContextualMenuItemData;

    const CONTAINER_SOURCE_ID = '1234';

    const action1 = createActionForContainer({
        actionCatalog: '1234',
        actionCatalogVersion: 'Staged',
        variationCode: 'variation1'
    });
    const action2 = createActionForContainer({
        actionCatalog: '1234',
        actionCatalogVersion: 'Online',
        variationCode: 'variation2'
    });
    const mockActions: ActionsForContainerPage = {
        actions: [action1, action2],
        results: [],
        pagination: {
            count: 2,
            page: 0,
            totalCount: 2,
            totalPages: 1
        }
    };

    let component: ShowComponentInfoListComponent;

    beforeEach(() => {
        persoContextService = jasmine.createSpyObj('persoContextService', ['getSeData']);

        persoContextualMenuService = jasmine.createSpyObj(
            'personalizationsmarteditContextualMenuService',
            ['isPersonalizationAllowedInWorkflow', 'isContextualMenuInfoEnabled']
        );

        persoUtils = new PersonalizationsmarteditUtils(translateService, null, null);

        persoRestService = jasmine.createSpyObj<PersonalizationsmarteditRestService>(
            'personalizationsmarteditRestService',
            ['getCxCmsAllActionsForContainer']
        );

        persoMessageHandler = jasmine.createSpyObj<PersonalizationsmarteditMessageHandler>(
            'personalizationsmarteditMessageHandler',
            ['sendError']
        );

        translateService = jasmine.createSpyObj('translateService', ['instant']);

        persoComponentHandlerService = jasmine.createSpyObj(
            'personalizationsmarteditComponentHandlerService',
            ['getContainerSourceIdForContainerId']
        );

        permissionService = jasmine.createSpyObj('permissionService', ['isPermitted']);

        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);
    });

    beforeEach(() => {
        persoComponentHandlerService.getContainerSourceIdForContainerId.and.returnValue(
            CONTAINER_SOURCE_ID
        );

        persoRestService.getCxCmsAllActionsForContainer.and.returnValue(
            Promise.resolve(mockActions)
        );

        spyOn(persoUtils, 'getCatalogVersionNameByUuid').and.returnValue(
            Promise.resolve('Electronics Content Catalog (Online)')
        );
        spyOn(persoUtils, 'isItemFromCurrentCatalog').and.returnValue(true);

        permissionService.isPermitted.and.returnValue(Promise.resolve(true));

        persoContextualMenuService.isContextualMenuInfoEnabled.and.returnValue(false);
        persoContextualMenuService.isPersonalizationAllowedInWorkflow.and.returnValue(false);

        component = new ShowComponentInfoListComponent(
            injectedData,
            persoComponentHandlerService,
            persoContextService,
            persoContextualMenuService,
            persoMessageHandler,
            persoRestService,
            persoUtils,
            permissionService,
            translateService,
            cdr
        );
    });

    it('fetches the actions properly', async () => {
        component.containerId = 'someContainerId1234';
        await component.ngOnInit();

        await component.fetchMoreActions();

        expect(component.actions).toEqual([
            {
                ...action1,
                customization: {
                    catalog: action1.actionCatalog,
                    catalogVersion: action1.actionCatalogVersion,
                    catalogVersionNameL10N: 'Electronics Content Catalog (Online)',
                    isFromCurrentCatalog: true
                }
            },
            {
                ...action2,
                customization: {
                    catalog: action2.actionCatalog,
                    catalogVersion: action2.actionCatalogVersion,
                    catalogVersionNameL10N: 'Electronics Content Catalog (Online)',
                    isFromCurrentCatalog: true
                }
            }
        ]);
    });

    function createActionForContainer(catalog: {
        actionCatalog: string;
        actionCatalogVersion: string;
        variationCode: string;
    }) {
        const { actionCatalog, actionCatalogVersion, variationCode } = catalog;
        return {
            type: null,
            actionCatalog,
            actionCatalogVersion,
            actionCode: null,
            actionRank: null,
            customizationCode: null,
            customizationName: null,
            customizationRank: null,
            customizationStatus: null,
            variationCode,
            variationName: null,
            variationRank: null,
            variationStatus: null
        };
    }
});
