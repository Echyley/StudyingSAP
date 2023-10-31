import { PersonalizationsmarteditContextUtils, PersonalizationsmarteditUtils } from 'personalizationcommons';
import { IModalService, LogService } from 'smarteditcommons';
import { PersonalizationsmarteditContextService, PersonalizationsmarteditPreviewService, PersonalizationsmarteditRestService } from '../service';
export declare class CombinedViewCommonsService {
    protected personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils;
    protected personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    protected personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService;
    protected personalizationsmarteditUtils: PersonalizationsmarteditUtils;
    protected personalizationsmarteditRestService: PersonalizationsmarteditRestService;
    private modalService;
    private logService;
    constructor(personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils, personalizationsmarteditContextService: PersonalizationsmarteditContextService, personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService, personalizationsmarteditUtils: PersonalizationsmarteditUtils, personalizationsmarteditRestService: PersonalizationsmarteditRestService, modalService: IModalService, logService: LogService);
    openManagerAction(): Promise<void>;
    updatePreview(previewTicketVariations: any): void;
    getVariationsForPreviewTicket(): any[];
    combinedViewEnabledEvent(isEnabled: boolean): void;
    isItemFromCurrentCatalog(item: any): boolean;
    private updateActionsOnSelectedVariations;
}
