<h1 align="center">SmartEdit Loader</h1>
The smartedit-loader library contains a short-lived Angular web application that configures and loads the [SmartEdit container](../smartedit-container).

## Prerequisites

To execute the smartedit-loader library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the smartedit-loader will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit-loader library folder. The smartedit-loader library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The smartedit-loader library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the library is ready to be consumed by the [smartedit-master](../smartedit-master).
