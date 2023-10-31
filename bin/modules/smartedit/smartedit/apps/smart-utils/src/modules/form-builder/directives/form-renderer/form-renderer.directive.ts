/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    ComponentFactoryResolver,
    Directive,
    Input,
    OnDestroy,
    ViewContainerRef
} from '@angular/core';
import { Subscription } from 'rxjs';

import { AbstractForm } from '../../models';
import { decorate } from './decorate';

@Directive({
    selector: '[formRenderer]'
})
export class FormRendererDirective implements OnDestroy {
    /**
     * @internal
     */
    private _subscription!: Subscription;

    @Input('formRenderer')
    set form(form: AbstractForm) {
        this._dispose();
        const componentFactory = this.componentFactoryResolver.resolveComponentFactory(
            form.component
        );

        // Create and decorate the component's inputs.
        const componentRef = this.viewContainerRef.createComponent(componentFactory);
        this._subscription = decorate(componentRef, form);
    }

    constructor(
        private componentFactoryResolver: ComponentFactoryResolver,
        private viewContainerRef: ViewContainerRef
    ) {}

    ngOnDestroy(): void {
        this._dispose();
    }

    /**
     * @internal
     * Clear all views and unsubscribe to streams.
     */
    private _dispose(): void {
        this._subscription && this._subscription.unsubscribe();
        this.viewContainerRef.clear();
    }
}
