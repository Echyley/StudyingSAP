# Offline (Win/*Unix) Npm Ancillary Module Deployment

**Rush:**

**Online mode - Steps to to create the node_modules .cache folder with verdaccio:**

From smartedittools folder run the command:

`ant clean && rm -rf ~/.pnpm-store && rm -rf ~/.rush/ && npm cache clear --force`

From npm-ancilary folder run the command:

`rm -rf ./resources/npm/verdaccio/.cache`

Start verdaccio in online mode:

`./resources/npm/verdaccio/start-verdaccio.sh ./resources/npm/verdaccio/config.yaml true`

The folder ./resources/npm/verdaccio/.cache will be re-created automatically.

Open chrome and check that verdaccio is up and running: [http://localhost:4873/](http://localhost:4873/)

# In a terminal, watch the verdaccio log with the command:

`tail -f ./resources/npm/verdaccio/verdaccio.log`

From smartedittools folder run the command:

`ant rushupdate -Dpath=./`

Stop the verdaccio process:

`./resources/npm/verdaccio/stop-verdaccio.sh`

**Result:**

Al node_modules were downloaded in the verdaccio .cache folder under ./resources/npm/verdaccio/.cache folder.

At this point, the online mode steps are completed, the .cache folder can be pushed to offline-ancillary module.

See next section “**Offline mode**” for testing in offline mode.

**Offline mode:**

Disable internet connection on laptop

Start verdaccio in offline mode:

From npm-ancilary folder run the command:

`./resources/npm/verdaccio/start-verdaccio.sh ./resources/npm/verdaccio/config_offline.yaml true`

# In a terminal, watch the verdaccio log with the command:

`tail -f ./resources/npm/verdaccio/verdaccio.log`

From smartedittools folder run the command:

`ant clean && rm -rf ~/.pnpm-store && rm -rf ~/.rush/ && npm cache clear --force`

`ant rushupdate -Dpath=./`

**—> Result: BUILD SUCCESSFUL**

During rush update, you should see in verdaccio logs that all node modules are downloaded from verdaccio localhost instead of the public npm repository.
