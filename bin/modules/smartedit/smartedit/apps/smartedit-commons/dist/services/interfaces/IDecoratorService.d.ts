import { IDecoratorDisplayCondition } from './IDecorator';
export declare abstract class IDecoratorService {
    addMappings(mappings: {
        [index: string]: string[];
    }): void;
    enable(decoratorKey: string, displayCondition?: IDecoratorDisplayCondition): void;
    disable(decoratorKey: string): void;
    getDecoratorsForComponent(componentType: string, componentId?: string): Promise<string[]>;
}
