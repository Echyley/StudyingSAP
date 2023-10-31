'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var angular = require('@angular/core');

function _interopNamespace(e) {
    if (e && e.__esModule) return e;
    var n = Object.create(null);
    if (e) {
        Object.keys(e).forEach(function (k) {
            if (k !== 'default') {
                var d = Object.getOwnPropertyDescriptor(e, k);
                Object.defineProperty(n, k, d.get ? d : {
                    enumerable: true,
                    get: function () { return e[k]; }
                });
            }
        });
    }
    n["default"] = e;
    return Object.freeze(n);
}

var angular__namespace = /*#__PURE__*/_interopNamespace(angular);

/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
class TestRelative {
    constructor() {
        console.log(angular__namespace.VERSION);
    }
}

exports.TestRelative = TestRelative;
