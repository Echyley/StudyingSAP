import { ConfirmationModalConfig } from './IConfirmationModal';
export declare abstract class IConfirmationModalService {
    confirm(conf: ConfirmationModalConfig): Promise<boolean>;
}
