/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['smartEdit', 'smartEditContainer', 'build', 'configurationBuilders'],
        config: function(data, conf) {
            var paths = require('../paths');

            return {
                options: {
                    dest: 'jsTarget/docs',
                    title: 'SmartEdit API',
                    startPage: '/smartEdit'
                },
                smartEdit: {
                    api: true,
                    src: paths.ngdocs.smartedit,
                    title: 'SmartEdit'
                },
                smartEditContainer: {
                    api: true,
                    src: paths.ngdocs.smarteditcontainer,
                    title: 'SmartEdit Container'
                },
                build: {
                    title: 'SmartEdit Builder',
                    src: ['smartedit-build/config/**/*.+(js|ts|json)']
                },
                configurationBuilders: {
                    api: true,
                    startPage: '#/configurationBuilders/ConfigurationBuilder',
                    title: 'SmartEdit Configration Builders',
                    src: ['smartedit-build/builders/**/*.js']
                }
            };
        }
    };
};
