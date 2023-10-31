import 'testhelpers';
import 'smarteditcommons';

function importAll(requireContext: __WebpackModuleApi.RequireContext): void {
    requireContext.keys().forEach(function (key: string) {
        requireContext(key);
    });
}
importAll(require.context('./features', true, /Test\.(js|ts)$/));
importAll(require.context('../src', true, /Module\.ts$/));
