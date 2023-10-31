/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    // TODO: remove this file once smartedit e2e tests are aligned (/test folder renamed to /jsTests).
    return {
        config: function(data, conf) {
            conf.root = 'test/e2e';
            conf.dest = 'test/e2e/list.html';
            return conf;
        }
    };
};
