/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['dev'],
        config: function(data, conf) {
            const customPaths = require('../paths');

            return {
                dev: {
                    files: [
                        {
                            expand: true,
                            cwd: `${customPaths.web.smartEdit.styling.dir}/inner`,
                            src: 'styling.less',
                            dest: `${customPaths.web.smartEdit.styling.dir}/inner`,
                            ext: '.css'
                        },
                        {
                            expand: true,
                            cwd: `${customPaths.web.smartEdit.styling.dir}/outer`,
                            src: 'styling.less',
                            dest: `${customPaths.web.smartEdit.styling.dir}/outer`,
                            ext: '.css'
                        }
                    ]
                }
            };
        }
    };
};
