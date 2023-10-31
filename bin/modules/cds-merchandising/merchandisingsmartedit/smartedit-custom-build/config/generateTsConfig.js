/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {

    return {
        config: function(data, conf) {
            const lodash = require('lodash');

            const merchandisingsmarteditPaths = {
                "merchandisingsmartedit/*": ["web/features/merchandisingsmartedit/*"],
                "merchandisingsmarteditcommons": ["web/features/merchandisingsmarteditcommons"],
                "merchandisingsmarteditcommons*": ["web/features/merchandisingsmarteditcommons*"]
            };

            const yssmarteditmoduleContainerPaths = {
                "merchandisingsmarteditcontainer/*": ["web/features/merchandisingsmarteditContainer/*"],
                "merchandisingsmarteditcommons": ["web/features/merchandisingsmarteditcommons"],
                "merchandisingsmarteditcommons*": ["web/features/merchandisingsmarteditcommons*"]
            };

            const commonsInclude = ["../../jsTarget/web/features/merchandisingsmarteditcommons/**/*"];
            const smarteditInclude = commonsInclude.concat("../../jsTarget/web/features/merchandisingsmartedit/**/*");
            const smarteditContainerInclude = commonsInclude.concat("../../jsTarget/web/features/merchandisingsmarteditContainer/**/*");

            // PROD
            conf.generateProdSmarteditTsConfig.data.include = smarteditInclude;
            conf.generateProdSmarteditContainerTsConfig.data.include = smarteditContainerInclude;

            // DEV
            conf.generateDevSmarteditTsConfig.data.include = smarteditInclude;
            conf.generateDevSmarteditContainerTsConfig.data.include = smarteditContainerInclude;

            // KARMA
            conf.generateKarmaSmarteditTsConfig.data.include = smarteditInclude.concat(["../../jsTests/tests/merchandisingsmartedit/unit/**/*"]);
            conf.generateKarmaSmarteditContainerTsConfig.data.include = smarteditContainerInclude.concat(["../../jsTests/tests/merchandisingsmarteditcontainer/unit/**/*"]);

            // IDE
            conf.generateIDETsConfig.data.include = conf.generateIDETsConfig.data.include.concat(["../../jsTests/tests/**/unit/**/*"]);

            function addYsmarteditmodulePaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(merchandisingsmarteditPaths));
            }

            function addYsmarteditmoduleContainerPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(yssmarteditmoduleContainerPaths));
            }

            // PROD
            addYsmarteditmodulePaths(conf.generateProdSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);

            // DEV
            addYsmarteditmodulePaths(conf.generateDevSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);

            // KARMA
            addYsmarteditmodulePaths(conf.generateKarmaSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);

            // IDE
            addYsmarteditmodulePaths(conf.generateIDETsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateIDETsConfig.data);

            conf.generateDevSmarteditTsConfig.data.compilerOptions.emitDecoratorMetadata = true;
            conf.generateDevSmarteditContainerTsConfig.data.compilerOptions.emitDecoratorMetadata = true;
            conf.generateProdSmarteditTsConfig.data.compilerOptions.emitDecoratorMetadata = true;
            conf.generateProdSmarteditContainerTsConfig.data.compilerOptions.emitDecoratorMetadata = true;
            return conf;
        }
    };

};
