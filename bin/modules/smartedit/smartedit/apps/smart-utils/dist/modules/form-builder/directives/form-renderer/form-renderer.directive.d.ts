/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ComponentFactoryResolver, OnDestroy, ViewContainerRef } from '@angular/core';
import { AbstractForm } from '../../models';
export declare class FormRendererDirective implements OnDestroy {
    private componentFactoryResolver;
    private viewContainerRef;
    /**
     * @internal
     */
    private _subscription;
    set form(form: AbstractForm);
    constructor(componentFactoryResolver: ComponentFactoryResolver, viewContainerRef: ViewContainerRef);
    ngOnDestroy(): void;
    /**
     * @internal
     * Clear all views and unsubscribe to streams.
     */
    private _dispose;
}
