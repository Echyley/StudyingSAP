# Merchandisingaddon

**NOTE:**
Build mechanism in mechandisingaddon is disabled in buildcallbacks.xml
Compiled and minified file 'mechandisingaddon.js' is committed to repository.
There is no need to build this project on any pipeline.

## Building locally

1. If you did not do it before, you have to run the rush update command at least once before building:

`ant rushupdate -Dpath=path_to_merchandisingaddon` 
   
example: `ant rushupdate -Dpath=/Users/i324253/yenvs/merch/sources/merchandising-module/merchandisingaddon/`

After that you can use ant or npm to run build:

npm build example:

2. Go to directory `acceleratoraddon/web/features`
3. Run command `npm run build`

ant build example:

2. `ant rushbuild -Dpath=path_to_merchandisingaddon`

`ant rushbuild -Dpath=/Users/i324253/yenvs/merch/sources/merchandising-module/merchandisingaddon/`

ant rebuild example:

2. `ant rushrebuild -Dpath=path_to_merchandisingaddon`

 `ant rushrebuild -Dpath=/Users/i324253/yenvs/merch/sources/merchandising-module/merchandisingaddon/`


## Testing locally

1. Go to directory `acceleratoraddon/web/features`
2. Run command `npm run test`

## Cleaning

To run rush purge:
`ant clean`

To run rush purge, delete pnpm store, rush home folder and npm cache, run:
`ant clean && rm -rf ~/.pnpm-store && rm -rf ~/.rush/ && npm cache clear --force`


## Useful commands

Update rush.json based on smartedittools rush.json.tpl

`node updateRushConfig.js {ext.merchandisingaddon.path} {ext.smartedittools.path}/common/config/rush.tpl.json`

`node updateRushConfig.js /Users/i324253/yenvs/merch/sources/merchandising-module/merchandisingaddon/ /Users/i324253/yenvs/merch/workspace/smartedittools/common/config/rush.tpl.json`