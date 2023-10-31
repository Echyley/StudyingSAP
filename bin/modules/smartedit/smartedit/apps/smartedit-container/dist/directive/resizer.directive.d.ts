/// <reference types="jquery" />
import { IStorageService } from 'smarteditcommons';
/**
 * Used to adjust the width of page tree continer in storefrontPage
 */
export declare class ResizerDirective {
    private readonly storageService;
    private readonly yjQuery;
    resizer: string;
    private readonly instanceId;
    constructor(storageService: IStorageService, yjQuery: JQueryStatic);
    onMouseDown($event: MouseEvent): void;
}
