import { DialogRef as FundamentalModalRef, DialogService as FundamentalModalService } from '@fundamental-ngx/core';
import { IModalService } from '../../interfaces';
import { ModalOpenConfig } from './i-modal.service';
export declare class ModalService implements IModalService {
    protected fundamentalModalService: FundamentalModalService;
    constructor(fundamentalModalService: FundamentalModalService);
    open<T>(options: ModalOpenConfig<T>): FundamentalModalRef;
    hasOpenModals(): boolean;
    dismissAll(): void;
    private openStandalone;
    private openWithTemplate;
}
