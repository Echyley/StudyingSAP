import { LogService } from '../../../../services';
import { SystemEventService } from '../../../../services/SystemEventService';
import { GenericEditorUtil } from './GenericEditorUtil';
import { DropdownPopulatorInterface, OptionsDropdownPopulator, UriDropdownPopulator } from './populators';
/**
 * The SEDropdownService handles the initialization and the rendering of the {@link SeDropdownComponent}.
 * - Angular - `CustomDropdownPopulatorsToken` Injection Token
 */
export declare const GenericEditorDropdownServiceFactory: (genericEditorUtil: GenericEditorUtil, logService: LogService, LINKED_DROPDOWN: string, CLICK_DROPDOWN: string, DROPDOWN_IMPLEMENTATION_SUFFIX: string, systemEventService: SystemEventService, optionsDropdownPopulator: OptionsDropdownPopulator, uriDropdownPopulator: UriDropdownPopulator, customDropdownPopulators?: DropdownPopulatorInterface[]) => any;
