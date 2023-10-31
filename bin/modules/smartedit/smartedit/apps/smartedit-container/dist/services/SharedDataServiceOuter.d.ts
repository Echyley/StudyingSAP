import { Cloneable, ISharedDataService } from 'smarteditcommons';
/** @internal */
export declare class SharedDataService extends ISharedDataService {
    private storage;
    constructor();
    get(key: string): Promise<Cloneable>;
    set(key: string, value: Cloneable): Promise<void>;
    update(key: string, modifyingCallback: (oldValue: any) => any): Promise<void>;
    remove(key: string): Promise<Cloneable>;
    containsKey(key: string): Promise<boolean>;
}
