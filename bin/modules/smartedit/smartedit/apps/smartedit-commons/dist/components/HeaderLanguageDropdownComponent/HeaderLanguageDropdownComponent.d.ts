import './HeaderLanguageDropdownComponent.scss';
import { LanguageDropdown, LogService } from '@smart/utils';
import { CrossFrameEventService } from '../../services/crossFrame/CrossFrameEventService';
import { LanguageService } from '../../services/language/LanguageService';
export declare class HeaderLanguageDropdownComponent extends LanguageDropdown {
    languageService: LanguageService;
    crossFrameEventService: CrossFrameEventService;
    logService: LogService;
    constructor(languageService: LanguageService, crossFrameEventService: CrossFrameEventService, logService: LogService);
}
