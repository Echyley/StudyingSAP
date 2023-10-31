/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';

import { DragAndDropScrollingService } from './DragAndDropScrollingService';
import { DragAndDropService } from './DragAndDropService';
import { InViewElementObserver } from './InViewElementObserver';

/**
 * Contains a service that provides a rich drag and drop experience tailored for CMS operations.
 */
@NgModule({
    providers: [InViewElementObserver, DragAndDropScrollingService, DragAndDropService]
})
export class DragAndDropServiceModule {}
