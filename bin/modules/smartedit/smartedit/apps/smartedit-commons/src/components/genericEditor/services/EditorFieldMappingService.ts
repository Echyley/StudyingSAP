/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { LogService, Payload } from '@smart/utils';
import { remove } from 'lodash';

import {
    GenericEditorField,
    GenericEditorMapping,
    GenericEditorMappingConfiguration,
    GenericEditorStructure
} from '../types';
import { BooleanComponent } from '../widgets/BooleanComponent';
import { DateTimePickerComponent } from '../widgets/DateTimePickerComponent';
import { DropdownComponent } from '../widgets/DropdownComponent/DropdownComponent';
import { EditableDropdownComponent } from '../widgets/EditableDropdownComponent';
import { EmailComponent } from '../widgets/EmailComponent';
import { EnumComponent } from '../widgets/EnumComponent/EnumComponent';
import { FloatComponent } from '../widgets/FloatComponent';
import { LongStringComponent } from '../widgets/LongStringComponent/LongStringComponent';
import { NumberComponent } from '../widgets/NumberComponent';
import { RichTextFieldComponent } from '../widgets/RichTextField';
import { ShortStringComponent } from '../widgets/ShortString/ShortStringComponent';
import { TextComponent } from '../widgets/TextComponent/TextComponent';

/**
 * FLOAT PRECISION
 */
/* @internal  */
export const DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION = '0.01';

/**
 * The purpose of the property editor template is to assign a value to model[qualifier].
 * In order to do this, all templates receive the following entities in their scope:
 */
/** @internal */
export interface PropertyEditorTemplate {
    /** The field description of the field being edited as defined by the structure API described in `IGenericEditor` */
    field: GenericEditorField;
    /** If the field is not localized, this is the actual field.qualifier, if it is localized, it is the language identifier such as en, de... */
    qualifier: string;
    /** If the field is not localized, this is the actual full parent model object, if it is localized, it is the language map: model[field.qualifier]. */
    model: any;
}

/**
 * The editorFieldMappingService contains the strategies that the `GenericEditorComponent` component
 * uses to control the rendering of a field. When the genericEditor directive is about to display a field, it queries the
 * editorFieldMappingService service to retrieve the right strategies to use. Internally, this service makes this selection based on three
 * matchers:
 * <ul>
 * 	<li><b>structureTypeNameMatcher</b>:	The matcher for the cmsStructureType of a field.</li>
 * 	<li><b>componentTypeNameMatcher</b>:	The matcher for the smarteditComponentType of the CMS component containing the field.</li>
 * <li><b>discriminatorMatcher</b>:			The matcher for the qualifier used by the genericEditor to identify the field.</li>
 * </ul>
 * These three matchers are used together to provide finer-grained control on the selection of strategies. A matcher itself is either a
 * string or a function that will be used to determine whether a value is considered a match or not. The following list describes
 * how a matcher behaves depending on its type:
 * <ul>
 *  <li><b>String</b>:                      Matches only the parameters that equal the provided string. </li>
 *  <li><b>Null</b>:                        Matches any parameter provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
 *  <li><b>Function</b>:                    Matches only the parameters for which the provided callback returns true. This option allows more
 *                                          control over the parameters to match.
 * </li>
 * </ul>
 * <br/>
 *
 * Currently, there are two types of strategies that the genericEditor uses to control the way a field is rendered:
 * <ul>
 *  <li><b>editor field mapping</b>:        This strategy is used to select and customize which property editor is to be used for this field.</li>
 *  <li><b>tab field mapping</b>:           This strategy is used to select the tab in the genericEditor where a field will be positioned.</li>
 * </ul>
 *
 */
@Injectable()
export class EditorFieldMappingService {
    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    private static readonly WILDCARD = '*';
    private static readonly MATCH = {
        NONE: 0,
        PARTIAL: 1,
        EXACT: 4 // An exact match is always better than a partial.
    };

    // --------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------
    public _editorsFieldMapping: GenericEditorMapping[] = [];
    public _fieldsTabsMapping: GenericEditorMapping[] = [];

    private _initialized: boolean;

    constructor(private logService: LogService) {}

    // --------------------------------------------------------------------------------------
    // Public API
    // --------------------------------------------------------------------------------------
    /**
     * This method overrides the default strategy of the `GenericEditorComponent` directive.
     * used to choose the property editor for a given field. Internally, this selection is based on three matchers:
     * <ul>
     * 	<li><b>structureTypeNameMatcher</b>:	The matcher for the cmsStructureType of a field.</li>
     * 	<li><b>componentTypeNameMatcher</b>:	The matcher for the smarteditComponentType of the CMS component containing the field.</li>
     * <li><b>discriminatorMatcher</b>:			The matcher for the qualifier used by the genericEditor to identify the field.</li>
     * </ul>
     * Only the fields that match all three matchers will be overriden.
     *
     * The following example shows how some sample fields would match some mappings:
     * <pre>
     * const field1 = {
     * 		cmsStructureType: 'ShortString',
     * 		smarteditComponentType: 'CmsParagraphComponent',
     * 		qualifier: 'name'
     * };
     *
     *  const field2 = {
     *      cmsStructureType: 'Boolean',
     *      smarteditComponentType: 'CmsParagraphComponent',
     *      qualifier: 'visible'
     *  };
     * </pre>
     *
     * <pre>
     * // This mapping won't match any of the fields. They don't match all three matchers.
     * editorFieldMappingService.addFieldMapping(
     *  'ShortString', 'CmsParagraphComponent', 'visible', configuration);
     *
     * // This mapping will match field2. It matches all three matchers perfectly.
     * editorFieldMappingService.addFieldMapping(
     *  'Boolean', 'CmsParagraphComponent', 'visible', configuration);
     *
     * // This mapping will match both fields. They match all three matchers.
     * // Note that both null and '*' represent a wildcard that accepts any value.
     * editorFieldMappingService.addFieldMapping(
     *  null, 'CmsParagraphComponent', '*', configuration);
     * </pre>
     *
     * <b>Note:</b> <br/>
     * The genericEditor has some predefined editors for the following cmsStructureTypes:
     * <ul>
     * 		<li><b>ShortString</b>:			Displays a text input field.</li>
     * 		<li><b>LongString</b>:  		Displays a text area.</li>
     * 		<li><b>RichText</b>:    		Displays an HTML/rich text editor.</li>
     * 		<li><b>Boolean</b>:     		Displays a check box.</li>
     * 		<li><b>DateTime</b>:        	Displays an input field with a date-time picker.</li>
     * 		<li><b>Media</b>:       		Displays a filterable dropdown list of media</li>
     * 		<li><b>Enum</b>:		 		Displays a filterable dropdown list of the enum class values identified by cmsStructureEnumType property.
     * 		<li><b>EditableDropdown</b>: 	Displays a configurable dropdown list that is enabled by `GenericEditorDropdownComponent`.
     * </ul>
     * <br />
     * You can program the `GenericEditorComponent` to use other property editors for these
     * cmsStructureTypes. You can also add custom cmsStructureTypes.
     * All default and custom property editors are HTML templates. These templates must adhere to the PropertyEditorTemplate {@link PropertyEditorTemplate contract}.
     *
     * @param structureTypeName The matcher used to identify the cmsStructureTypes for which a custom property editor is
     * required.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the cmsStructureTypes whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any cmsStructureType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the cmsStructureTypes for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>cmsStructureType:</b>           The cmsStructureType of the field.</li>
     *  <li><b>field:</b>                      The field to evaluate.</li>
     *  <li><b>componentTypeStructure:</b>     The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * @param componentTypeName The matcher used to identify the smarteditComponentType for which a custom property editor is
     * required.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the smarteditComponentType whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any smarteditComponentType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the smarteditComponentType for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>componentTypeName:</b>         The smarteditComponentType name of the field. </li>
     *  <li><b>field:</b>                     The field to evaluate. </li>
     *  <li><b>componentTypeStructure:</b>    The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * @param discriminator The matcher used to identify the discriminator for which a custom property editor is
     * required.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the discriminators whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any discriminator provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the discriminators for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>discriminator</b>:               The discriminator of the field to evaluate. </li>
     *  <li><b>field:</b>                       The field to evaluate. </li>
     *  <li><b>componentTypeStructure:</b>      The smarteditComponentType of the component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * @param configuration The holder that contains the override instructions. Cannot be null.
     * @param configuration.template The path to the HTML template used in the override. Cannot be null.
     * @param  configuration.customSanitize Custom sanitize function for a custom property editor. It's provided with a payload.
     */
    addFieldMapping(
        structureTypeName: string | ((...args: any[]) => boolean),
        componentTypeName: string | ((...args: any[]) => boolean),
        discriminator: string | ((...args: any[]) => boolean),
        configuration: GenericEditorMappingConfiguration
    ): void {
        this._addMapping(
            structureTypeName,
            componentTypeName,
            discriminator,
            configuration,
            this._editorsFieldMapping
        );
    }

    /**
     * This method is used by the genericEditor to retrieve the property editor to be rendered in a generic editor, along with its configuration.
     * If more than one property editor could be applied to the provided field, the one with the most accurate match will be used.
     *
     * Note:
     * Currently, all templates in SmartEdit use the short form. Before returning a response, this method ensures that
     * the template provided to the generic editor is in short form. For example:
     * - A template 'genericEditor/templates/shortStringTemplate.html' will be transformed to 'shortStringTemplate.html'
     *
     * @param field The object that represents the field that the property editor is retrieved for.
     * @param field.cmsStructureType The cmsStructureType that the property editor is retrieved for.
     * @param field.smarteditComponentType The smarteditComponentType that the property editor is retrieved for.
     * @param field.qualifier The field name of the smarteditComponentType that the property editor is retrieved for.
     * @param componentTypeStructure The smarteditComponentType structure of the componenent that contains the field that the property editor
     * is retrieved for.
     * @returns The configuration of the property editor to be used for this field. Can be null if no adequate match is found.
     *
     */
    getEditorFieldMapping(
        field: GenericEditorField,
        componentTypeStructure: GenericEditorStructure
    ): any {
        let fieldMapping = this._getMapping(
            field,
            componentTypeStructure,
            this._editorsFieldMapping
        );
        if (!fieldMapping) {
            this.logService.warn(
                'editorFieldMappingService - Cannot find suitable field mapping for type ',
                field.cmsStructureType
            );
            fieldMapping = null;
        } else if (fieldMapping && fieldMapping.template) {
            fieldMapping.template = this._cleanTemplate(fieldMapping.template);
        }

        return fieldMapping;
    }

    /**
     * This method overrides the default strategy of the `GenericEditorComponent` directive
     * used to choose the tab where to render a field in the generic editor. Internally, this selection is based on three elements:
     * <ul>
     * 	<li><b>structureTypeName</b>:			The cmsStructureType of a field.</li>
     * 	<li><b>componentTypeName</b>:			The smarteditComponentType of the component containing the field.</li>
     * <li><b>discriminator</b>:			    The qualifier used by the genericEditor to identify the field.</li>
     * </ul>
     * Only the fields that match all three elements will be overriden.
     *
     * The following example shows how sample fields would match some mappings:
     * <pre>
     *  const field1 = {
     *      cmsStructureType: 'ShortString',
     *      smarteditComponentType: 'CmsParagraphComponent',
     *      qualifier: 'name'
     *  };
     *
     *  const field2 = {
     *      cmsStructureType: 'Boolean',
     *      smarteditComponentType: 'CmsParagraphComponent',
     *      qualifier: 'visible'
     *  };
     * </pre>
     *
     * <pre>
     * // This mapping won't match any of the fields. They don't match all three matchers.
     * editorFieldMappingService.addFieldTabMapping(
     *  'ShortString', 'CmsParagraphComponent', 'visible', tabId);
     *
     * // This mapping will match field2. It matches all three matchers perfectly.
     * editorFieldMappingService.addFieldTabMapping(
     *  'Boolean', 'CmsParagraphComponent', 'visible', tabId);
     *
     * // This mapping will match both fields. They match all three matchers.
     * // Note that both null and '*' represent a wildcard that accepts any value.
     * editorFieldMappingService.addFieldTabMapping(
     *  null, 'CmsParagraphComponent', '*', tabId);
     * </pre>
     *
     * @param structureTypeNameMatcher The matcher used to identify the cmsStructureTypes for which to find its tab.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the cmsStructureTypes whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any cmsStructureType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the cmsStructureTypes for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>cmsStructureType:</b>           The cmsStructureType of the field.</li>
     *  <li><b>field:</b>                      The field to evaluate.</li>
     *  <li><b>componentTypeStructure:</b>     The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * @param {String | Function} componentTypeName The matcher used to identify the smarteditComponentType for which for which to find its tab.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the smarteditComponentType whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any smarteditComponentType provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the smarteditComponentType for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>componentTypeName:</b>         The smarteditComponentType name of the field. </li>
     *  <li><b>field:</b>                     The field to evaluate. </li>
     *  <li><b>componentTypeStructure:</b>    The smarteditComponentType structure of the CMS component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * @param discriminator The matcher used to identify the discriminator for which for which to find its tab.
     * There are three possible values for this parameter:
     *  <li><b>String</b>:                      Matches only the discriminators whose name equals this string. </li>
     *  <li><b>Null</b>:                        Matches any discriminator provided. It can be thought of as a wildcard. You can also use an asterisk (*).</li>
     *  <li><b>Function</b>:                    Matches only the discriminators for which the provided callback returns true. This option allows more
     *                                          control over the types to match.
     *
     * This function will be called with three parameters:
     * <ul>
     *  <li><b>discriminator</b>:               The discriminator of the field to evaluate. </li>
     *  <li><b>field:</b>                       The field to evaluate. </li>
     *  <li><b>componentTypeStructure:</b>      The smarteditComponentType of the component that contains the field to evaluate.</li>
     * </ul>
     * </li>
     * </ul>
     * @param tabId The ID of the tab where the field must be rendered in the generic editor.
     */
    addFieldTabMapping(
        structureTypeName: string | ((...args: any[]) => boolean),
        componentTypeName: string | ((...args: any[]) => boolean),
        discriminator: string | ((...args: any[]) => boolean),
        tabId: string
    ): void {
        this._addMapping(
            structureTypeName,
            componentTypeName,
            discriminator,
            tabId,
            this._fieldsTabsMapping
        );
    }

    /**
     * This method is used by the genericEditor to retrieve the tab where the field will be rendered in the generic editor.
     * If more than one tab matches the field provided, then the tab with the most accurate match will be used.
     *
     * @param field The object that represents the field that the tab is retrieved for.
     * @param field.cmsStructureType The cmsStructureType that the tab is retrieved for.
     * @param field.smarteditComponentType The smarteditComponentType that the tab is retrieved for.
     * @param field.qualifier The field name of the smarteditComponentType that the tab is retrieved for.
     * @param componentTypeStructure The smarteditComponentType structure of the component that contains the field that the tab is retrieved for.
     * @returns The ID of the tab where this field must reside. Can be null if no adequate match is found.
     *
     */
    getFieldTabMapping(
        field: GenericEditorField,
        componentTypeStructure: GenericEditorStructure
    ): string {
        return this._getMapping(field, componentTypeStructure, this._fieldsTabsMapping);
    }

    // --------------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------------
    _addMapping(
        structureTypeMatcher: string | ((...args: any[]) => boolean),
        componentTypeMatcher: string | ((...args: any[]) => boolean),
        discriminatorMatcher: string | ((...args: any[]) => boolean),
        mappedValue: any,
        collection: GenericEditorMapping[]
    ): void {
        structureTypeMatcher = this._validateField(structureTypeMatcher);
        componentTypeMatcher = this._validateField(componentTypeMatcher);
        discriminatorMatcher = this._validateField(discriminatorMatcher);

        const newMapping = {
            structureTypeMatcher,
            componentTypeMatcher,
            discriminatorMatcher,
            value: mappedValue
        };

        remove(
            collection,
            (element: GenericEditorMapping) =>
                element.structureTypeMatcher === structureTypeMatcher &&
                element.componentTypeMatcher === componentTypeMatcher &&
                element.discriminatorMatcher === discriminatorMatcher
        );

        collection.push(newMapping);
    }

    _validateField(
        field: string | ((...args: any[]) => boolean)
    ): string | ((...args: any[]) => boolean) {
        if (field === null) {
            field = EditorFieldMappingService.WILDCARD;
        } else if (typeof field !== 'string' && typeof field !== 'function') {
            throw new Error(
                'editorFieldMappingService: Mapping matcher must be of type string or function.'
            );
        }

        return field;
    }

    _getMapping(
        field: GenericEditorField,
        componentTypeStructure: GenericEditorStructure,
        collection: GenericEditorMapping[]
    ): any {
        let result = null;
        let maxValue = 0;
        collection.forEach((mappingEntry) => {
            const mappingValue = this._evaluateMapping(mappingEntry, field, componentTypeStructure);
            if (mappingValue > maxValue) {
                result = mappingEntry.value;
                maxValue = mappingValue;
            }
        });

        return result;
    }

    _evaluateMapping(
        mappingEntry: GenericEditorMapping,
        field: GenericEditorField,
        componentTypeStructure: GenericEditorStructure
    ): number {
        let componentTypeMatch;
        let discriminatorMatch;

        let mappingMatch = EditorFieldMappingService.MATCH.NONE;

        const structureTypeMatch = this._evaluateMatcher(
            mappingEntry.structureTypeMatcher,
            field.cmsStructureType,
            field,
            componentTypeStructure
        );
        if (structureTypeMatch !== EditorFieldMappingService.MATCH.NONE) {
            componentTypeMatch = this._evaluateMatcher(
                mappingEntry.componentTypeMatcher,
                field.smarteditComponentType,
                field,
                componentTypeStructure
            );
            if (componentTypeMatch !== EditorFieldMappingService.MATCH.NONE) {
                discriminatorMatch = this._evaluateMatcher(
                    mappingEntry.discriminatorMatcher,
                    field.qualifier,
                    field,
                    componentTypeStructure
                );
            }
        }

        if (
            structureTypeMatch !== EditorFieldMappingService.MATCH.NONE &&
            componentTypeMatch !== EditorFieldMappingService.MATCH.NONE &&
            discriminatorMatch !== EditorFieldMappingService.MATCH.NONE
        ) {
            mappingMatch = structureTypeMatch + componentTypeMatch + discriminatorMatch;
        }

        return mappingMatch;
    }

    _evaluateMatcher(
        matcher: string | ((...args: any[]) => boolean),
        actualValue: string,
        field: GenericEditorField,
        componentTypeStructure: GenericEditorStructure
    ): number {
        if (typeof matcher === 'string') {
            if (matcher === EditorFieldMappingService.WILDCARD) {
                return EditorFieldMappingService.MATCH.PARTIAL;
            } else {
                return this._exactValueMatchPredicate(matcher, actualValue)
                    ? EditorFieldMappingService.MATCH.EXACT
                    : EditorFieldMappingService.MATCH.NONE;
            }
        } else {
            return matcher(actualValue, field, componentTypeStructure)
                ? EditorFieldMappingService.MATCH.EXACT
                : EditorFieldMappingService.MATCH.NONE;
        }
    }

    _registerDefaultFieldMappings(): void {
        if (!this._initialized) {
            this._initialized = true;

            this.addFieldMapping('Boolean', null, null, {
                component: BooleanComponent
            });

            this.addFieldMapping('ShortString', null, null, {
                component: ShortStringComponent,
                validators: {
                    notBlank: (
                        id: string,
                        structure: GenericEditorField,
                        required: boolean,
                        component: Payload
                    ): boolean => (!!required ? !!required : undefined)
                }
            });

            this.addFieldMapping('Text', null, null, {
                component: TextComponent
            });

            this.addFieldMapping('LongString', null, null, {
                component: LongStringComponent
            });

            this.addFieldMapping('RichText', null, null, {
                component: RichTextFieldComponent
            });

            this.addFieldMapping('Number', null, null, {
                component: NumberComponent
            });

            this.addFieldMapping('Float', null, null, {
                component: FloatComponent
            });

            this.addFieldMapping('Dropdown', null, null, {
                component: DropdownComponent
            });

            this.addFieldMapping('EditableDropdown', null, null, {
                component: EditableDropdownComponent
            });

            this.addFieldMapping('DateTime', null, null, {
                component: DateTimePickerComponent
            });

            this.addFieldMapping('Enum', null, null, {
                component: EnumComponent
            });
            this.addFieldMapping('Email', null, null, {
                component: EmailComponent,
                validators: {
                    email: (
                        id: string,
                        structure: GenericEditorField,
                        required: boolean,
                        component: Payload
                    ): boolean => true
                }
            });
        }
    }

    _cleanTemplate(template: string): string {
        const index = template ? template.lastIndexOf('/') : -1;
        if (index !== -1) {
            template = template.substring(index + 1);
        }

        return template;
    }

    // --------------------------------------------------------------------------------------
    // Predicates
    // --------------------------------------------------------------------------------------
    _exactValueMatchPredicate(expectedValue: string, actualValue: string): boolean {
        return expectedValue === actualValue;
    }
}
