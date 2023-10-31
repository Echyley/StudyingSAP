/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InfiniteScrollModule } from '@fundamental-ngx/core';

import { SpinnerModule } from '../spinner';
import { InfiniteScrollingComponent } from './InfiniteScrollingComponent';

@NgModule({
    imports: [InfiniteScrollModule, SpinnerModule, CommonModule],
    declarations: [InfiniteScrollingComponent],
    entryComponents: [InfiniteScrollingComponent],
    exports: [InfiniteScrollingComponent]
})
export class InfiniteScrollingModule {}
