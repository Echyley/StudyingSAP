import { ModalManagerService, IBaseCatalog } from 'smarteditcommons';
import './CatalogHierarchyModalComponent.scss';
export declare class CatalogHierarchyModalComponent {
    modalService: ModalManagerService;
    catalogs$: Promise<IBaseCatalog[]>;
    siteId: string;
    constructor(modalService: ModalManagerService);
    ngOnInit(): void;
    onSiteSelected(): void;
}
