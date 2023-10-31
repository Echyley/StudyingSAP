/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { SMARTEDITLOADER_COMPONENT_NAME } from 'smarteditcommons';

const legacyLoaderTagName = 'legacy-loader';

@Component({
    selector: SMARTEDITLOADER_COMPONENT_NAME,
    template: `<${legacyLoaderTagName}></${legacyLoaderTagName}>`
})
export class SmarteditloaderComponent {}
