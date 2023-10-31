/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['lib', 'webroot', 'e2e'],
        config: function(data, conf) {
            var paths = require('../paths');

            conf.lib = {
                src: [global.smartedit.bundlePaths.libPath, paths.smarteditcommons.lib]
            };

            conf.e2e = {
                src: ['jsTarget/' + paths.tests.allE2eTSMocks]
            };
            conf.webroot = {
                src: [paths.web.webroot.staticResources.smartEdit.css.all]
            };

            /**
             * TODO - remove this target after 6.6 CF
             * This issue will not be exposed to people using the commerce suite, or the pipeline.
             * Only people that had a smartedit-extension dev setup and did a rebase.
             * In this case the symlinking fails due to existing directories.
             * This clean will fix the problem so that symlinking willwork again. So this should
             * be a 1 time execution fix.
             */
            conf.bundleForNewSymlinks = {
                src: [
                    global.smartedit.bundlePaths.bundleRoot + '/@types',
                    global.smartedit.bundlePaths.bundleRoot + '/localization',
                    global.smartedit.bundlePaths.bundleRoot + '/webroot/**/*'
                ]
            };

            return conf;
        }
    };
};
