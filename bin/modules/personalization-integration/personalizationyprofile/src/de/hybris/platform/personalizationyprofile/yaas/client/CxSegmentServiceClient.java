/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.yaas.client;

import de.hybris.platform.personalizationyprofile.yaas.Segment;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;

import rx.Observable;


@OAuth
@Http
public interface CxSegmentServiceClient
{

	/**
	 * Read segments data from yaas profile service.
	 *
	 * @return list of Segment
	 */
	@GET
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
	@Path("/${tenant}/segments")
	List<Segment> getSegments();

	/**
	 * Read segments data from yaas profile service. Method runs asynchronously.
	 *
	 * @return list of Segment
	 */
	@GET
	@Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
	@Path("/${tenant}/segments")
	Observable<List<Segment>> getSegmentsAsync();

}
