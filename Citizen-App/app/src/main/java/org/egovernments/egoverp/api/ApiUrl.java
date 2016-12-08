/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.api;


/**
 * The API endpoints
 **/

public class ApiUrl {

    public final static String AUTHORIZATION = "Basic ZWdvdi1hcGk6ZWdvd i1hcGk=";


    /**
     * Grievances
     */

    public final static String COMPLAINTS_COUNT_DETAILS = "/api/v1.0/complaint/count";

    public final static String COMPLAINT_GET_TYPES = "/api/v1.0/complaint/getAllTypes";

    public final static String COMPLAINT_CATEGORIES_TYPES = "/api/v1.0/complaint/getComplaintCategories";

    public final static String COMPLAINT_GET_FREQUENTLY_FILED_TYPES = "/api/v1.0/complaint/getFrequentlyFiledTypes";

    public final static String COMPLAINT_CREATE = "/api/v1.0/complaint/create";

    public final static String COMPLAINT_DOWNLOAD_SUPPORT_DOCUMENT = "/api/v1.0/complaint/{complaintNo}/downloadSupportDocument";

    public final static String COMPLAINT_GET_LOCATION_BY_NAME = "/api/v1.0/complaint/getLocation";

    public final static String COMPLAINT_LATEST = "/api/v1.0/complaint/latest/{page}/{pageSize}";

    public final static String COMPLAINT_NEARBY = "/api/v1.0/complaint/nearby/{page}/{pageSize}";

    public final static String COMPLAINT_SEARCH = "/api/v1.0/complaint/search";

    public final static String COMPLAINT_DETAIL = "/api/v1.0/complaint/{complaintNo}/detail";

    public final static String COMPLAINT_HISTORY = "/api/v1.0/complaint/{complaintNo}/complaintHistory";

    public final static String COMPLAINT_DOWNLOAD_IMAGE = "/api/v1.0/complaint/downloadfile/";

    public final static String COMPLAINT_STATUS = "/api/v1.0/complaint/{complaintNo}/status";

    public final static String COMPLAINT_UPDATE_STATUS = "/api/v1.0/complaint/{complaintNo}/updateStatus";

    /**
     * Citizen
     */
    public final static String CITIZEN_REGISTER = "/api/v1.0/createCitizen";

    public final static String CITIZEN_ACTIVATE = "/api/v1.0/activateCitizen";

    public final static String CITIZEN_LOGIN = "/api/oauth/token";

    public final static String CITIZEN_PASSWORD_RECOVER = "/api/v1.0/recoverPassword";

    public final static String CITIZEN_GET_PROFILE = "/api/v1.0/citizen/getProfile";

    public final static String CITIZEN_UPDATE_PROFILE = "/api/v1.0/citizen/updateProfile";

    public final static String CITIZEN_LOGOUT = "/api/v1.0/citizen/logout";

    public final static String CITIZEN_GET_COMPLAINT_CATEGORIES_COUNT = "/api/v1.0/citizen/getMyComplaint/count";

    public final static String CITIZEN_GET_MY_COMPLAINT = "/api/v1.0/citizen/getMyComplaint/{page}/{pageSize}";

    public final static String CITIZEN_SEND_OTP = "/api/v1.0/sendOTP";

    /**
     * Property Tax
     */

    public final static String SEARCH_PROPERTY = "/restapi/property/propertytaxdetailsByOwnerDetails";
    public final static String PROPERTY_TAX_DETAILS = "/restapi/property/propertytaxdetails";

    /**
     * Water Charges
     */

    public final static String SEARCH_WATER_CONNECTION = "/restapi/watercharges/getwatertaxdetailsByOwnerDetails";
    public final static String WATER_TAX_DETAILS = "/restapi/watercharges/getwatertaxdetails";

    /**
     *  Building plan approval
     */

    public final static String BPA_AUTH_KEY = "PuraSevaADCR@123";

    public final static String BPA_SERVER_ADDRESS="http://apdpms.ap.gov.in:8082";

    public final static String BPA_DETAILS = "/AutoDCR.APServices/PuraSeva/PuraSeva.svc/GetDetailByFileNo/{applicationNo}/{authKey}";

    /**
     *  Building penalization
     */

    public final static String BPS_SOAP_ACTION = "http://bps.in/webservices/";

    public final static String BPS_SOAP_METHOD_NAME = "GetStatus";

    public final static String BPS_SOAP_METHOD_PARAM_NAME = "ApplicationNo";

    public final static String BPS_SOAP_SERVICE_URL = "http://103.231.212.217/BPS/Services/AppSearch.asmx";

}