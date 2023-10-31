import { CloneableUtils, InternalFeature, IFeatureService, IToolbarItem, IToolbarServiceFactory, LogService } from 'smarteditcommons';
/** @internal */
export declare class FeatureService extends IFeatureService {
    private toolbarServiceFactory;
    protected logService: LogService;
    private features;
    constructor(toolbarServiceFactory: IToolbarServiceFactory, cloneableUtils: CloneableUtils, logService: LogService);
    getFeatureProperty(featureKey: string, propertyName: keyof InternalFeature): Promise<string | string[] | (() => void)>;
    getFeatureKeys(): string[];
    addToolbarItem(configuration: IToolbarItem): Promise<void>;
    protected _registerAliases(configuration: InternalFeature): Promise<void>;
    private _getFeatureByKey;
}
