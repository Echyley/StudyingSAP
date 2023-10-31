/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export interface ICustomElement {
    connectedCallback?(): void;
    disconnectedCallback?(): void;
    attributeChangedCallback?(name: string, oldValue: any, newValue: any): void;
}

export type CustomElementConstructor = new (...arg: any[]) => ICustomElement;
