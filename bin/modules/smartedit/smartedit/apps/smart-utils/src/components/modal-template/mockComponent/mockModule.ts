/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TestComponent } from './TestComponent';

@NgModule({
    declarations: [TestComponent],
    imports: [CommonModule],
    entryComponents: [TestComponent]
})
export class MockModule {}
