import { OnInit } from '@angular/core';
import { AlertRef } from '@fundamental-ngx/core';
import { IExperienceService, TypedMap } from 'smarteditcommons';
export declare class PageRestoredAlertComponent implements OnInit {
    private alertRef;
    private experienceService;
    hyperlinkLabel: string;
    hyperLinkDetails: TypedMap<string>;
    message: string;
    constructor(alertRef: AlertRef, experienceService: IExperienceService);
    ngOnInit(): void;
    onClick(): void;
}
