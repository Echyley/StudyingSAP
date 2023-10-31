import { DomSanitizer } from '@angular/platform-browser';
export declare class GenericEditorSanitizationService {
    private domSanitizer;
    constructor(domSanitizer: DomSanitizer);
    isSanitized(content: string): boolean;
}
