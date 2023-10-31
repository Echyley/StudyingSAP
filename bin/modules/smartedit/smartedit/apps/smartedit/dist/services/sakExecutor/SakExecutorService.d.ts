import { IDecoratorService } from 'smarteditcommons';
export declare class SakExecutorService {
    private decoratorService;
    constructor(decoratorService: IDecoratorService);
    wrapDecorators(projectedContent: Node, element: HTMLElement): Promise<HTMLElement>;
    private setAttributeOn;
}
