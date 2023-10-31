/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TypedMap } from 'smarteditcommons';

declare global {
    // const angular: typeof __angular;
    interface Window {
        test: { unit: TypedMap<TypedMap<any>> };
    }
}
