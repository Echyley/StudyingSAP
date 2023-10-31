/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = function() {
    return {
        targets: [
            'prodSmarteditCommons',
            'devSmarteditCommons',
            'libSmarteditCommons',
            'angularStorefront',
            'devVendor',
            'prodVendor'
        ],
        config: function(data, conf) {
            const bundlePaths = require('../../smartedit-build/bundlePaths');
            const customPaths = require('../paths');
            const { resolve } = require('path');

            return {
                ...conf,
                prodSmarteditCommons: () =>
                    require(resolve(customPaths.webpack.prodSmarteditCommons)),
                devSmarteditCommons: () =>
                    require(resolve(customPaths.webpack.devSmarteditCommons)),
                libSmarteditCommons: () =>
                    require(resolve(customPaths.webpack.libSmarteditCommons)),
                angularStorefront: () =>
                    require(resolve(bundlePaths.external.webpack.angularStorefront)),
                devVendor: () => require(resolve(customPaths.webpack.devVendorChunk)),
                prodVendor: () => require(resolve(customPaths.webpack.prodVendorChunk))
            };
        }
    };
};
