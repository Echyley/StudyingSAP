/**
 * Provides an abstract extensible pageTree service. Used to manage and perform actions to either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
export declare abstract class IPageTreeService {
    protected item: any;
    registerTreeComponent(item: any): Promise<void>;
    getTreeComponent(): Promise<any>;
}
