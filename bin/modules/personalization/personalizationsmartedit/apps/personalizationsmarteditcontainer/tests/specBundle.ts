import 'testhelpers';
import 'smarteditcontainer';

function importAll(requireContext: any): any {
    requireContext.keys().forEach(function (key: string) {
        requireContext(key);
    });
}
importAll(require.context('./features', true, /Test\.(js|ts)$/));
importAll(require.context('../src', true, /Module\.ts$/));
