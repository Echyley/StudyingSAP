const {
    resolve
} = require('path');
const base = require('%WEBPACK_PATH%');
const {
    compose,
    webpack: {
        alias, karma
    }
} = require('smartedit-build/builders');
module.exports = compose(
    karma(),
    alias('%WEBPACK_ALIAS%', resolve('src'))
)(base);
