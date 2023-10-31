/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';

import { PropertyPipe } from './PropertyPipe';

@NgModule({
    declarations: [PropertyPipe],
    exports: [PropertyPipe]
})
export class PropertyPipeModule {}
