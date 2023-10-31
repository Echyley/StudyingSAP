import { ChangeDetectorRef } from '@angular/core';
import { AlertRef } from '@fundamental-ngx/core';
import { IExperienceService, L10nPipe, TypedMap } from 'smarteditcommons';
export declare class ClonePageAlertComponent {
    private alertRef;
    private experienceService;
    private cdr;
    private l10n;
    description: string;
    hyperlinkLabel: string;
    descriptionDetails: TypedMap<any>;
    private catalogVersion;
    private clonedPageInfo;
    constructor(alertRef: AlertRef, experienceService: IExperienceService, cdr: ChangeDetectorRef, l10n: L10nPipe);
    ngOnInit(): Promise<void>;
    onClick(): void;
    private getTranslatedName;
}
