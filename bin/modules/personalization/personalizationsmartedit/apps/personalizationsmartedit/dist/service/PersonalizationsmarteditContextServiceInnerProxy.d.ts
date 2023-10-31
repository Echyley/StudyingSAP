import { Customize, CombinedView, SeData, Personalization, IPersonalizationsmarteditContextServiceProxy } from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import { CrossFrameEventService } from 'smarteditcommons';
export declare class PersonalizationsmarteditContextServiceProxy extends IPersonalizationsmarteditContextServiceProxy {
    protected personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    protected crossFrameEventService: CrossFrameEventService;
    constructor(personalizationsmarteditContextService: PersonalizationsmarteditContextService, crossFrameEventService: CrossFrameEventService);
    setPersonalization(newPersonalization: Personalization): void;
    setCustomize(newCustomize: Customize): void;
    setCombinedView(newCombinedView: CombinedView): void;
    setSeData(newSeData: SeData): void;
}
