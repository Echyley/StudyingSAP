import { OnInit } from '@angular/core';
import { ConfirmationModalConfig, ModalManagerService } from '../../services';
export declare class ConfirmDialogComponent implements OnInit {
    private modalManager;
    config: ConfirmationModalConfig;
    constructor(modalManager: ModalManagerService);
    ngOnInit(): void;
}
