/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';
import {
    GenericEditorField,
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    LINKED_DROPDOWN,
    SystemEventService
} from 'smarteditcommons';
import { CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID } from '../../../constants';
import { CmsLinkToSelectOption, LinkToOption, SelectedOption, SelectOption } from './types';

const SelectOptions = [
    {
        id: LinkToOption.content,
        structureApiMode: 'CONTENT'
    },
    {
        id: LinkToOption.product,
        structureApiMode: 'PRODUCT',
        hasCatalog: true
    },
    {
        id: LinkToOption.category,
        structureApiMode: 'CATEGORY',
        hasCatalog: true
    },
    {
        id: LinkToOption.external,
        structureApiMode: 'EXTERNAL'
    }
];

/**
 * Component wrapper on top of Generic Editor Dropdown that upon selection of an option, will trigger an event with a new Structure API or structure.
 *
 * Contains the following option values:
 * - `content` (link to a content page)
 * - `product` (link to a product page)
 * - `category` (link to a specific category page)
 * - `external` (external link)
 */
@Component({
    selector: 'se-cms-link-to-select',
    templateUrl: './CmsLinkToSelectComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CmsLinkToSelectComponent implements OnInit, OnDestroy {
    public id: string;
    public field: GenericEditorField;
    public optionModel: CmsLinkToSelectOption;
    public qualifier: string;

    private unRegSelectValueChanged: () => void;

    constructor(
        private cdr: ChangeDetectorRef,
        private systemEventService: SystemEventService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        data: GenericEditorWidgetData<CmsLinkToSelectOption>
    ) {
        ({
            id: this.id,
            field: this.field,
            model: this.optionModel,
            qualifier: this.qualifier
        } = data);
    }

    ngOnInit(): void {
        const linkTo = this.getLinkToValue(this.optionModel);
        if (linkTo !== null) {
            this.optionModel.linkTo = linkTo;
        }

        const onSelectValueChangedEventName = `${this.id}${LINKED_DROPDOWN}`;
        this.unRegSelectValueChanged = this.systemEventService.subscribe(
            onSelectValueChangedEventName,
            (_eventId, data) => this.onLinkToSelectValueChanged(data)
        );
    }

    ngOnDestroy(): void {
        this.unRegSelectValueChanged();
    }

    /**
     * Called when select value changes in the following order:
     * - select
     * - sub-select
     * - select (reinitialize)
     */
    private onLinkToSelectValueChanged({ optionObject: option, qualifier }: SelectedOption): void {
        // only handle change for the select (not dependant select)
        if (this.qualifier !== qualifier) {
            return;
        }

        if (!option) {
            return;
        }

        const optionValue = option.id;
        const selectedOption = SelectOptions.find(({ id }) => id === optionValue);
        if (!selectedOption) {
            throw new Error('Selected option is not supported');
        }

        // Prevent cycling infinitely because the Generic Editor append the component each time the structure change
        if (optionValue === this.optionModel.currentSelectedOptionValue) {
            return;
        }
        this.optionModel.currentSelectedOptionValue = optionValue;
        this.optionModel.external = optionValue !== LinkToOption.external;

        this.clearModel(selectedOption);

        this.systemEventService.publishAsync(CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID, {
            content: this.optionModel,
            structureApiMode: selectedOption.structureApiMode,
            editorId: this.id
        });

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private getLinkToValue(model: CmsLinkToSelectOption): LinkToOption | null {
        if (model.product) {
            return LinkToOption.product;
        } else if (model.contentPage) {
            return LinkToOption.content;
        } else if (model.category) {
            return LinkToOption.category;
        } else if (model.url || (model.linkToggle && model.linkToggle.urlLink) || model.urlLink) {
            return LinkToOption.external;
        }
        return null;
    }

    /** Removes value from dependant "Link To" select (input in case of external). */
    private clearModel({ id, hasCatalog }: SelectOption): void {
        if (id !== LinkToOption.category) {
            delete this.optionModel.category;
        }

        if (id !== LinkToOption.product) {
            delete this.optionModel.product;
        }

        if (!hasCatalog) {
            delete this.optionModel.productCatalog;
        }

        if (id !== LinkToOption.content) {
            delete this.optionModel.contentPage;
        }

        if (id !== LinkToOption.external) {
            delete this.optionModel.url;
            delete this.optionModel.urlLink;
            if (this.optionModel.linkToggle && this.optionModel.linkToggle.urlLink) {
                delete this.optionModel.linkToggle.urlLink;
            }
        }
    }
}
