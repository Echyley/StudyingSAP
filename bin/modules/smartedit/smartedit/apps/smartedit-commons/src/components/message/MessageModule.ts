/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { TranslateModule } from '@ngx-translate/core';
import { EventMessageComponent } from './EventMessage/EventMessageComponent';
import { MessageComponent } from './MessageComponent';

/**
 * This module provides the se-message component, which is responsible for rendering contextual
 * feedback messages for the user actions.
 */
@NgModule({
    imports: [CommonModule, TranslateModule.forChild()],
    declarations: [MessageComponent, EventMessageComponent],
    entryComponents: [MessageComponent, EventMessageComponent],
    exports: [MessageComponent, EventMessageComponent]
})
export class MessageModule {}
