import { GenericEditorPredicate, GenericEditorStructure, GenericEditorTab, GenericEditorTabConfiguration } from '../types';
/**
 * The genericEditorTabService is used to configure the way in which the tabs in the
 * {@link GenericEditorComponent} component are rendered.
 */
export declare class GenericEditorTabService {
    private static readonly MIN_PRIORITY;
    private static readonly DEFAULT_TAB_ID;
    private _tabsConfiguration;
    private _defaultTabPredicates;
    /**
     * This method stores the configuration of the tab identified by the provided ID.
     *
     * @param tabId The ID of the tab to configure.
     * @param tabConfiguration The object containing the configuration of the tab.
     * @param tabConfiguration.priority The priority of the tab. Higher numbers represent higher priority. This property is used to
     * sort tabs.
     *
     */
    configureTab(tabId: string, tabConfiguration: GenericEditorTabConfiguration): void;
    /**
     * This method retrieves the configuration of a tab.
     *
     * @param tabId The ID of the tab for which to retrieve its configuration.
     * @returns The configuration of the tab. Can be null if no tab with the provided ID has been configured.
     *
     */
    getTabConfiguration(tabId: string): GenericEditorTabConfiguration;
    /**
     * This method sorts in place the list of tabs provided. Sorting starts with tab priority. If two or more tabs have the same priority they
     * will be sorted alphabetically by ID.
     *
     * @param tabsToSort The list of tabs to sort.
     */
    sortTabs(tabsToSort: GenericEditorTab[]): GenericEditorTab[];
    getComponentTypeDefaultTab(componentTypeStructure: GenericEditorStructure): string;
    addComponentTypeDefaultTabPredicate(predicate: GenericEditorPredicate): void;
    private getTabPriority;
}
