/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.services

import com.hybris.cockpitng.util.notifications.DefaultNotificationService
import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule
import de.hybris.platform.integrationbackoffice.widgets.configuration.data.ExportConfigurationEditorPresentation
import de.hybris.platform.servicelayer.ServicelayerTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

import javax.annotation.Resource

@Ignore("The test keeps failing on ECPP and can't be reproduced locally.")
@IntegrationTest
class ExportConfigurationEditorPresentationPopulatorIntegrationTest extends ServicelayerTest {

    static final def POPULATOR = 'exportConfigPresentationPopulator'
    static final def PRESENTATION = 'defaultExportConfigurationEditorPresentation'
    static final def NOTIFICATION_SERVICE = 'notificationService'

    @Resource
    private ExportConfigurationEditorPresentationPopulator exportConfigPresentationPopulator

    @Rule
    public BeanRegisteringRule rule = new BeanRegisteringRule()
            .register(ExportConfigurationEditorPresentationPopulator, POPULATOR)
            .register(DefaultNotificationService, NOTIFICATION_SERVICE)
            .register(ExportConfigurationEditorPresentation, PRESENTATION)

    @Test
    void "can populate correct bean"() {
        expect:
        exportConfigPresentationPopulator.getExportConfigEditorPresentation() != null
    }
}
