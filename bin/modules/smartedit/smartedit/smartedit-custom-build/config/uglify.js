/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['webApplicationInjector', 'uglifyThirdparties'],
        config: function(data, conf) {
            var paths = require('../paths');

            return {
                uglifyThirdparties: {
                    files: {
                        'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.min.js': [
                            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js'
                        ],
                        'node_modules/crypto-js/crypto-js.min.js': [
                            'node_modules/crypto-js/crypto-js.js'
                        ],
                        'node_modules/reflect-metadata/Reflect.min.js': [
                            'node_modules/reflect-metadata/Reflect.js'
                        ],
                        'node_modules/angular-mocks/angular-mocks.min.js': [
                            'node_modules/angular-mocks/angular-mocks.js'
                        ],
                        'node_modules/intersection-observer/intersection-observer.min.js': [
                            'node_modules/intersection-observer/intersection-observer.js'
                        ]
                    },
                    options: {
                        mangle: true
                    }
                }
            };
        }
    };
};
