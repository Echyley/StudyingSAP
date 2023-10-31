/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AbstractControlOptions, FormArray } from '@angular/forms';
import { values } from 'lodash';

import { Observable } from 'rxjs';
import { AbstractForm } from './abstract-form';
import { InputProperties, InputPropertyChange } from './input-properties';
import { ComponentType } from './types';
import { ValidatorParameters } from './validator-parameters';

export class FormList extends FormArray implements AbstractForm {
    /**
     * @inheritdoc
     * @override
     */
    readonly controls!: AbstractForm[];

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

    constructor(
        controls: AbstractForm[],
        validatorOrOpts: AbstractControlOptions = {},
        {
            component,
            inputs = new InputProperties(),
            validatorParams = new ValidatorParameters(),
            persist = true
        }: {
            component: ComponentType;
            inputs: InputProperties;
            validatorParams: ValidatorParameters;
            persist: boolean;
        }
    ) {
        super(controls, validatorOrOpts);

        this.component = component;
        this.inputs = inputs;
        this.inputChanges = inputs.changes;
        this.validatorParams = validatorParams;
        this.persist = persist;
    }

    /**
     * @inheritdoc
     * @param key
     * @param value
     */
    setInput<T>(key: keyof T, value: T[keyof T]): void {
        this.inputs.set(key, value);
    }

    /**
     * @inheritdoc
     * @param key
     */
    getInput<T>(key: keyof T): T[keyof T] | undefined {
        return this.inputs.get<T>(key);
    }

    /**
     * @inheritdoc
     * @override
     */
    getPersistedValue(): any[] {
        return this.controls.reduce((acc: any[], child: AbstractForm) => {
            if (!child.persist) {
                /**
                 * Look ahead and merge the values of the
                 * nested group, array or field.
                 *
                 * If it's a field, the values of the field
                 * would be an empty array, because it's not an object.
                 */
                return acc.concat(values(child.getPersistedValue()));
            }

            acc.push(child.getPersistedValue());
            return acc;
        }, []);
    }

    /**
     * The size of the list.
     */
    size(): number {
        return this.controls.length;
    }

    /**
     * Swaps a form element in the array.
     *
     * @param a The index of form a.
     * @param b The index of form b.
     */
    swapFormElements(a: number, b: number): void {
        if (!this._isInBounds(a) || !this._isInBounds(b) || a === b) {
            return;
        }

        /**
         * Swapping control's array.
         */
        const control = this.at(a);
        this.insert(a, this.at(b));
        this.insert(b, control);
    }

    /**
     * Moves a form element in the array to a new position.
     *
     * @param from The previous index.
     * @param to The targeted index.
     */
    moveFormElement(from: number, to: number): void {
        if (!this._isInBounds(from) || !this._isInBounds(to) || from === to) {
            return;
        }

        const delta = to < from ? -1 : 1;

        const tempControl = this.at(from);

        for (let i = from; (i - to) * delta < 0; i += delta) {
            const position = i + delta;

            this.setControl(i, this.at(position));
        }
        this.setControl(to, tempControl);
    }

    /**
     * Checks of the index is in bounds.
     *
     * @internal
     * @param index
     */
    private _isInBounds(index: number): boolean {
        return index < this.controls.length && index >= 0;
    }
}
