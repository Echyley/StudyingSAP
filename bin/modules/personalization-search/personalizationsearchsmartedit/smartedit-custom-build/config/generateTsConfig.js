/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/* jshint esversion: 6 */
module.exports = function() {

    return {
        config: function(data, conf) {
            const lodash = require('lodash');

            const personalizationsearchsmarteditPaths = {
                "personalizationsearchsmartedit/*": ["web/features/personalizationsearchsmartedit/*"]
            };

            const yssmarteditmoduleContainerPaths = {
                "personalizationsearchsmarteditcontainer/*": ["web/features/personalizationsearchsmarteditContainer/*"]
            };

            const innerLayerInclude = ["../../jsTarget/web/features/personalizationsearchsmartedit/**/*"];
            const outerLayerInclude = ["../../jsTarget/web/features/personalizationsearchsmarteditContainer/**/*"];
            const innerLayerTestInclude = ["../../jsTests/tests/personalizationsearchsmartedit/unit/features/**/*"];
            const outerLayerTestInclude = ["../../jsTests/tests/personalizationsearchsmarteditContainer/unit/features/**/*"];

            function addYsmarteditmodulePaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(personalizationsearchsmarteditPaths));
            }

            function addYsmarteditmoduleContainerPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(yssmarteditmoduleContainerPaths));
            }

            // PROD
            addYsmarteditmodulePaths(conf.generateProdSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);
            conf.generateProdSmarteditTsConfig.data.include = innerLayerInclude;
            conf.generateProdSmarteditContainerTsConfig.data.include = outerLayerInclude;

            // DEV
            addYsmarteditmodulePaths(conf.generateDevSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);
            conf.generateDevSmarteditTsConfig.data.include = innerLayerInclude;
            conf.generateDevSmarteditContainerTsConfig.data.include = outerLayerInclude;

            // KARMA
            addYsmarteditmodulePaths(conf.generateKarmaSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);
            conf.generateKarmaSmarteditTsConfig.data.include = innerLayerInclude.concat(innerLayerTestInclude);
            conf.generateKarmaSmarteditContainerTsConfig.data.include = outerLayerInclude.concat(outerLayerTestInclude);

            // IDE
            addYsmarteditmodulePaths(conf.generateIDETsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateIDETsConfig.data);
            conf.generateIDETsConfig.data.include = conf.generateIDETsConfig.data.include.concat(["../../jsTests/tests/**/unit/**/*"]);

            return conf;
        }
    };

};
