/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {coreAnnotationsHelper} from "./coreAnnotationsHelper";

beforeEach(() => {
    jasmine.clock().uninstall();
    coreAnnotationsHelper.init();
    jasmine.addMatchers({
        toBeEmptyFunction: function () {
            return {
                compare: function (actual) {
                    //FIXME: find a way to reuse the actual isFunctionEmpty from functionsModule
                    var passed =
                        typeof actual === 'function' &&
                        (actual
                                .toString()
                                .match(/\{([\s\S]*)\}/m)[1]
                                .trim() === '' ||
                            /(proxyFunction|cov_)/g.test(actual.toString().replace(/\s/g, '')));

                    return {
                        pass: passed,
                        message:
                            'Expected ' +
                            actual +
                            (passed ? '' : ' not') +
                            ' to be an empty function'
                    };
                }
            };
        }
    })
});
afterEach(() => {
    jasmine.clock().uninstall();
});
