import { PageVersionSelectionService, IPageVersion } from 'smarteditcommons';
import { PersonalizationsmarteditRestService } from './PersonalizationsmarteditRestService';
export declare class PersonalizationsmarteditVersionCheckerService {
    protected restService: PersonalizationsmarteditRestService;
    protected pageVersionSelectionService: PageVersionSelectionService;
    private version;
    constructor(restService: PersonalizationsmarteditRestService, pageVersionSelectionService: PageVersionSelectionService);
    setVersion(version: IPageVersion): void;
    provideTranlationKey(key: string): Promise<string>;
}
