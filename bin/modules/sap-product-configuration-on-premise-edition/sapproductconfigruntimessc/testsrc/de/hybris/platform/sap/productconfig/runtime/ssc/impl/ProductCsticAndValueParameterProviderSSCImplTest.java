/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;
import de.hybris.platform.sap.productconfig.runtime.ssc.wrapper.KBOCacheWrapper;
import de.hybris.platform.sap.productconfig.runtime.ssc.wrapper.SSCConfigSessionFactory;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKnowledgeBaseData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.sce.front.base.KnowledgeBase;
import com.sap.sce.kbrt.kb_descriptor;
import com.sap.sce.kbrt.imp.kb_cstic_imp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
public class ProductCsticAndValueParameterProviderSSCImplTest
{
	private static final String SESSION_ID = "session id";

	private static final String PRODUCT_CODE = "product code";

	private static final String PRODUCT_TYPE = "MARA";

	private ProductCsticAndValueParameterProviderSSCImpl classUnderTest;

	@Mock
	private IKnowledgeBaseData knowledgebaseData;
	@Mock
	private KnowledgeBase knowledgebase;
	@Mock
	private KBOCacheWrapper kboCache;
	@Mock
	private kb_descriptor kbDescriptor;
	@Mock
	private SSCConfigSessionFactory configSessionFactory;
	@Mock
	private IConfigSession session;
	@Mock
	private I18NService i18nService;

	@Before
	public void setUp() throws IpcCommandException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = Mockito.spy(new ProductCsticAndValueParameterProviderSSCImpl());
		classUnderTest.setKboCache(kboCache);
		classUnderTest.setI18NService(i18nService);
		classUnderTest.setConfigSessionFactory(configSessionFactory);
		when(configSessionFactory.provideInstance()).thenReturn(session);
		when(session.getSessionId()).thenReturn(SESSION_ID);
		when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
		when(kbDescriptor.getAssociatedKB()).thenReturn(knowledgebase);
		when(kboCache.get_kb_Desc_from_cache(any(), any(), any(), any(), any(), any(), any(), eq(PRODUCT_CODE), eq(PRODUCT_TYPE),
				eq(SESSION_ID))).thenReturn(kbDescriptor);
	}

	@Test
	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	public void testProcessCstic_Not_String()
	{
		final kb_cstic_imp cstic = null;

		final Map<String, CsticParameterWithValues> csticsMap = new HashMap<String, CsticParameterWithValues>();

		Mockito.doReturn(false).when(classUnderTest).isStringCstic(cstic);
		Mockito.doReturn("C1").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 1").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn("V11,V12").when(classUnderTest).retrieveCsticDomainAsSting(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(1, csticsMap.size());
		assertEquals("C1", csticsMap.get("C1").getCstic().getCsticName());
		assertEquals("Description 1", csticsMap.get("C1").getCstic().getCsticDescription());
		List<ValueParameter> values = csticsMap.get("C1").getValues();
		assertEquals(2, values.size());
		assertEquals("V11", values.get(0).getValueName());
		assertEquals("V12", values.get(1).getValueName());

		Mockito.doReturn("C2").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 2").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn("V21,V22").when(classUnderTest).retrieveCsticDomainAsSting(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(2, csticsMap.size());
		assertEquals("C2", csticsMap.get("C2").getCstic().getCsticName());
		assertEquals("Description 2", csticsMap.get("C2").getCstic().getCsticDescription());
		values = csticsMap.get("C2").getValues();
		assertEquals(2, values.size());
		assertEquals("V21", values.get(0).getValueName());
		assertEquals("V22", values.get(1).getValueName());

		Mockito.doReturn("C1").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 1").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn("V11,V13").when(classUnderTest).retrieveCsticDomainAsSting(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(2, csticsMap.size());
		assertEquals("C1", csticsMap.get("C1").getCstic().getCsticName());
		assertEquals("Description 1", csticsMap.get("C1").getCstic().getCsticDescription());
		values = csticsMap.get("C1").getValues();
		assertEquals(3, values.size());
		assertEquals("V11", values.get(0).getValueName());
		assertEquals("V12", values.get(1).getValueName());
		assertEquals("V13", values.get(2).getValueName());
	}


	@Test
	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	public void testProcessCstic_String()
	{
		final kb_cstic_imp cstic = null;

		final Map<String, CsticParameterWithValues> csticsMap = new HashMap<String, CsticParameterWithValues>();

		Mockito.doReturn(true).when(classUnderTest).isStringCstic(cstic);
		Mockito.doReturn("C1").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 1").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn(Arrays.asList("V11", "V12")).when(classUnderTest).retrieveCsticValues(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(1, csticsMap.size());
		assertEquals("C1", csticsMap.get("C1").getCstic().getCsticName());
		assertEquals("Description 1", csticsMap.get("C1").getCstic().getCsticDescription());
		List<ValueParameter> values = csticsMap.get("C1").getValues();
		assertEquals(2, values.size());
		assertEquals("V11", values.get(0).getValueName());
		assertEquals("V12", values.get(1).getValueName());

		Mockito.doReturn("C2").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 2").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn(Arrays.asList("V21", "V22")).when(classUnderTest).retrieveCsticValues(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(2, csticsMap.size());
		assertEquals("C2", csticsMap.get("C2").getCstic().getCsticName());
		assertEquals("Description 2", csticsMap.get("C2").getCstic().getCsticDescription());
		values = csticsMap.get("C2").getValues();
		assertEquals(2, values.size());
		assertEquals("V21", values.get(0).getValueName());
		assertEquals("V22", values.get(1).getValueName());

		Mockito.doReturn("C1").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 1").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn(Arrays.asList("V11", "V13")).when(classUnderTest).retrieveCsticValues(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(2, csticsMap.size());
		assertEquals("C1", csticsMap.get("C1").getCstic().getCsticName());
		assertEquals("Description 1", csticsMap.get("C1").getCstic().getCsticDescription());
		values = csticsMap.get("C1").getValues();
		assertEquals(3, values.size());
		assertEquals("V11", values.get(0).getValueName());
		assertEquals("V12", values.get(1).getValueName());
		assertEquals("V13", values.get(2).getValueName());
	}

	@Test
	public void testFillValueList()
	{
		final List<ValueParameter> values = createValueParameterList();
		final String domainAsString = "V1,V3";

		classUnderTest.fillValueList(values, domainAsString);

		assertEquals(3, values.size());
		assertEquals("V1", values.get(0).getValueName());
		assertEquals("V2", values.get(1).getValueName());
		assertEquals("V3", values.get(2).getValueName());
	}

	@Test
	public void testFillValueListEmpty()
	{
		final List<ValueParameter> values = new ArrayList();
		final String domainAsString = "";

		classUnderTest.fillValueList(values, domainAsString);

		assertEquals(0, values.size());
	}

	@Test
	public void testFillValueListEmpty2()
	{
		final List<ValueParameter> values = new ArrayList();
		final String domainAsString = ",";

		classUnderTest.fillValueList(values, domainAsString);

		assertEquals(0, values.size());
	}

	@Test
	public void testIsValueAlreadyExists()
	{
		final List<ValueParameter> values = createValueParameterList();

		assertTrue(classUnderTest.isValueAlreadyExists("V1", values));
		assertTrue(classUnderTest.isValueAlreadyExists("V2", values));
		assertFalse(classUnderTest.isValueAlreadyExists("V3", values));
	}

	private List<ValueParameter> createValueParameterList()
	{
		final List<ValueParameter> values = new ArrayList<ValueParameter>();

		ValueParameter valueParameter = new ValueParameter();
		valueParameter.setValueName("V1");
		valueParameter.setValueDescription("");
		values.add(valueParameter);

		valueParameter = new ValueParameter();
		valueParameter.setValueName("V2");
		valueParameter.setValueDescription("");
		values.add(valueParameter);

		return values;
	}

	@Test
	public void testRetrieveCsticDomainAsStingNoExceptionForNonBdt()
	{
		final kb_cstic_imp cstic = null;
		Mockito.doReturn(false).when(classUnderTest).hasKbBdtType(Mockito.any());
		assertNull(classUnderTest.retrieveCsticDomainAsSting(cstic));
	}

	@Test
	public void testRetrieveKnowledgeBase() throws IpcCommandException
	{
		final KnowledgeBase result = classUnderTest.retrieveKnowledgeBase(PRODUCT_CODE);
		assertEquals(knowledgebase, result);
	}

	@Test
	public void testRetrieveKnowledgeBaseSessionGivesNull() throws IpcCommandException
	{
		when(kboCache.get_kb_Desc_from_cache(any(), any(), any(), any(), any(), any(), any(), eq(PRODUCT_CODE), eq(PRODUCT_TYPE),
				isNotNull(String.class))).thenReturn(null);
		final KnowledgeBase result = classUnderTest.retrieveKnowledgeBase(PRODUCT_CODE);
		assertNull(result);
	}

	@Test
	public void testRetrieveKnowledgeBaseSessionManagerGivesNull() throws IpcCommandException
	{
		when(kbDescriptor.getAssociatedKB()).thenReturn(null);
		final KnowledgeBase result = classUnderTest.retrieveKnowledgeBase(PRODUCT_CODE);
		assertNull(result);
	}
}
