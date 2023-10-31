import { Page } from 'smarteditcommons';
export interface ActionsForContainerPage extends Page<ActionDetailsResponseDto> {
    actions: ActionDetailsResponseDto[];
}
export interface ActionDetailsResponseDto {
    type: string;
    actionCatalog: string;
    actionCatalogVersion: string;
    actionCode: string;
    actionRank: number;
    customizationCode: string;
    customizationName: string;
    customizationRank: number;
    customizationStatus: string;
    variationCode: string;
    variationName: string;
    variationRank: number;
    variationStatus: string;
}
