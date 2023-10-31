import { IContextualMenuConfiguration } from 'smarteditcommons';
export declare abstract class IComponentMenuConditionAndCallbackService {
    externalCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
    sharedCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
    removeCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
    removeCallback(configuration: IContextualMenuConfiguration, $event: Event): Promise<void>;
    cloneCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
    cloneCallback(configuration: IContextualMenuConfiguration): Promise<void>;
    editConditionForHiddenComponent(configuration: IContextualMenuConfiguration): Promise<boolean>;
    dragCondition(configuration: IContextualMenuConfiguration): Promise<boolean>;
}
