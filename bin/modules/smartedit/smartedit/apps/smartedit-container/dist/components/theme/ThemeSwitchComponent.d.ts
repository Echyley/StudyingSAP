import { OnInit } from '@angular/core';
import { GridListItemOutputEvent } from '@fundamental-ngx/core/grid-list';
import { ThemesService, ModalManagerService, IConfirmationModalService } from 'smarteditcommons';
import { ThemeSwitchService } from '../../services/theme/ThemeSwitchService';
export declare class ThemeSwitchComponent implements OnInit {
    private readonly _themesService;
    private readonly _themeSwitchService;
    modalManager: ModalManagerService;
    private readonly confirmationModalService;
    themes: any;
    layoutPattern: string;
    private readonly list;
    constructor(_themesService: ThemesService, _themeSwitchService: ThemeSwitchService, modalManager: ModalManagerService, confirmationModalService: IConfirmationModalService);
    ngOnInit(): void;
    onSelectionChange(event: GridListItemOutputEvent<number>): void;
    private onCancel;
    private onSave;
}
