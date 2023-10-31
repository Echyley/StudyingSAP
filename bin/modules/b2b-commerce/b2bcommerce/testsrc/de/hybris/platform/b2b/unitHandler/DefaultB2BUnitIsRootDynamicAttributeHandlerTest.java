package de.hybris.platform.b2b.unitHandler;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.b2bunit.DefaultB2BUnitIsRootDynamicAttributeHandler;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for IsRoot Detector dynamic attribute
 *
 *
 */

@UnitTest
public class DefaultB2BUnitIsRootDynamicAttributeHandlerTest extends ServicelayerTest
{
	@Resource
	private DefaultB2BUnitIsRootDynamicAttributeHandler defaultB2BUnitIsRootDynamicAttributeHandler;

	@Test
	public void testDynamicAttributeHandlerIsRoot()
	{
		final B2BUnitModel unit = mock(B2BUnitModel.class);
		final Boolean isRoot = defaultB2BUnitIsRootDynamicAttributeHandler.get(unit);
		assertTrue(isRoot);
	}

	@Test
	public void testDynamicAttributeHandlerIsNotRoot()
	{
		final B2BUnitModel unitRoot = mock(B2BUnitModel.class);
		final B2BUnitModel unitChild = mock(B2BUnitModel.class);
		final Set<PrincipalGroupModel> groups = new HashSet<>();
		groups.add(unitRoot);
		when(unitChild.getGroups()).thenReturn(groups);
		final Boolean isRoot = defaultB2BUnitIsRootDynamicAttributeHandler.get(unitChild);
		assertFalse(isRoot);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSDynamicAttributeHandlerException()
	{
		final B2BUnitModel unit = mock(B2BUnitModel.class);
		defaultB2BUnitIsRootDynamicAttributeHandler.set(unit, Boolean.TRUE);
	}

}
