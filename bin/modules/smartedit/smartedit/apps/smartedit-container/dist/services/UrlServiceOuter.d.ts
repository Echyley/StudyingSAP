import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { IUrlService, WindowUtils } from 'smarteditcommons';
/** @internal */
export declare class UrlService extends IUrlService {
    private router;
    private location;
    private windowUtils;
    constructor(router: Router, location: Location, windowUtils: WindowUtils);
    openUrlInPopup(url: string): void;
    path(url: string): void;
}
