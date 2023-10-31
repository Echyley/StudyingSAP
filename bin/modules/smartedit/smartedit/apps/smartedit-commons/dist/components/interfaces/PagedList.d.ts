import { IDropdownMenuItem } from 'smarteditcommons/components/dropdown/dropdownMenu';
/**
 * Base PagedList interface for {@link ClientPagedListComponent} and {@link DynamicPagedListComponent}.
 */
export interface PagedList {
    /**
     * If set to true, the size of the filtered collection will be displayed.
     */
    displayCount: boolean;
    /**
     * If provided, the DropdownMenu will be displayed.
     */
    dropdownItems?: IDropdownMenuItem[];
    itemsPerPage: number;
    /**
     * If set to true, the list will be sorted descending.
     */
    reversed: boolean;
    sortBy: string;
}
