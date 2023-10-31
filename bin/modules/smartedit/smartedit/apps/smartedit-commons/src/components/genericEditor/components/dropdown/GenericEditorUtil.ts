/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GenericEditorField } from '../../types';
import {
    DropdownPopulatorInterface,
    IDropdownPopulator,
    OptionsDropdownPopulator,
    UriDropdownPopulator
} from './populators';
import { IPopulatorName } from './types';

// This util class is derived from GenericEditorDropdownServiceFactory.ts to shorten file size
export class GenericEditorUtil {
    public isFieldPaged(paged: boolean | undefined = undefined): boolean {
        return paged ? paged : false;
    }

    /**
     * Lookup for Populator with given name and returns its instance.
     *
     * it will look for Angular service in `customDropdownPopulators`.
     */
    public resolvePopulatorByName(
        name: string,
        optionsDropdownPopulator: OptionsDropdownPopulator,
        uriDropdownPopulator: UriDropdownPopulator,
        customDropdownPopulators: DropdownPopulatorInterface[]
    ): IDropdownPopulator | undefined {
        if (name === optionsDropdownPopulator.constructor.name) {
            return optionsDropdownPopulator;
        }

        if (name === uriDropdownPopulator.constructor.name) {
            return uriDropdownPopulator;
        }

        if (customDropdownPopulators && customDropdownPopulators.length > 0) {
            return customDropdownPopulators.find(
                (populator) => populator.constructor.name.toUpperCase() === name.toUpperCase()
            );
        }

        return undefined;
    }

    public isPopulatorPaged(populator: IDropdownPopulator): boolean {
        return populator.isPaged && populator.isPaged();
    }

    public resolvePopulator(
        field: GenericEditorField,
        populatorName: IPopulatorName,
        optionsDropdownPopulator: OptionsDropdownPopulator,
        uriDropdownPopulator: UriDropdownPopulator,
        customDropdownPopulators: DropdownPopulatorInterface[]
    ):
        | {
              instance: IDropdownPopulator;
              isPaged: boolean;
          }
        | undefined {
        if (field.options && field.uri) {
            throw new Error('se.dropdown.contains.both.uri.and.options');
        }
        // OptionsDropdownPopulator e.g. EditableDropdown
        if (field.options) {
            return {
                instance: this.resolvePopulatorByName(
                    populatorName.options,
                    optionsDropdownPopulator,
                    uriDropdownPopulator,
                    customDropdownPopulators
                ),
                isPaged: false
            };
        }

        // UriDropdownPopulator
        if (field.uri) {
            return {
                instance: this.resolvePopulatorByName(
                    populatorName.uri,
                    optionsDropdownPopulator,
                    uriDropdownPopulator,
                    customDropdownPopulators
                ),
                isPaged: this.isFieldPaged(field.paged)
            };
        }

        // e.g. productDropdownPopulator, categoryDropdownPopulator
        if (field.propertyType) {
            const populator = this.resolvePopulatorByName(
                populatorName.propertyType,
                optionsDropdownPopulator,
                uriDropdownPopulator,
                customDropdownPopulators
            );
            return {
                instance: populator,
                isPaged: this.isPopulatorPaged(populator)
            };
        }

        // e.g. CMSItemDropdownDropdownPopulator
        const cmsStructureTypePopulator = this.resolvePopulatorByName(
            populatorName.cmsStructureType,
            optionsDropdownPopulator,
            uriDropdownPopulator,
            customDropdownPopulators
        );
        if (cmsStructureTypePopulator) {
            return {
                instance: cmsStructureTypePopulator,
                isPaged: this.isFieldPaged(field.paged)
            };
        }

        // For downstream teams
        // e.g. SmarteditComponentType + qualifier + DropdownPopulator
        const smarteditComponentTypeWithQualifierPopulator = this.resolvePopulatorByName(
            populatorName.smarteditComponentType.withQualifier,
            optionsDropdownPopulator,
            uriDropdownPopulator,
            customDropdownPopulators
        );
        if (smarteditComponentTypeWithQualifierPopulator) {
            return {
                instance: smarteditComponentTypeWithQualifierPopulator,
                isPaged: this.isPopulatorPaged(smarteditComponentTypeWithQualifierPopulator)
            };
        }

        // For downstream teams
        // TODO: PreviewDatapreviewCatalogDropdownPopulator provide with the token
        // e.g. smarteditComponentType + qualifier + DropdownPopulator
        const smarteditComponentTypeWithQualifierForDowngradedServicePopulator =
            this.resolvePopulatorByName(
                populatorName.smarteditComponentType.withQualifierForDowngradedService,
                optionsDropdownPopulator,
                uriDropdownPopulator,
                customDropdownPopulators
            );
        if (smarteditComponentTypeWithQualifierForDowngradedServicePopulator) {
            return {
                instance: smarteditComponentTypeWithQualifierForDowngradedServicePopulator,
                isPaged: this.isPopulatorPaged(
                    smarteditComponentTypeWithQualifierForDowngradedServicePopulator
                )
            };
        }

        // For downstream teams
        // e.g. SmarteditComponentType + DropdownPopulator
        const smarteditComponentTypeWithoutQualifierPopulator = this.resolvePopulatorByName(
            populatorName.smarteditComponentType.withoutQualifier,
            optionsDropdownPopulator,
            uriDropdownPopulator,
            customDropdownPopulators
        );
        if (smarteditComponentTypeWithoutQualifierPopulator) {
            return {
                instance: smarteditComponentTypeWithoutQualifierPopulator,
                isPaged: this.isPopulatorPaged(smarteditComponentTypeWithoutQualifierPopulator)
            };
        }
        return undefined;
    }
}
