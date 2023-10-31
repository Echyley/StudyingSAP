import 'testhelpers';
import 'smarteditcommons';
import 'smartedit';

function importAll(requireContext: any): void {
    requireContext.keys().forEach(function (key: string) {
        requireContext(key);
    });
}
importAll(require.context('./features', true, /Test\.(js|ts)$/));
importAll(require.context('../src', true, /Module\.ts$/));
