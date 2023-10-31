/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import CustomMatcherFactories = jasmine.CustomMatcherFactories;
import CustomMatcher = jasmine.CustomMatcher;
import CustomMatcherResult = jasmine.CustomMatcherResult;

export const matchers: CustomMatcherFactories = {
    toJsonEqual(): CustomMatcher {
        return {
            compare(actual: any, expected: any): CustomMatcherResult {
                if (JSON.stringify(actual) === JSON.stringify(expected)) {
                    return {
                        pass: true,
                        message: `Actual equals expected`
                    };
                } else {
                    return {
                        pass: false,
                        message: `Actual: ${JSON.stringify(actual)}; \n Expected ${JSON.stringify(
                            expected
                        )}`
                    };
                }
            }
        };
    }
};
