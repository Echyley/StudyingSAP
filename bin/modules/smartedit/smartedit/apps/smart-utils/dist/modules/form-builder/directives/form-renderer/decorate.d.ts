/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ComponentRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { AbstractForm } from '../../models';
/**
 * @internal
 * Decorates the components with the decorators that were put into places.
 * The idea is made similar to how Angular decorates their properties with inputs.
 */
export declare const decorate: (componentRef: ComponentRef<any>, form: AbstractForm) => Subscription;
