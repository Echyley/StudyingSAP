import { Dictionary } from 'lodash';
import { PersonalizationsmarteditComponentHandlerService } from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';
export declare class PersonalizationsmarteditCustomizeViewHelper {
    private personalizationsmarteditComponentHandlerService;
    constructor(personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService);
    getSourceContainersInfo(): Dictionary<number>;
}
