/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { annotationService, functionsUtils } from '@smart/utils';
import { SeConstructor, SeFactory } from './types';

/** @internal */
export class DINameUtils {
    buildComponentName(componentConstructor: SeConstructor): string {
        return this.buildName(componentConstructor)
            .replace(/Component$/, '')
            .replace(/Directive$/, '');
    }

    buildServiceName(serviceConstructor: SeConstructor | SeFactory): string {
        return this.buildName(serviceConstructor);
    }

    buildModuleName(moduleConstructor: SeConstructor | SeFactory): string {
        return this.buildName(moduleConstructor).replace(/Module$/, '');
    }

    // builds the DI recipe name for a given construtor
    buildName(myConstructor: SeConstructor | SeFactory): string {
        const originalConstructor = annotationService.getOriginalConstructor(myConstructor);
        const originalName = functionsUtils.getConstructorName(originalConstructor);
        return this.convertNameCasing(originalName);
    }

    // converts the first character to lower case
    convertNameCasing(originalName: string): string {
        const builtName = originalName.substring(0, 1).toLowerCase() + originalName.substring(1);
        return builtName;
    }
}

export const diNameUtils = new DINameUtils();
