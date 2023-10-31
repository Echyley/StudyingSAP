/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CombinedViewSelectItem } from '../types';
import { Customize } from './Customize';

export class CombinedView {
    public enabled: boolean;
    public selectedItems: CombinedViewSelectItem[] | null;
    public customize: Customize;

    constructor() {
        this.enabled = false;
        this.selectedItems = null;
        this.customize = new Customize();
    }
}
