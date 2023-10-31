/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FormModule as FundamentalFormModule } from '@fundamental-ngx/core/form';
import {
    CustomDropdownPopulatorsToken,
    TranslationModule,
    SelectModule,
    L10nPipeModule,
    GenericEditorDropdownModule
} from 'smarteditcommons';
import { PageComponentsModule } from '../pages/PageComponentsModule';
import { CatalogModule } from './catalog/CatalogModule';
import { CmsGenericEditorConfigurationService } from './CmsGenericEditorConfigurationService';
import { CmsLinkToSelectComponent } from './cmsLinkToSelect';
import {
    CategoryDropdownPopulator,
    CmsLinkComponentContentPageDropdownPopulator,
    ProductCatalogDropdownPopulator,
    ProductDropdownPopulator
} from './dropdownPopulators';
import { InfoPageNameComponent } from './infoPageName';
import { LinkToggleComponent } from './linkToggle';

import {
    MissingPrimaryContentPageComponent,
    DuplicatePrimaryNonContentPageComponent,
    DuplicatePrimaryContentPageLabelComponent
} from './pageRestore';
import { PageTypeEditorComponent } from './pageType';
import { RestrictionsListComponent } from './restrictionsList';
import {
    SingeActiveCatalogAwareItemSelectorItemRendererComponent,
    SingleActiveCatalogAwareItemSelectorComponent
} from './singleActiveCatalogAwareSelector';
import { SlotSharedCloneActionFieldComponent } from './slotSharedCloneActionField';
import { SlotSharedSlotTypeFieldComponent } from './slotSharedSlotTypeField';
import { ThumbnailSelectComponent, MuteBooleanComponent } from './video';
import { WorkflowCreateVersionFieldComponent } from './workflowCreateVersionField';

@NgModule({
    imports: [
        TranslationModule.forChild(),
        FormsModule,
        FundamentalFormModule,
        CommonModule,
        L10nPipeModule,
        SelectModule,
        GenericEditorDropdownModule,
        CatalogModule,
        PageComponentsModule
    ],
    providers: [
        CmsGenericEditorConfigurationService,
        {
            provide: CustomDropdownPopulatorsToken,
            useClass: ProductDropdownPopulator,
            multi: true
        },
        {
            provide: CustomDropdownPopulatorsToken,
            useClass: ProductCatalogDropdownPopulator,
            multi: true
        },
        {
            provide: CustomDropdownPopulatorsToken,
            useClass: CategoryDropdownPopulator,
            multi: true
        },
        {
            provide: CustomDropdownPopulatorsToken,
            useClass: CmsLinkComponentContentPageDropdownPopulator,
            multi: true
        }
    ],
    declarations: [
        WorkflowCreateVersionFieldComponent,
        MissingPrimaryContentPageComponent,
        DuplicatePrimaryNonContentPageComponent,
        DuplicatePrimaryContentPageLabelComponent,
        RestrictionsListComponent,
        SlotSharedCloneActionFieldComponent,
        SlotSharedSlotTypeFieldComponent,
        CmsLinkToSelectComponent,
        PageTypeEditorComponent,
        SingleActiveCatalogAwareItemSelectorComponent,
        SingeActiveCatalogAwareItemSelectorItemRendererComponent,
        LinkToggleComponent,
        InfoPageNameComponent,
        ThumbnailSelectComponent,
        MuteBooleanComponent
    ],
    entryComponents: [
        WorkflowCreateVersionFieldComponent,
        MissingPrimaryContentPageComponent,
        DuplicatePrimaryNonContentPageComponent,
        DuplicatePrimaryContentPageLabelComponent,
        RestrictionsListComponent,
        SlotSharedCloneActionFieldComponent,
        SlotSharedSlotTypeFieldComponent,
        CmsLinkToSelectComponent,
        PageTypeEditorComponent,
        SingleActiveCatalogAwareItemSelectorComponent,
        SingeActiveCatalogAwareItemSelectorItemRendererComponent,
        LinkToggleComponent,
        InfoPageNameComponent,
        ThumbnailSelectComponent,
        MuteBooleanComponent
    ],
    exports: [WorkflowCreateVersionFieldComponent, CatalogModule]
})
export class GenericEditorWidgetsModule {}
