<h1 align="center">SmartEdit Master</h1>
The smartedit-master library links all the libraries (as defined in the package.json) in the SmartEdit ecosystem and compiles them into JS and CSS files that can be loaded by the browser. 
	
## Prerequisites

To execute the smartedit-master library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the smartedit-master will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit-master library folder. The smartedit-master library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Commands

The following list describes the commands that can be executed in the smartedit-master library:

-   build:

    This command can be used to build the smartedit-master library. It builds both, the smartedit inner and smartedit container files. The output is placed in the `dist/` folder and then copied over to the `web/webroot/static-resources/dist` folder, from where it can be consumed by the browser. Note that the files produced are minified and ready to be used in production.

    ```bash
    npm run build
    ```

-   build dev:

    This command can be used to build the smartedit-master library. It builds both, the smartedit inner and smartedit container files. The output is placed in the `dist/` folder and then copied over to the `web/webroot/static-resources/dist` folder, from where it can be consumed by the browser. Note that the files produced are not minified and NOT RECOMMENDED TO BE USED IN PRODUCTION.

    ```bash
    npm run build:dev
    ```

-   Build Inner Prod

    This command can be used to build the smartedit inner files in the smartedit-master library. The output is placed in the `dist/` folder and then copied over to the `web/webroot/static-resources/dist` folder, from where it can be consumed by the browser. Note that the files produced are minified.

    ```bash
    npm run build-inner:prod
    ```

-   Build Inner Dev

    This command can be used to build the smartedit inner files in the smartedit-master library. The output is placed in the `dist/` folder and then copied over to the `web/webroot/static-resources/dist` folder, from where it can be consumed by the browser. Note that the files produced are not minified.

    ```bash
    npm run build-inner:dev
    ```

-   Build Container Prod

    This command can be used to build the smartedit container files in the smartedit-master library. The output is placed in the `dist/` folder and then copied over to the `web/webroot/static-resources/dist` folder, from where it can be consumed by the browser. Note that the files produced are minified.

    ```bash
    npm run build-container:prod
    ```

-   Build Container Dev
    This command can be used to build the smartedit container files in the smartedit-master library. The output is placed in the `dist/` folder and then copied over to the `web/webroot/static-resources/dist` folder, from where it can be consumed by the browser. Note that the files produced are not minified.
    ```bash
    npm run build-container:dev
    ```
