/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SliderPanelZIndexHelper } from './SliderPanelZIndexHelper';

describe('SliderPanelZIndexHelper', () => {
    let helper: SliderPanelZIndexHelper;
    const nodeTree0 = `
        <div style="z-index: 10">
            <div style="z-index: 5"></div>
            <div style="z-index: 10">
                <div style="z-index: 15">
                    <div style="z-index: 20"></div>
                    <div style="z-index: 30"></div>
                </div>
            </div>
        </div>
    `;

    const nodeTree1 = '<div></div>';

    const nodeTree2 = `
        <div>
            <div></div>
            <div>
                <div></div>
            </div>
        </div>
    `;

    const nodeTree3 = `
        <div>
            <div style="z-index: 20"></div>
            <script style="z-index: 70"></script>
            <base style="z-index: 40" />
            <link style="z-index: 50" />
        </div>
    `;

    beforeEach(() => {
        helper = new SliderPanelZIndexHelper();
    });

    it('Returns the highest zIndex value within the node tree', () => {
        const expected = 30;
        const tempDiv0 = document.createElement('div');
        tempDiv0.innerHTML = nodeTree0;

        const result = helper.getHighestZIndex(window.smarteditJQuery(tempDiv0));

        expect(result).toBe(expected);
    });

    it('Returns 0 when the node tree is empty', () => {
        const expected = 0;
        const tempDiv1 = document.createElement('div');
        tempDiv1.innerHTML = nodeTree1;
        const result = helper.getHighestZIndex(window.smarteditJQuery(tempDiv1));

        expect(result).toBe(expected);
    });

    it('Returns 0 when the node tree elements have no specified zIndex', () => {
        const expected = 0;
        const tempDiv2 = document.createElement('div');
        tempDiv2.innerHTML = nodeTree2;
        const result = helper.getHighestZIndex(window.smarteditJQuery(tempDiv2));

        expect(result).toBe(expected);
    });

    it('Ignores the blacklisted node names', () => {
        const expected = 20;
        const tempDiv3 = document.createElement('div');
        tempDiv3.innerHTML = nodeTree3;
        const result = helper.getHighestZIndex(window.smarteditJQuery(tempDiv3));

        expect(result).toBe(expected);
    });
});
