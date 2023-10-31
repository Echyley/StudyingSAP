/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as lodash from 'lodash';
import { TruncatedText } from '../../dtos/TruncatedText';

/**
 * Service containing truncate string functions.
 * @internal
 */
export class TextTruncateService {
    /**
     * Truncates text to the nearest word depending on character length.
     * Truncates below character length.
     *
     * @param limit index in text to truncate to
     */
    public truncateToNearestWord(limit: number, text: string, ellipsis = ''): TruncatedText {
        if (lodash.isNil(text) || limit > text.length) {
            return new TruncatedText(text, text, false);
        }
        const regexp = /(\s)/g;
        const truncatedGroups: RegExpMatchArray = text.match(regexp);

        let truncateIndex = 0;
        if (!truncatedGroups) {
            truncateIndex = limit;
        } else {
            for (let i = 0; i < truncatedGroups.length; i++) {
                const nextPosition: number = this.getPositionOfCharacters(
                    text,
                    truncatedGroups[i],
                    i + 1
                );
                if (nextPosition > limit) {
                    break;
                }
                truncateIndex = nextPosition;
            }
        }
        const truncated: string = text.substr(0, truncateIndex);
        return new TruncatedText(text, truncated, true, ellipsis);
    }

    private getPositionOfCharacters(
        searchString: string,
        characters: string,
        index: number
    ): number {
        return searchString.split(characters, index).join(characters).length;
    }
}
