/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.cybersource.constants.CyberSourceConstants;
import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.ObjectUtils.notEqual;

public class DigitalPaymentsSignatureUtil {

    private static final Logger LOG = Logger.getLogger(DigitalPaymentsSignatureUtil.class);

    private AcceleratorDigestUtils digestUtils;
    private SiteConfigService siteConfigService;
    private UserService userService;

    private static final ObjectMapper mapper = new ObjectMapper();

    public String computeSignature(String data) throws InvalidKeyException, NoSuchAlgorithmException {

        //Bind data with User
        String userId = userService.getCurrentUser().getUid();
        Pair pair = new Pair(data, userId);

        String dataWithUser;
        try {
            // Convert to json
            dataWithUser = mapper.writeValueAsString(pair);

            // Compute digest
            return digestUtils.getPublicDigest(dataWithUser, getSharedSecret());
        } catch (JsonProcessingException e) {
            LOG.error(e);
        }

        return null;
    }

    public boolean isValidSignature(String signature, String data){
        validateParameterNotNullStandardMessage("data", data);
        validateParameterNotNullStandardMessage("signature", signature);
        try {
            String computedSign = computeSignature(data);
            if ( notEqual(signature, computedSign) ) {
                LOG.error("Signature doesn't match");
                return false;
            }
        }
        catch (InvalidKeyException | NoSuchAlgorithmException e){
            LOG.error(e);
            return false;
        }

        return true;
    }

    private String getSharedSecret(){
        return siteConfigService.getString(CyberSourceConstants.HopProperties.SHARED_SECRET, EMPTY);
    }

    public void setDigestUtils(AcceleratorDigestUtils digestUtils) {
        this.digestUtils = digestUtils;
    }

    public void setSiteConfigService(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    static class Pair{
        String value;
        String user;

        Pair(String value, String user) {
            this.value = value;
            this.user = user;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
}
