/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import '../styling/less/styling.less';
import '../styling/sass/styles.scss';
import { enableProdMode } from '@angular/core';

if (process.env.NODE_ENV === 'production') {
    enableProdMode();
}

export { smarteditFactory } from './smartedit';
export * from './services';
export * from './modules';
