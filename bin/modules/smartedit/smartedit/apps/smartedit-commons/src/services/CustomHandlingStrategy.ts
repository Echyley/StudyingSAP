/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { UrlHandlingStrategy, UrlTree } from '@angular/router/router';

// DO NOT DELETE this as we need this for angular routing service
export class CustomHandlingStrategy implements UrlHandlingStrategy {
    // we shall process all types of path to keep consistence with old routing strategy
    shouldProcessUrl(url: UrlTree): boolean {
        return true;
    }
    extract(url: UrlTree): UrlTree {
        return url;
    }
    merge(newUrlPart: UrlTree, rawUrl: UrlTree): UrlTree {
        return newUrlPart;
    }
}
