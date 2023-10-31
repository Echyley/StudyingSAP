/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { SeEntryModule } from "smarteditcommons";
import "../../styling/style.less";

@SeEntryModule("personalizationsearchsmartedit")
@NgModule({
  imports: [BrowserModule],
  declarations: [],
  entryComponents: [],
  exports: [],
  providers: [],
})
export class PersonalizationsearchSmarteditModule {}
