import { AbstractControlOptions, FormArray } from '@angular/forms';
import { Observable } from 'rxjs';
import { AbstractForm } from './abstract-form';
import { InputProperties, InputPropertyChange } from './input-properties';
import { ComponentType } from './types';
import { ValidatorParameters } from './validator-parameters';
export declare class FormList extends FormArray implements AbstractForm {
    /**
     * @inheritdoc
     * @override
     */
    readonly controls: AbstractForm[];
    /**
     * @inheritdoc
     */
    readonly component: ComponentType;
    /**
     * @inheritdoc
     */
    readonly inputs: InputProperties;
    /**
     * @inheritdoc
     */
    readonly validatorParams: ValidatorParameters;
    /**
     * @inheritdoc
     */
    readonly persist: boolean;
    /**
     * @inheritdoc
     */
    readonly inputChanges: Observable<InputPropertyChange>;
    constructor(controls: AbstractForm[], validatorOrOpts: AbstractControlOptions, { component, inputs, validatorParams, persist }: {
        component: ComponentType;
        inputs: InputProperties;
        validatorParams: ValidatorParameters;
        persist: boolean;
    });
    /**
     * @inheritdoc
     * @param key
     * @param value
     */
    setInput<T>(key: keyof T, value: T[keyof T]): void;
    /**
     * @inheritdoc
     * @param key
     */
    getInput<T>(key: keyof T): T[keyof T] | undefined;
    /**
     * @inheritdoc
     * @override
     */
    getPersistedValue(): any[];
    /**
     * The size of the list.
     */
    size(): number;
    /**
     * Swaps a form element in the array.
     *
     * @param a The index of form a.
     * @param b The index of form b.
     */
    swapFormElements(a: number, b: number): void;
    /**
     * Moves a form element in the array to a new position.
     *
     * @param from The previous index.
     * @param to The targeted index.
     */
    moveFormElement(from: number, to: number): void;
    /**
     * Checks of the index is in bounds.
     *
     * @internal
     * @param index
     */
    private _isInBounds;
}
