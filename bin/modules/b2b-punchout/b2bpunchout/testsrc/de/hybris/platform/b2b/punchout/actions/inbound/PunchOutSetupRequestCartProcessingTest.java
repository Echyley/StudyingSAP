package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.FileNotFoundException;
import java.util.List;

import org.cxml.CXML;
import org.cxml.ItemOut;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutSetupRequestCartProcessingTest
{
	@InjectMocks
	private final PunchOutSetupRequestCartProcessing punchOutSetupRequestCartProcessing = new PunchOutSetupRequestCartProcessing();

	private CXML requestXML;
	private CartData cartData;

	@Mock
	private CartFacade cartFacade;
	@Mock
	private Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
		cartData = new CartData();
		when(cartFacade.getCurrentCart()).thenReturn(cartData);
	}

	@Test
	public void testGenerateCxmlResponse()
	{
		punchOutSetupRequestCartProcessing.processCartData(requestXML);

		verify(cartFacade).removeSessionCart();
		verify(cartFacade).update(cartData);
		verify(itemOutConverter, times(2)).convert(any(ItemOut.class));

		ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
		verify(modelService).saveAll(argument.capture());
		assertEquals(2, argument.getValue().size());
	}
}
