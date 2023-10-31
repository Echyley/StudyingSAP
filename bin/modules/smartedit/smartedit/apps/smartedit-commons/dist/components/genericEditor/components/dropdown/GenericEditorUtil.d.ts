import { GenericEditorField } from '../../types';
import { DropdownPopulatorInterface, IDropdownPopulator, OptionsDropdownPopulator, UriDropdownPopulator } from './populators';
import { IPopulatorName } from './types';
export declare class GenericEditorUtil {
    isFieldPaged(paged?: boolean | undefined): boolean;
    /**
     * Lookup for Populator with given name and returns its instance.
     *
     * it will look for Angular service in `customDropdownPopulators`.
     */
    resolvePopulatorByName(name: string, optionsDropdownPopulator: OptionsDropdownPopulator, uriDropdownPopulator: UriDropdownPopulator, customDropdownPopulators: DropdownPopulatorInterface[]): IDropdownPopulator | undefined;
    isPopulatorPaged(populator: IDropdownPopulator): boolean;
    resolvePopulator(field: GenericEditorField, populatorName: IPopulatorName, optionsDropdownPopulator: OptionsDropdownPopulator, uriDropdownPopulator: UriDropdownPopulator, customDropdownPopulators: DropdownPopulatorInterface[]): {
        instance: IDropdownPopulator;
        isPaged: boolean;
    } | undefined;
}
