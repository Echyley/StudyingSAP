/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Install a git pre-commit hook when a developer does grunt generate.
 */
const fs = require('fs');
const path = require('path');

// Git directory.
const dir = path.join(__dirname, '../../../.git');
const file = path.join(dir, 'hooks/pre-commit');

if (!fs.existsSync(dir)) {
    console.log('Not git directory found. Skipping.');
    return;
}

const contents = `
    #!/bin/sh
    pretty-quick --staged
`;

fs.writeFileSync(file, contents);
fs.chmodSync(file, 0o755);
