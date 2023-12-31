/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.jaxb.adapters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.jaxb.MoxyJaxbContextFactoryImpl;
import de.hybris.platform.webservicescommons.jaxb.adapters.DateAdapter;
import de.hybris.platform.webservicescommons.jaxb.adapters.XSSStringAdapter;
import de.hybris.platform.webservicescommons.jaxb.metadata.impl.DefaultMetadataNameProvider;
import de.hybris.platform.webservicescommons.jaxb.metadata.impl.DefaultMetadataSourceFactory;
import de.hybris.platform.webservicescommons.mapping.SubclassRegistry;
import de.hybris.platform.webservicescommons.mapping.impl.DefaultFieldSetBuilder;
import de.hybris.platform.webservicescommons.mapping.impl.DefaultFieldSetLevelHelper;
import de.hybris.platform.webservicescommons.mapping.impl.DefaultSubclassRegistry;
import net.minidev.json.JSONObject;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.xml.transform.StringSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@UnitTest
public class MapAdapterTest
{

	@Test
	public void testMarshalContainerData() throws JAXBException
	{

		final ContainerData container = new ContainerData();
		container.setName("TEST");
		final Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("category", "test 1");
		metadata.put("creation", "test 2");
		container.setContent(metadata);

		final String json = marshal(container);

		final ContainerData containerDataUnmarshalled = unmarshal(json,
				ContainerData.class);

		Assert.assertEquals(container.getName(), containerDataUnmarshalled.getName());
		Assert.assertNotNull(container.getContent());
		Assert.assertEquals(container.getContent().size(), containerDataUnmarshalled.getContent().size());
		Assert.assertNotNull(containerDataUnmarshalled.getContent().entrySet().stream().findFirst().get().getValue());
	}

	@Test
	public void testUnmarshalContainerData() throws JAXBException
	{

		JSONObject container = new JSONObject();
		container.put("name", "TEST");
		JSONObject metadata = new JSONObject();
		metadata.put("num", 100);
		metadata.put("balance", 1000.21);
		metadata.put("isVip", true);
		metadata.put("point", null);
		container.put("content", metadata);


		final String json = container.toJSONString();

		final ContainerData containerDataUnmarshalled = unmarshal(json,
				ContainerData.class);

		Assert.assertEquals("TEST", containerDataUnmarshalled.getName());
		Assert.assertNotNull(containerDataUnmarshalled.getContent());
		Assert.assertEquals(4, containerDataUnmarshalled.getContent().size());
		Assert.assertNotNull(containerDataUnmarshalled.getContent().entrySet().stream().findFirst().get().getValue());
		Assert.assertEquals("1000.21", containerDataUnmarshalled.getContent().get("balance"));
		Assert.assertEquals("true", containerDataUnmarshalled.getContent().get("isVip"));
		Assert.assertNull(containerDataUnmarshalled.getContent().get("point"));
		Assert.assertEquals("100", containerDataUnmarshalled.getContent().get("num"));
	}


	protected static MoxyJaxbContextFactoryImpl createMoxyJaxbContextFactoryImpl()
	{
		final SubclassRegistry subClassRegistry = new DefaultSubclassRegistry();

		final DefaultFieldSetBuilder fieldSetBuilder = new DefaultFieldSetBuilder();
		fieldSetBuilder.setSubclassRegistry(subClassRegistry);
		fieldSetBuilder.setDefaultMaxFieldSetSize(50000);
		fieldSetBuilder.setFieldSetLevelHelper(new DefaultFieldSetLevelHelper());
		fieldSetBuilder.setDefaultRecurrencyLevel(4);

		final DefaultMetadataSourceFactory metadataSourceFactory = new DefaultMetadataSourceFactory();
		final DefaultMetadataNameProvider nameProvider = new DefaultMetadataNameProvider();
		metadataSourceFactory.setNameProvider(nameProvider);

		// build MoxyJaxbContextFactory
		final MoxyJaxbContextFactoryImpl moxyJaxbContextFactory = new MoxyJaxbContextFactoryImpl();
		moxyJaxbContextFactory.setSubclassRegistry(subClassRegistry);
		moxyJaxbContextFactory.setTypeAdapters(Arrays.asList(StringMapAdapter.class, XSSStringAdapter.class, DateAdapter.class));
		moxyJaxbContextFactory.setMetadataSourceFactory(metadataSourceFactory);
		moxyJaxbContextFactory.setWrapCollections(false);
		moxyJaxbContextFactory.setAnalysisDepth(5);
		return moxyJaxbContextFactory;
	}

	public static <T> String marshal(final T cls) throws JAXBException
	{

		final JAXBContext context = createMoxyJaxbContextFactoryImpl().createJaxbContext(cls.getClass());
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		marshaller.marshal(cls, byteArrayOutputStream);
		return byteArrayOutputStream.toString();
	}

	public static <T> T unmarshal(final String source, final Class<T> cls)
			throws JAXBException
	{
		final JAXBContext context = createMoxyJaxbContextFactoryImpl().createJaxbContext(cls);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		unmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

		final StreamSource streamSource = new StringSource(source);
		return unmarshaller.unmarshal(streamSource, cls).getValue();
	}
}


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class ContainerData
{

	private String name;

	@XmlPath(".")
	@XmlJavaTypeAdapter(StringMapAdapter.class)
	private Map<String, String> content = new HashMap<String, String>();

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public void setContent(final Map<String, String> content)
	{
		this.content = content;
	}

	public Map<String, String> getContent()
	{
		return content;
	}
}
