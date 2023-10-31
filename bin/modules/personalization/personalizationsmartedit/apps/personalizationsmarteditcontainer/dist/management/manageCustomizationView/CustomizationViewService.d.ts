import { Customization, CustomizationVariation, PersonalizationsmarteditUtils } from 'personalizationcommons';
import { Observable } from 'rxjs';
interface CustomizationViewState {
    customization: Customization;
    /** Variations that are displayed in TargetGroupVariationList component. */
    visibleVariations: CustomizationVariation[];
}
export declare const defaultCustomization: Customization;
export declare class CustomizationViewService {
    private persoUtils;
    private editVariationActionSubject;
    private stateSubject;
    constructor(persoUtils: PersonalizationsmarteditUtils);
    getState$(): Observable<CustomizationViewState>;
    getState(): CustomizationViewState;
    setState(state: CustomizationViewState): void;
    addVariation(variation: CustomizationVariation): void;
    setVariations(variations: CustomizationVariation[]): void;
    setCustomization(customization: Customization): void;
    getCustomization$(): Observable<Customization>;
    editVariationAction(variation: CustomizationVariation): void;
    editVariationAction$(): Observable<CustomizationVariation | undefined>;
    selectCustomization(): Customization;
    selectVisibleVariations(): CustomizationViewState['visibleVariations'];
    selectVariationByCode(code: string): CustomizationVariation | undefined;
    /** Calculate visible items */
    private prepareState;
}
export {};
