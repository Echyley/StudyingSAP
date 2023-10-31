const {
    resolve
} = require('path');
const base = require('smartedit-build/config/webpack/webpack.ext.karma.smartedit.config.js');
const {
    compose,
    webpack: {
        alias, karma
    }
} = require('smartedit-build/builders');
module.exports = compose(
    karma(),
    alias('personalizationsearchsmartedit', resolve('src'))
)(base);
