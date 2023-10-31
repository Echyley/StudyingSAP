package de.hybris.platform.webhookbackoffice.widgets.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;
import de.hybris.bootstrap.annotations.
		UnitTest;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PingWebhookConfigurationActionTest extends AbstractActionUnitTest<PingWebhookConfigurationAction>
{

    @Mock(lenient = true)
    private ActionContext<WebhookConfigurationModel> ctx;

    @InjectMocks
    private PingWebhookConfigurationAction action = new PingWebhookConfigurationAction();

    @Override
    public PingWebhookConfigurationAction getActionInstance()
    {
        return action;
    }

    @Test
    public void testCannotPerformWithNull()
    {
        when(ctx.getData()).thenReturn(null);

        assertThat(action.canPerform(ctx)).isFalse();
    }
}