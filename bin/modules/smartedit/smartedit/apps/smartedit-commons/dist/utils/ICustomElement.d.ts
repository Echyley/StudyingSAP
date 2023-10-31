export interface ICustomElement {
    connectedCallback?(): void;
    disconnectedCallback?(): void;
    attributeChangedCallback?(name: string, oldValue: any, newValue: any): void;
}
export declare type CustomElementConstructor = new (...arg: any[]) => ICustomElement;
