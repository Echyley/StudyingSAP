/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function(grunt) {
    return {
        targets: ['unitSmarteditcommons'],
        config: function(data, conf) {
            const customPaths = require('../paths');
            const bundlePaths = require('../../smartedit-build/bundlePaths');

            const { coverage, headless } = require('../../smartedit-build/builders/karma');

            conf.unitSmarteditcommons = {
                options: headless(!grunt.option('browser'))({
                    configFile: bundlePaths.external.karma.smarteditCommons
                })
            };

            if (grunt.option('coverage')) {
                conf.unitSmartedit.options = coverage(
                    customPaths.coverage.dir,
                    customPaths.coverage.smarteditDirName
                )(conf.unitSmartedit.options);
                conf.unitSmarteditContainer.options = coverage(
                    customPaths.coverage.dir,
                    customPaths.coverage.smarteditcontainerDirName
                )(conf.unitSmartedit.options);
                conf.unitSmarteditcommons.options = coverage(
                    customPaths.coverage.dir,
                    customPaths.coverage.smarteditcommonsDirName
                )(conf.unitSmartedit.options);
            }

            return conf;
        }
    };
};
