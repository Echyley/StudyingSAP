import { SearchProfileActionContext } from 'personalizationsearchsmarteditcontainer/types';
export declare class SearchProfilesContextService {
    searchProfileActionContext: SearchProfileActionContext;
    constructor();
    searchProfileActionComparer: (a1: any, a2: any) => boolean;
    updateSearchActionContext: (actions: any) => any;
    isDirty: () => boolean;
}
