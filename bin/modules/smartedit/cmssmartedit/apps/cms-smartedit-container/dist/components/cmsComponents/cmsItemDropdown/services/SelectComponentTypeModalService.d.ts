import { LogService, ModalService, TypedMap } from 'smarteditcommons';
export declare class SelectComponentTypeModalService {
    private logService;
    private modalService;
    constructor(logService: LogService, modalService: ModalService);
    open(subTypes: TypedMap<string>): Promise<string | void>;
}
