/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.login.impl;

import java.util.ArrayList;
import java.util.List;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSObject;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookEvent;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookEventData;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookRequest;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaUserManagementMode;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;

public class GigyaB2BTestUtils {
	public static final String SAMPLE_UID_SIGNATURE = "sample- uid-sig";
	public static final String SAMPLE_UID_SIGNATURE_TS = "sample-uid-sig-ts";
	public static final String SAMPLE_GIGYA_SITE_SECRET_KEY = "sample-gigya-site-secret-key";
	public static final String SAMPLE_GIGYA_USER_KEY = "sample-gigya-user-key";
	public static final String SAMPLE_GIGYA_USER_SECRET = "sample-gigya-user-secret";
	
	public static final String SAMPLE_UID = "sample-uid";
	public static final String SAMPLE_API_KEY = "sample-api-key";
	public static final String SAMPLE_JWT_TOKEN = "sample-jwt-token";
	public static final String SAMPLE_JWT_PAYLOAD_HASH = "sample-jwt-payload-hash";
	public static final String SAMPLE_BASESITE_ID = "sample-basesite";
	public static final String ACCOUNT_CREATED_WEBHOOK_EVENT = "accountCreated";
	public static final String ACCOUNT_UPDATED_WEBHOOK_EVENT = "accountUpdated";
	
	public static final int STATUS_CODE_200 = 200;
	public static final int ZERO = 0;
	public static final String EMPTY_JSON = "{}";
	
	public static final String EMAIL = "email";
	public static final String FIRSTNAME = "firstName";
	public static final String LASTNAME = "lastName";
	
	public static final String PROFILE = "profile";
	public static final String GROUPS = "groups";
	public static final String UID = "UID";
	public static final String BASE_SITE_ID = "baseSite";
	public static final String PREFERENCES = "preferences";
	
	public static final String BPID = "bpid";
	public static final String ORGID ="orgId";
	public static final String ORGANISATIONS = "organizations";
	
	public static final String DUMMY_BPID = "MyOrgBPID";
	public static final String DUMMY_ORGID ="MyOrgID";
	public static final String DUMMY_UID = "uid";
	
	public static final String DUMMY_TOKEN1 = "TOKEN-01";
	public static final String DUMMY_TOKEN2 = "TOKEN-02";
	
	
	public static final String DUMMY_EMAIL1 = "dummyemail@mail.com";
	public static final String DUMMY_FIRSTNAME1 = "dummyfirstname";
	public static final String DUMMY_LASTNAME1 = "dummylastname";
	
	public static final String DUMMY_EMAIL2 = "email@mail.com";
	public static final String DUMMY_FIRSTNAME2 = "First Name";
	public static final String DUMMY_LASTNAME2 = "Last Name";
	
	public static final String UPDATED_FIRSTNAME2 = "Mock First Name";
	
	public static final String JWT_TOKEN_HASH = "jwtTokenHash";
	public static final String NONCE = "76127162378123868128";
	public static final String API_KEY = "API-KEY";
	
	public static final String ACCOUNT_CREATED_EVENT = "accountCreated";
	public static final String EVENT_ID = "987654321";
	public static final String CALL_ID = "123456789";
	public static final String VERSION = "2.0";
	public static final String ACCOUNT_TYPE = "full";
	
	public static final Long TIMESTAMP1 =  1637300418L;	
	public static final Long TIMESTAMP2 =  1637300415L;
	
	public static final String GS_RESPONSE = "gsResponse";
	public static final String GS_STATUS_CODE = "statusCode";
	public static final String GS_ERROR_CODE = "errorCode";
	
	public static final String SAMPLE_B2B_UID = "sample-b2b-uid";
	
	
	public static GigyaJsOnLoginInfo createJSInfo() {
		GigyaJsOnLoginInfo jsInfo = new GigyaJsOnLoginInfo();
		jsInfo.setUIDSignature(SAMPLE_UID_SIGNATURE);
		jsInfo.setSignatureTimestamp(SAMPLE_UID_SIGNATURE_TS);
		jsInfo.setUID(DUMMY_EMAIL1);
		return jsInfo;
	}
	
	
	public static GigyaConfigModel createGigyaConfigModel() {
		GigyaConfigModel gigyaConfig = new GigyaConfigModel();
		gigyaConfig.setGigyaUserKey(SAMPLE_GIGYA_USER_KEY);
		gigyaConfig.setGigyaUserSecret(SAMPLE_GIGYA_USER_SECRET);
		gigyaConfig.setMode(GigyaUserManagementMode.RAAS);
		return gigyaConfig;
	}
	
	
	public static CMSSiteModel createCMSSiteModel() {
		CMSSiteModel cmsSite = new CMSSiteModel();
		cmsSite.setGigyaConfig(createGigyaConfigModel());
		return cmsSite;
	}
	
	public static GSObject createGroupData() {
		GSObject groupData = new GSObject();
		GSObject orgData = new GSObject();
		GSArray gigyaOrgData = new GSArray();
		orgData.put(BPID, DUMMY_BPID);
		orgData.put(ORGID, DUMMY_ORGID);
		gigyaOrgData.add(orgData);
		groupData.put(ORGANISATIONS,gigyaOrgData);
		return groupData;
	}
	
	public static GigyaWebHookRequest createWebHookRequest(final String eventType) {
		GigyaWebHookRequest gigyaWebHookRequest = new GigyaWebHookRequest();
		gigyaWebHookRequest.setNonce(NONCE);
		gigyaWebHookRequest.setTimestamp(TIMESTAMP1);
		gigyaWebHookRequest.setBaseSiteId(BASE_SITE_ID);
		
		
		GigyaWebHookEvent gigyaWebHookEvent = new GigyaWebHookEvent();
		gigyaWebHookEvent.setApiKey(API_KEY);
		gigyaWebHookEvent.setType(eventType);
		gigyaWebHookEvent.setId(EVENT_ID);
		gigyaWebHookEvent.setVersion(VERSION);
		gigyaWebHookEvent.setCallId(CALL_ID);
		gigyaWebHookEvent.setTimestamp(TIMESTAMP2);
		
		
		GigyaWebHookEventData gigyaWebHookEventData = new GigyaWebHookEventData();
		gigyaWebHookEventData.setApiKey(API_KEY);
		gigyaWebHookEventData.setAccountType(ACCOUNT_TYPE);
		gigyaWebHookEventData.setUid(DUMMY_UID);
		
		List<GigyaWebHookEvent> gigyaWebHookEvents = new ArrayList<>();
		gigyaWebHookEvent.setData(gigyaWebHookEventData);
		gigyaWebHookEvents.add(gigyaWebHookEvent);
		gigyaWebHookRequest.setEvents(gigyaWebHookEvents);
		return gigyaWebHookRequest;
	}
	
}
