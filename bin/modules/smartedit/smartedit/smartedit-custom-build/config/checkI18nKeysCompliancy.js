/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        config: function(data, conf) {
            var paths = require('../paths');
            return {
                prefix: {
                    ignored: ['type.'],
                    expected: ['se.']
                },
                paths: {
                    files: paths.checkI18nKeysCompliancy,
                    properties: [paths.smartEditLocalesProperties]
                }
            };
        }
    };
};
