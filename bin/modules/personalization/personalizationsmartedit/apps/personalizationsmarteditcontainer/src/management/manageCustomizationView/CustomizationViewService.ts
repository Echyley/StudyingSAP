/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    Customization,
    CustomizationVariation,
    PersonalizationsmarteditUtils,
    PERSONALIZATION_MODEL_STATUS_CODES
} from 'personalizationcommons';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

interface CustomizationViewState {
    customization: Customization;
    /** Variations that are displayed in TargetGroupVariationList component. */
    visibleVariations: CustomizationVariation[];
}

export const defaultCustomization: Customization = {
    name: '',
    enabledStartDate: '',
    enabledEndDate: '',
    status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
    statusBoolean: true,
    variations: [],
    catalog: '',
    catalogVersion: '',
    code: '',
    rank: null
};

const defaultCustomizationViewState: CustomizationViewState = {
    customization: defaultCustomization,
    visibleVariations: []
};

@Injectable()
export class CustomizationViewService {
    private editVariationActionSubject: BehaviorSubject<CustomizationVariation | undefined>;
    private stateSubject: BehaviorSubject<CustomizationViewState>;

    constructor(private persoUtils: PersonalizationsmarteditUtils) {
        this.editVariationActionSubject = new BehaviorSubject(undefined);
        this.stateSubject = new BehaviorSubject(defaultCustomizationViewState);
    }

    public getState$(): Observable<CustomizationViewState> {
        return this.stateSubject.asObservable();
    }

    public getState(): CustomizationViewState {
        return this.stateSubject.getValue();
    }

    public setState(state: CustomizationViewState): void {
        this.stateSubject.next(state);
    }

    public addVariation(variation: CustomizationVariation): void {
        const customization = this.selectCustomization();

        const variations = [...customization.variations, variation];
        this.setVariations(variations);
    }

    public setVariations(variations: CustomizationVariation[]): void {
        const customization = this.selectCustomization();
        customization.variations = variations.map((variation, idx) => {
            variation.rank = idx;
            return variation;
        });

        this.setState(this.prepareState(customization));
    }

    public setCustomization(customization: Customization): void {
        this.setState(this.prepareState(customization));
    }

    public getCustomization$(): Observable<Customization> {
        return this.getState$().pipe(map((state) => state.customization));
    }

    // Actions
    public editVariationAction(variation: CustomizationVariation): void {
        this.editVariationActionSubject.next(variation);
    }

    public editVariationAction$(): Observable<CustomizationVariation | undefined> {
        return this.editVariationActionSubject.asObservable();
    }

    // Selectors
    public selectCustomization(): Customization {
        return this.getState().customization;
    }

    public selectVisibleVariations(): CustomizationViewState['visibleVariations'] {
        return this.getState().visibleVariations;
    }

    public selectVariationByCode(code: string): CustomizationVariation | undefined {
        return this.getState().customization.variations.find(
            (variation) => variation.code === code
        );
    }

    /** Calculate visible items */
    private prepareState(customization: Customization): CustomizationViewState {
        return {
            customization,
            visibleVariations: this.persoUtils.getVisibleItems(customization.variations)
        };
    }
}
