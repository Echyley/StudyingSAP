/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const path = require('path');
const karma = require('karma');
const parseConfig = karma.config.parseConfig;
const Server = karma.Server;

parseConfig(null, require(path.join(process.cwd(), 'karma.conf.js')), {
    promiseConfig: true,
    throwErrors: true
}).then(
    (karmaConfig) => {
        const server = new Server(karmaConfig, function doneCallback(exitCode) {
            console.log('Karma has exited with ' + exitCode);
            process.exit(exitCode);
        });
        server.start();
    },
    (rejectReason) => console.log(rejectReason)
);
