/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    var removeSourceMapping = function(src, filepath) {
        var regEx = new RegExp(/^\/\/#\ssourceMappingURL=.+/, 'm');
        return src.replace(regEx, '');
    };

    return {
        targets: [
            'smarteditThirdparties',
            'smarteditThirdpartiesDev',
            'containerThirdpartiesDev',
            'containerThirdparties',
            'commonTypes',
            'smarteditTypes',
            'smarteditcontainerTypes'
        ],
        config: function(data, baseConf) {
            var paths = require('../paths');

            baseConf.smarteditThirdparties = {
                src: paths.getSmarteditThirdpartiesFiles(),
                options: {
                    process: removeSourceMapping
                },
                dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/js/prelibraries.js'
            };

            baseConf.smarteditThirdpartiesDev = {
                src: paths.getSmarteditThirdpartiesDevFiles(),
                dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/js/prelibraries.js'
            };

            baseConf.containerThirdpartiesDev = {
                src: paths.getContainerThirdpartiesDevFiles(),

                dest: 'web/webroot/static-resources/dist/smarteditcontainer/js/thirdparties.js'
            };

            baseConf.containerThirdparties = {
                src: paths.containerThirdpartiesFiles(),
                options: {
                    process: removeSourceMapping
                },
                dest: 'web/webroot/static-resources/dist/smarteditcontainer/js/thirdparties.js'
            };

            baseConf.smarteditTypes = {
                flatten: true,
                src: ['temp/types/smartedit/**/*.d.ts'],
                dest: global.smartedit.bundlePaths.bundleRoot + '/@types/smartedit/index.d.ts'
            };

            baseConf.smarteditcontainerTypes = {
                flatten: true,
                src: ['temp/types/smarteditcontainer/**/*.d.ts'],
                dest:
                    global.smartedit.bundlePaths.bundleRoot +
                    '/@types/smarteditcontainer/index.d.ts'
            };
            return baseConf;
        }
    };
};
