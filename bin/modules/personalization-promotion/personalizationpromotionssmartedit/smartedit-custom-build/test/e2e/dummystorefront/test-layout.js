window.sfConfigManager.registerLayout(
    "SMARTEDIT_EXT_TEST_LAYOUT", {
        pageId: 'test_layout_page_id',
        catalogVersion: 'test_layout-catalog-version',
        nodeType: 'root',
        children: [{
            componentId: 'testSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: [{
                componentId: 'testComp',
                componentType: 'someCompType',
                nodeType: 'component'
            }]
        }]
    }
);
