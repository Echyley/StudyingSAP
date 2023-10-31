/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {

    return {
        targets: [
            'merchandisingsmartedit',
            'merchandisingsmarteditContainer',
            'styling'
        ],
        config: function(data, baseConf) {
            const targetBase = "jsTarget/web/features/";

            function generateConfigFromFolderName(folderName) {
                return {
                    src: [
                        targetBase + "merchandisingsmarteditcommons/**/*.js",
                        targetBase + folderName + '/**/*.js'
                    ],
                    dest: targetBase + folderName + '/' + folderName + '_bundle.js'
                };
            }

            baseConf.merchandisingsmartedit = generateConfigFromFolderName("merchandisingsmartedit");
            baseConf.merchandisingsmarteditContainer = generateConfigFromFolderName("merchandisingsmarteditContainer");

            baseConf.styling = {
                separator: ';',
                src: ["web/webroot/css/*.css"],
                dest: "web/webroot/css/style.css",
            };

            return baseConf;
        }
    };

};
