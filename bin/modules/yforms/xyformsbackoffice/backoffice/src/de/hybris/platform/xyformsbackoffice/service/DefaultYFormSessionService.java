/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.service;

import com.hybris.backoffice.cockpitng.util.impl.DefaultPlatformCockpitSessionService;
import de.hybris.platform.xyformsbackoffice.proxy.YFormsBuilderProxyService;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class DefaultYFormSessionService extends DefaultPlatformCockpitSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPlatformCockpitSessionService.class);


    @Resource(name = "yFormsBuilderProxyService")
    protected YFormsBuilderProxyService yFormsBuilderProxyService;


    @Override
    public void logout() {
        try {
            Execution execution = Executions.getCurrent();
            HttpServletRequest request = (HttpServletRequest)execution.getNativeRequest();
            yFormsBuilderProxyService.invalidateOrbeonSession(request);
        }
        catch (NotImplementedException e)
        {
            LOG.error("not implement logout orbeon session function",e);
        }

        super.logout();
    }
}
