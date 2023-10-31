/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RestrictionsModule } from 'cmssmarteditcontainer/components/restrictions/RestrictionsModule';
import { MessageModule, TranslationModule } from 'smarteditcommons';
import { PageRestrictionsEditorComponent } from './pageRestrictionsEditor';
import { PageRestrictionsInfoMessageComponent } from './pageRestrictionsInfoMessage';

@NgModule({
    imports: [CommonModule, MessageModule, RestrictionsModule, TranslationModule.forChild()],
    declarations: [PageRestrictionsEditorComponent, PageRestrictionsInfoMessageComponent],
    entryComponents: [PageRestrictionsEditorComponent, PageRestrictionsInfoMessageComponent],
    exports: [RestrictionsModule, PageRestrictionsInfoMessageComponent]
})
export class PageRestrictionsModule {}
