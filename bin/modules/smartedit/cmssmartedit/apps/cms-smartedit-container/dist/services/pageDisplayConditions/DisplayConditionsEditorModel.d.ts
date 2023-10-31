import { DisplayConditionsFacade, IDisplayConditionsPageVariation, IDisplayConditionsPrimaryPage } from '../../facades/DisplayConditionsFacade';
/**
 * Initializes a Page data such as type, name for Primary or Variation Page.
 * Used as a local instance of DisplayConditionsEditor.
 */
export declare class DisplayConditionsEditorModel {
    private displayConditionsFacade;
    variations: IDisplayConditionsPageVariation[];
    associatedPrimaryPage: Partial<IDisplayConditionsPrimaryPage>;
    originalPrimaryPage: Partial<IDisplayConditionsPrimaryPage>;
    isAssociatedPrimaryReadOnly: boolean;
    pageType: string;
    pageUid: string;
    pageName: string;
    isPrimary: boolean;
    constructor(displayConditionsFacade: DisplayConditionsFacade);
    initModel(pageUid: string): Promise<void>;
    private initModelForPrimary;
    private initModelForVariation;
}
