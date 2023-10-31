/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FormModule as FundamentalFormModule } from '@fundamental-ngx/core/form';
import { TranslationModule } from '@smart/utils';

import { SelectModule } from '../../../components/select/SelectModule';
import { BooleanModule } from './BooleanComponent';
import { DateTimePickerModule } from './DateTimePickerComponent';
import {
    DropdownComponent,
    DropdownItemPrinterComponent
} from './DropdownComponent/DropdownComponent';
import { EditableDropdownModule } from './EditableDropdownComponent';
import { EmailModule } from './EmailComponent';
import { EnumComponent, EnumItemPrinterComponent } from './EnumComponent/EnumComponent';
import { FloatModule } from './FloatComponent';
import { LongStringComponent } from './LongStringComponent/LongStringComponent';
import { NumberModule } from './NumberComponent';
import { RichTextFieldModule } from './RichTextField';
import { ShortStringComponent } from './ShortString/ShortStringComponent';
import { TextComponent } from './TextComponent/TextComponent';

@NgModule({
    imports: [
        CommonModule,
        DateTimePickerModule,
        BooleanModule,
        TranslationModule.forChild(),
        FormsModule,
        RichTextFieldModule,
        SelectModule,
        FloatModule,
        NumberModule,
        EmailModule,
        EditableDropdownModule,
        FundamentalFormModule
    ],
    declarations: [
        ShortStringComponent,
        LongStringComponent,
        EnumComponent,
        EnumItemPrinterComponent,
        DropdownComponent,
        DropdownItemPrinterComponent,
        TextComponent
    ],
    entryComponents: [
        ShortStringComponent,
        LongStringComponent,
        EnumComponent,
        EnumItemPrinterComponent,
        DropdownComponent,
        DropdownItemPrinterComponent,
        TextComponent
    ]
})
export class GenericEditorWidgetModule {}
