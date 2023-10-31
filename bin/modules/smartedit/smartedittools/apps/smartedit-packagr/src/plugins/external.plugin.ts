/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as rollup from 'rollup';

/**
 * Resolves external dependencies, such as third-party libraries defined in package.json
 * (by default these are dependencies and peerDependencies).
 * When an external dependency is found, this plugin marks it like that to avoid Rollup from adding it
 * in the bundle.
 *
 * Notes:
 * - Allows exact and partial matching (e.g., rxjs/operators).
 *
 * @param externals The list of external dependencies read from package.json.
 *
 */
export const external = (externals: string[]): rollup.Plugin => {
    // Used for exact matches
    const dependencies = new Set(externals);

    const partialDependencies = new Set();
    const dependenciesExcluded = new Set();

    const isRelativeDependency = (id: string): boolean => {
        return id.charAt(0) === '.';
    };

    const isExcluded = (id: string): boolean => {
        return dependenciesExcluded.has(id);
    };

    const isMatch = (id: string): boolean => {
        return dependencies.has(id) || partialDependencies.has(id);
    };

    const isPartialMatch = (id: string): boolean => {
        // Fastest way to iterate (https://jsperf.com/for-vs-foreach/75)
        for (let i = 0; i < dependencies.size; i++) {
            if (id.startsWith(externals[i])) {
                return true;
            }
        }

        return false;
    };

    return {
        name: 'external',

        /*
            This hook defines the custom resolver to use.

            Check the following link for more information: 
            - https://rollupjs.org/guide/en/#resolveid
        */
        resolveId(id: string) {
            if (isRelativeDependency(id) || isExcluded(id)) {
                // This resolver doesn't know how to handle this dependency. Let others handle it.
                return null;
            }

            if (isMatch(id)) {
                // This is a known dependency (exact given dependency or previously marked as a partial dependency).
                // Mark as external.
                return { id, external: true };
            }

            if (isPartialMatch(id)) {
                // There was no exact match. Mark it as a partial dependency to speed up the search in the future.
                // and mark as external.
                partialDependencies.add(id);
                return { id, external: true };
            }

            // Didn't match any known dependencies. Mark it as excluded dependency to avoid
            // wasting time looking for it.
            dependenciesExcluded.add(id);

            // Let other resolvers handle it.
            return null;
        }
    };
};
