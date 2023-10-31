/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['tslintRules', 'tsInjectables'],
        config: function(data, conf) {
            var paths = require('../paths');
            return {
                options: {},
                tslintRules: {
                    src: global.smartedit.bundlePaths.bundleRoot + '/config/tslint/rules/*.ts',
                    dest:
                        global.smartedit.bundlePaths.bundleRoot + '/config/tslint/rules/generated/'
                },
                tsInjectables: paths.tools.seInjectableInstrumenter
            };
        }
    };
};
