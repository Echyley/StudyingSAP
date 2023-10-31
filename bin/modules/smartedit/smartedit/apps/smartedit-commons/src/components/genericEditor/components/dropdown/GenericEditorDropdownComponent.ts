/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    Type,
    ViewChild,
    OnChanges
} from '@angular/core';
import { TypedMap } from '@smart/utils';

import { CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION } from '../../../../utils';
import { ActionableSearchItem, SelectComponent, SelectReset } from '../../../select';
import { GenericEditorField } from '../../types';
import { IGenericEditorDropdownService, IGenericEditorDropdownServiceConstructor } from './types';

/**
 * Initializes dropdown. Encapsulates common logic that is called by Angular ngOnInit hooks.
 *
 * @internal
 * @ignore
 */
export function genericEditorDropdownComponentOnInit(): void {
    this.field.params = this.field.params || {};
    this.field.params.catalogId = this.field.params.catalogId || CONTEXT_CATALOG;
    this.field.params.catalogVersion = this.field.params.catalogVersion || CONTEXT_CATALOG_VERSION;

    const dropdownServiceConstructor = this.GenericEditorDropdownService || this.SEDropdownService;
    this.dropdown = new dropdownServiceConstructor({
        field: this.field,
        qualifier: this.qualifier,
        model: this.model,
        id: this.id,
        onClickOtherDropdown: this.onClickOtherDropdown.bind(this),
        getApi: this.getApi
    });

    this.dropdown.init();
}

/**
 * Represents a custom dropdown (standalone or dependent on another one) for the {@link GenericEditorFactoryService}.
 *
 * {@link EditorFieldMappingService} maps this component by default to the "EditableDropdown" cmsStructureType.
 *
 * The dropdown will be configured and populated based on the field structure retrieved from the Structure API
 *
 * The following is an example of the 4 possible field structures that can be returned by the Structure API for the component to work.
 *
 * Note: Angular populator must be registered in your module with {@link CustomDropdownPopulatorsToken}.
 *
 * ### Example of the 4 possible field structures that can be returned by the Structure API for the component to work
 *
 *      [
 *          ...
 *          {
 *              cmsStructureType: "EditableDropdown",
 *              qualifier: "someQualifier1",
 *              i18nKey: 'i18nkeyForsomeQualifier1',
 *              idAttribute: "id",
 *              labelAttributes: ["label"],
 *              paged: false,
 *              options: [{
 *                  id: '1',
 *                  label: 'option1'
 *              }, {
 *                  id: '2',
 *                  label: 'option2'
 *              }, {
 *                  id: '3',
 *                  label: 'option3'
 *              }],
 *          }, {
 *              cmsStructureType: "EditableDropdown",
 *              qualifier: "someQualifier2",
 *              i18nKey: 'i18nkeyForsomeQualifier2',
 *              idAttribute: "id",
 *              labelAttributes: ["label"],
 *              paged: false,
 *              uri: '/someuri',
 *              dependsOn: 'someQualifier1'
 *          }, {
 *              cmsStructureType: "EditableDropdown",
 *              qualifier: "someQualifier2",
 *              i18nKey: 'i18nkeyForsomeQualifier2',
 *              idAttribute: "id",
 *              labelAttributes: ["label"],
 *              paged: false,
 *          }, {
 *              cmsStructureType: "EditableDropdown",
 *              qualifier: "someQualifier3",
 *              i18nKey: 'i18nkeyForsomeQualifier3',
 *              idAttribute: "id",
 *              labelAttributes: ["label"],
 *              paged: false,
 *              propertyType: 'somePropertyType',
 *          }
 *          ...
 *      ]
 *
 * If `uri`, `options` and `propertyType` are not set, then the component will look for an implementation of {@link DropdownPopulatorInterface} with the following recipe name:
 *
 * - ```smarteditComponentType + qualifier + "DropdownPopulator"```
 *
 * - default to: ```smarteditComponentType + "DropdownPopulator"```
 *
 * If no custom populator can be found, an exception will be raised.
 *
 * For the above example, since someQualifier2 will depend on someQualifier1, then if someQualifier1 is changed, then the list of options
 * for someQualifier2 is populated by calling the [fetchAll]{@link UriDropdownPopulator#fetchAll}.
 */
@Component({
    selector: 'se-generic-editor-dropdown',
    host: {
        '[class.se-generic-editor-dropdown]': 'true'
    },
    templateUrl: './GenericEditorDropdownComponent.html'
})
export class GenericEditorDropdownComponent implements OnInit, OnChanges {
    /**
     * Used by `GenericEditorDropdownService` to emit events prefixed with this id.
     */
    @Input() id: string;

    @Input() field: GenericEditorField;

    /**
     * If the field is not localized, this is the actual field.qualifier, if it is localized, it is the language identifier such as en, de...
     */
    @Input() qualifier: string;

    /**
     * If the field is not localized, this is the actual full parent model object, if it is localized, it is the language map: model[field.qualifier].
     */
    @Input() model: TypedMap<any>;

    /**
     * See [showRemoveButton]{@link SelectComponent#showRemoveButton}.
     */
    @Input() showRemoveButton?: boolean;

    /**
     * See [itemComponent]{@link SelectComponent#itemComponent}.
     */
    @Input() itemComponent?: Type<any>;

    /**
     * See [resultsHeaderComponent]{@link SelectComponent#resultsHeaderComponent}.
     */
    @Input() resultsHeaderComponent?: Type<any>;

    @Input() actionableSearchItem?: ActionableSearchItem;

    @Input() reset: SelectReset;
    @Output() resetChange = new EventEmitter<SelectReset>();

    @ViewChild(SelectComponent, { static: false }) public selectComponent: SelectComponent<any>;

    public dropdown: IGenericEditorDropdownService;

    constructor(public GenericEditorDropdownService: IGenericEditorDropdownServiceConstructor) {}

    ngOnChanges(): void {
        genericEditorDropdownComponentOnInit.call(this);
    }

    ngOnInit(): void {
        genericEditorDropdownComponentOnInit.call(this);
    }

    public onClickOtherDropdown(): void {
        this.selectComponent.closeAndReset();
    }

    public onResetChange(reset: SelectReset): void {
        this.resetChange.emit(reset);
    }
}
