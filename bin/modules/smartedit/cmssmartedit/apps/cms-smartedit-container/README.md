<h1 align="center">CMS SmartEdit Container</h1>
The cmssmarteditcontainer is an Angular application that loads and extends the SmartEdit Container Application. It adds CMS centric functionality.

## Prerequisites

To execute the cmssmarteditcontainer library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the cmssmarteditcontainer will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the cmssmarteditcontainer library folder. The cmssmarteditcontainer library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The cmssmarteditcontainer library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the library is ready to be consumed by the [smartedit-master](../smartedit-master).

## Running Unit Tests

The following command can be used to run the cmssmarteditcontainer unit tests in the background:

```bash
npm run test
```

It is also possible to run unit tests in a browser window. This is useful when debugging tests (it is possible to set break-points). To do so, execute the following command:

```bash
npm run test:dev
```

## Generating documentation

The following command can be used to generate the cmssmarteditcontainer documentation:

```bash
npm run docs
```
