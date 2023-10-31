/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['all'],
        config: function(data, conf) {
            var paths = require('../paths');
            conf.all = paths.jshint;
            return conf;
        }
    };
};
