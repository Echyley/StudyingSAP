package de.hybris.platform.servicelayer.search

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.jalo.JaloSession

import static de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA;
import de.hybris.platform.servicelayer.ServicelayerBaseSpecification
import de.hybris.platform.servicelayer.session.SessionService
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class FlexibleSearchServiceSpecification extends ServicelayerBaseSpecification {

  @Resource
  private FlexibleSearchService flexibleSearchService

  @Resource
  private SessionService sessionService

  @Test
  def "calculates readonly enablement with implicit context as #expected when #value in context"() {

    given:
    sessionService.setAttribute(CTX_ENABLE_FS_ON_READ_REPLICA, value)

    when:
    var isReadOnlyEnabled = flexibleSearchService.isReadOnlyDataSourceEnabled()

    then:
    isReadOnlyEnabled == expected

    where:
    value        | expected
    true         | Optional.of(true)
    false        | Optional.of(false)
    "true"       | Optional.of(true)
    "false"      | Optional.of(false)
    "TRUE"       | Optional.of(true)
    "fAlsE"      | Optional.of(false)
    null         | Optional.empty()
    "other"      | Optional.empty()
    new Object() | Optional.empty()
  }

  @Test
  def "calculates readonly enablement with explicit context as #expected when #value in context"() {

    given:
    sessionService.setAttribute(CTX_ENABLE_FS_ON_READ_REPLICA, value)

    when:
    var isReadOnlyEnabled = flexibleSearchService.isReadOnlyDataSourceEnabled(JaloSession.getCurrentSession().getSessionContext())

    then:
    isReadOnlyEnabled == expected

    where:
    value        | expected
    true         | Optional.of(true)
    false        | Optional.of(false)
    "true"       | Optional.of(true)
    "false"      | Optional.of(false)
    "TRUE"       | Optional.of(true)
    "fAlsE"      | Optional.of(false)
    null         | Optional.empty()
    "other"      | Optional.empty()
    new Object() | Optional.empty()
  }
}
