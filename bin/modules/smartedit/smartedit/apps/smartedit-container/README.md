<h1 align="center">SmartEdit Container</h1>
The smartedit-container is an Angular application that contains and loads the SmartEdit application and a website into an iframe.

## Prerequisites

To execute the smartedit-container library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the smartedit-container will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit-container library folder. The smartedit-container library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The smartedit-container library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the library is ready to be consumed by the [smartedit-master](../smartedit-master) or other libraries in SmartEdit.

> 📘 Note
>
> The smartedit-container code will be referenced dynamically in the ../smartedit-master/container/index_master.ts file in the smartedit-master library.

## Running Unit Tests

The following command can be used to run the smartedit-container unit tests in the background:

```bash
npm run test
```

It is also possible to run unit tests in a browser window. This is useful when debugging tests (it is possible to set break-points). To do so, execute the following command:

```bash
npm run test:dev
```

## Generating documentation

The following command can be used to generate the smartedit-container documentation:

```bash
npm run docs
```
