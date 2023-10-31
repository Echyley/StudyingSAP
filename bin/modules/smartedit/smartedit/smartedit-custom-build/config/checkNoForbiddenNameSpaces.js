/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    var lodash = require('lodash');

    return {
        config: function(data, _conf) {
            const conf = lodash.cloneDeep(_conf);

            conf.mappings.forEach((mapping) => {
                mapping.level = 'FATAL';
                if (mapping.namespaces !== '*' && 'REGEXP:@smart/utils' in mapping.namespaces) {
                    mapping.patterns.push('!web/app/common/**/*.ts');
                    mapping.patterns.push('!web/app/common/*/*.ts');
                }
                mapping.patterns = mapping.patterns.map((pattern) => {
                    return pattern.replace('web/features', 'web/app').replace('jsTests/', 'tests/');
                });
            });
            return conf;
        }
    };
};
