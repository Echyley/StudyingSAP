/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['appToBundle'],
        config: function(data, conf) {
            return {
                appToBundle: {
                    // NOTE: there is also a concat task for the bundle
                    files: [
                        {
                            force: true,
                            overwrite: true,
                            src: 'web/webroot/static-resources',
                            dest:
                                global.smartedit.bundlePaths.bundleRoot +
                                '/webroot/static-resources'
                        },
                        {
                            expand: true,
                            flatten: true,
                            force: true,
                            overwrite: true,
                            src: ['resources/localization/smartedit-locales_en.properties'],
                            dest: global.smartedit.bundlePaths.bundleRoot + '/localization'
                        }
                    ]
                }
            };
        }
    };
};
