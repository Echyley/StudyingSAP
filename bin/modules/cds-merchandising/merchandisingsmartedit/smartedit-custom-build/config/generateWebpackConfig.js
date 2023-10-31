/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = function() {

    return {
        config: function(data, conf) {

            const path = require("path");
            const lodash = require("lodash");

            function setSmarteditProperties(conf, addEntry) {
                if (addEntry) {
                    conf.entry = {
                        'merchandisingsmartedit': './jsTarget/web/features/merchandisingsmartedit/index.ts'
                    };
                }
                //seems necessary on case sensitive OS to specify aliases in addition to paths in tsconfig
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "merchandisingsmarteditcommons": path.resolve("./jsTarget/web/features/merchandisingsmarteditcommons"),
                    "merchandisingsmartedit": path.resolve("./jsTarget/web/features/merchandisingsmartedit")
                });
            }

            function setSmarteditContainerProperties(conf, addEntry) {
                if (addEntry) { // don't add entries for any karma webpacks
                    conf.entry = {
                        'merchandisingsmarteditContainer': './jsTarget/web/features/merchandisingsmarteditContainer/index.ts'
                    };
                }
                //seems necessary on case sensitive OS to specify aliases in addition to paths in tsconfig
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "merchandisingsmarteditcommons": path.resolve("./jsTarget/web/features/merchandisingsmarteditcommons"),
                    "merchandisingsmarteditcontainer": path.resolve("./jsTarget/web/features/merchandisingsmarteditContainer")
                });
            }

            // ======== PROD ========
            setSmarteditProperties(conf.generateProdSmarteditWebpackConfig.data, true);
            setSmarteditContainerProperties(conf.generateProdSmarteditContainerWebpackConfig.data, true);


            // ======== DEV ========
            setSmarteditProperties(conf.generateDevSmarteditWebpackConfig.data, true);
            setSmarteditContainerProperties(conf.generateDevSmarteditContainerWebpackConfig.data, true);

            // ======== KARMA ========
            setSmarteditProperties(conf.generateKarmaSmarteditWebpackConfig.data, false);
            setSmarteditContainerProperties(conf.generateKarmaSmarteditContainerWebpackConfig.data, false);

            return conf;
        }
    };

};
