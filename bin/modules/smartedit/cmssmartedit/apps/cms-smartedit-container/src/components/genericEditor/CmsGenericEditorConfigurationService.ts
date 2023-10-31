/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    BooleanComponent,
    EditorFieldMappingService,
    GenericEditorMediaType,
    GenericEditorField,
    GenericEditorStructure,
    GenericEditorTabService
} from 'smarteditcommons';
import { DisplayConditionsEditorComponent } from '../../components/pages/displayConditions/displayConditionsEditor/DisplayConditionsEditorComponent';
import {
    ComponentRestrictionsEditorComponent,
    PageRestrictionsEditorComponent,
    CmsComponentDropdownComponent
} from '../cmsComponents';

import { NavigationNodeSelectorComponent } from '../navigation';
import { MultiCategorySelectorComponent, MultiProductSelectorComponent } from './catalog';
import { CmsLinkToSelectComponent } from './cmsLinkToSelect';
import { InfoPageNameComponent } from './infoPageName';
import { LinkToggleComponent } from './linkToggle';
import { MediaComponent, MediaContainerComponent } from './media';
import {
    MissingPrimaryContentPageComponent,
    DuplicatePrimaryNonContentPageComponent,
    DuplicatePrimaryContentPageLabelComponent
} from './pageRestore';
import { PageTypeEditorComponent } from './pageType';
import { RestrictionsListComponent } from './restrictionsList';
import { SingleActiveCatalogAwareItemSelectorComponent } from './singleActiveCatalogAwareSelector';
import { SlotSharedCloneActionFieldComponent } from './slotSharedCloneActionField';
import { SlotSharedSlotTypeFieldComponent } from './slotSharedSlotTypeField';
import { ThumbnailSelectOption, ThumbnailSelectComponent, MuteBooleanComponent } from './video';
import { WorkflowCreateVersionFieldComponent } from './workflowCreateVersionField';

@Injectable()
export class CmsGenericEditorConfigurationService {
    private readonly DEFAULT_PAGE_TAB_ID = 'information';
    private readonly CATEGORIES = {
        PAGE: 'PAGE',
        COMPONENT: 'COMPONENT'
    };
    constructor(
        private editorFieldMappingService: EditorFieldMappingService,
        private genericEditorTabService: GenericEditorTabService
    ) {}

    public setDefaultEditorFieldMappings(): void {
        this.editorFieldMappingService.addFieldMapping('Media', null, null, {
            component: MediaComponent
        });

        this.editorFieldMappingService.addFieldMapping('MediaContainer', null, null, {
            component: MediaContainerComponent
        });

        this.editorFieldMappingService.addFieldMapping('NavigationNodeSelector', null, null, {
            component: NavigationNodeSelectorComponent
        });

        this.editorFieldMappingService.addFieldMapping('MultiProductSelector', null, null, {
            component: MultiProductSelectorComponent
        });

        this.editorFieldMappingService.addFieldMapping('MultiCategorySelector', null, null, {
            component: MultiCategorySelectorComponent
        });

        this.editorFieldMappingService.addFieldMapping('CMSLinkToSelect', null, null, {
            component: CmsLinkToSelectComponent
        });

        this.editorFieldMappingService.addFieldMapping('OptionalCMSLinkToSelect', null, null, {
            component: CmsLinkToSelectComponent,
            tooltip: 'se.cms.linkto.tooltip'
        });

        this.editorFieldMappingService.addFieldMapping('SingleOnlineProductSelector', null, null, {
            component: SingleActiveCatalogAwareItemSelectorComponent
        });

        this.editorFieldMappingService.addFieldMapping('SingleOnlineCategorySelector', null, null, {
            component: SingleActiveCatalogAwareItemSelectorComponent
        });

        this.editorFieldMappingService.addFieldMapping('CMSItemDropdown', null, null, {
            component: CmsComponentDropdownComponent
        });

        this.editorFieldMappingService.addFieldMapping(
            'CMSComponentRestrictionsEditor',
            null,
            null,
            {
                component: ComponentRestrictionsEditorComponent
            }
        );

        this.editorFieldMappingService.addFieldMapping(
            'PageRestrictionsEditor',
            null,
            'restrictions',
            {
                component: PageRestrictionsEditorComponent
            }
        );

        // for editing modal only, not used for create/clone
        this.editorFieldMappingService.addFieldMapping(
            'DisplayConditionEditor',
            null,
            'displayCondition',
            {
                component: DisplayConditionsEditorComponent,
                hidePrefixLabel: true
            }
        );

        this.editorFieldMappingService.addFieldMapping(
            'ShortString',
            this.isPagePredicate,
            'typeCode',
            {
                component: PageTypeEditorComponent,
                hidePrefixLabel: true
            }
        );

        this.editorFieldMappingService.addFieldMapping('InfoPageName', this.isPagePredicate, null, {
            component: InfoPageNameComponent
        });

        this.editorFieldMappingService.addFieldMapping('Boolean', null, 'visible', {
            component: BooleanComponent,
            i18nKey: 'type.component.abstractcmscomponent.visible.name'
        });

        this.editorFieldMappingService.addFieldMapping('LinkToggle', null, null, {
            component: LinkToggleComponent
        });

        this.editorFieldMappingService.addFieldMapping('RestrictionsList', null, null, {
            component: RestrictionsListComponent,
            hidePrefixLabel: true
        });

        this.editorFieldMappingService.addFieldMapping('Video', null, null, {
            component: MediaComponent,
            allowMediaType: GenericEditorMediaType.VIDEO,
            tooltip: 'se.cms.video.tooltip'
        });

        this.editorFieldMappingService.addFieldMapping('VideoMute', null, null, {
            component: MuteBooleanComponent,
            hideFieldWidget: true,
            dependsOnField: 'autoPlay',
            dependsOnValue: 'true'
        });

        this.editorFieldMappingService.addFieldMapping('VideoThumbnailSelector', null, null, {
            component: ThumbnailSelectComponent,
            hideFieldWidget: false,
            dependsOnField: 'autoPlay',
            dependsOnValue: 'false'
        });

        this.editorFieldMappingService.addFieldMapping('Thumbnail', null, null, {
            component: MediaContainerComponent,
            hideFieldWidget: true,
            hidePrefixLabel: true,
            dependsOnField: 'thumbnailSelector',
            dependsOnValue: ThumbnailSelectOption.uploadThumbnail,
            allowMediaType: GenericEditorMediaType.IMAGE
        });

        this.editorFieldMappingService.addFieldMapping('PDFFile', null, null, {
            component: MediaComponent,
            allowMediaType: GenericEditorMediaType.PDF_DOCUMENT
        });

        // Page restore widgets.
        this.editorFieldMappingService.addFieldMapping(
            'DuplicatePrimaryNonContentPageMessage',
            null,
            null,
            {
                component: DuplicatePrimaryNonContentPageComponent,
                hidePrefixLabel: true
            }
        );

        this.editorFieldMappingService.addFieldMapping('DuplicatePrimaryContentPage', null, null, {
            component: DuplicatePrimaryContentPageLabelComponent,
            hidePrefixLabel: false
        });

        this.editorFieldMappingService.addFieldMapping('MissingPrimaryContentPage', null, null, {
            component: MissingPrimaryContentPageComponent,
            hidePrefixLabel: false
        });

        this.editorFieldMappingService.addFieldMapping('WorkflowCreateVersionField', null, null, {
            component: WorkflowCreateVersionFieldComponent,
            hidePrefixLabel: false
        });

        this.editorFieldMappingService.addFieldMapping('SlotSharedCloneActionField', null, null, {
            component: SlotSharedCloneActionFieldComponent,
            hidePrefixLabel: false
        });

        this.editorFieldMappingService.addFieldMapping('SlotSharedSlotTypeField', null, null, {
            component: SlotSharedSlotTypeFieldComponent,
            hidePrefixLabel: false
        });
    }

    public setDefaultTabsConfiguration(): void {
        this.genericEditorTabService.configureTab('default', {
            priority: 5
        });
        this.genericEditorTabService.configureTab('information', {
            priority: 5
        });
        this.genericEditorTabService.configureTab('administration', {
            priority: 4
        });
    }

    setDefaultTabFieldMappings(): void {
        // Set default tab.
        this.genericEditorTabService.addComponentTypeDefaultTabPredicate(this.defaultTabPredicate);

        // Set tabs
        this.editorFieldMappingService.addFieldTabMapping(
            null,
            this.isComponentPredicate,
            'visible',
            'visibility'
        );
        this.editorFieldMappingService.addFieldTabMapping(
            null,
            this.isComponentPredicate,
            'restrictions',
            'visibility'
        );
        this.editorFieldMappingService.addFieldTabMapping(
            null,
            this.isComponentPredicate,
            'onlyOneRestrictionMustApply',
            'visibility'
        );
        this.editorFieldMappingService.addFieldTabMapping(
            null,
            this.isComponentPredicate,
            'uid',
            'basicinfo'
        );
        this.editorFieldMappingService.addFieldTabMapping(
            null,
            this.isComponentPredicate,
            'id',
            'basicinfo'
        );
        this.editorFieldMappingService.addFieldTabMapping(
            null,
            this.isComponentPredicate,
            'modifiedtime',
            'basicinfo'
        );
        this.editorFieldMappingService.addFieldTabMapping(
            'DateTime',
            this.isComponentPredicate,
            'creationtime',
            'basicinfo'
        );

        // Page Tabs
        this.editorFieldMappingService.addFieldTabMapping(
            'DisplayConditionEditor',
            this.isPagePredicate,
            'displayCondition',
            'displaycondition'
        );
        this.editorFieldMappingService.addFieldTabMapping(
            null,
            this.isPagePredicate,
            'restrictions',
            'restrictions'
        );
    }

    private defaultTabPredicate = (componentTypeStructure: GenericEditorStructure): string =>
        componentTypeStructure.category === this.CATEGORIES.PAGE ? this.DEFAULT_PAGE_TAB_ID : null;

    private isPagePredicate = (
        componentType: string,
        field: GenericEditorField,
        componentTypeStructure: GenericEditorStructure
    ): boolean => componentTypeStructure.category === this.CATEGORIES.PAGE;

    private isComponentPredicate = (
        componentType: string,
        field: GenericEditorField,
        componentTypeStructure: GenericEditorStructure
    ): boolean => componentTypeStructure.category === this.CATEGORIES.COMPONENT;
}
