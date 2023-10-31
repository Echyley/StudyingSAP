import { OnDestroy } from '@angular/core';
import { Observable } from 'rxjs';
import { CrossFrameEventService } from './crossFrame/CrossFrameEventService';
import { LanguageService } from './language/LanguageService';
/**
 * Service which exposes a subscription of the current language.
 */
export declare class L10nService implements OnDestroy {
    private readonly languageService;
    languageSwitch$: Observable<string>;
    private readonly languageSwitchSubject;
    private readonly unRegSwitchLanguageEvent;
    constructor(languageService: LanguageService, crossFrameEventService: CrossFrameEventService);
    ngOnDestroy(): void;
    resolveLanguage(): Promise<void>;
}
