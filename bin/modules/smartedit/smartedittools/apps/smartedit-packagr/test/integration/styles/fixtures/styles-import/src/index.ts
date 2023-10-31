/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './style1.scss';

// This is to ensure that the second style is always imported after.
import './file2';

export class StylesImportTest {
    constructor() {}
}
