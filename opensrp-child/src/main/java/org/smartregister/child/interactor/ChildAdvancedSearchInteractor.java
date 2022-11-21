package org.smartregister.child.interactor;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.service.HTTPAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 11/04/2019.
 */
public class ChildAdvancedSearchInteractor implements ChildAdvancedSearchContract.Interactor {

    public static final String SEARCH_URL = "/rest/search/path";
    public static final String NEW_ADVANCE_SEARCH_URL = "/rest/client/search";
    private final AppExecutors appExecutors;
    private HTTPAgent httpAgent;
    private DristhiConfiguration dristhiConfiguration;
    private String motherGuardianNumber;

    public ChildAdvancedSearchInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    ChildAdvancedSearchInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        setMotherGuardianNumber(Constants.KEY.MOTHER_GUARDIAN_NUMBER);
    }

    @Override
    public void search(final Map<String, String> editMap, final ChildAdvancedSearchContract.InteractorCallBack callBack,
                       final String opensrpID) {
        Runnable runnable = () -> {

            final Response<String> response = globalSearch(editMap);
            appExecutors.mainThread().execute(() -> callBack.onResultsFound(response, opensrpID));
        };

        appExecutors.networkIO().execute(runnable);
    }

    private Response<String> globalSearch(Map<String, String> searchParameters) {
        if (ChildLibrary.getInstance().getProperties().isTrue(ChildAppProperties.KEY.USE_NEW_ADVANCE_SEARCH_APPROACH)) {
            return retrieveRemoteClients(searchParameters);
        }
        return searchUsingOldApproachWithPostRequest(searchParameters);
    }

    private Response<String> searchUsingOldApproachWithPostRequest(Map<String, String> searchParameters){
        if (!searchParameters.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (DBConstants.KEY.MOTHER_GUARDIAN_PHONE_NUMBER.equals(key)) {
                    key = DBConstants.KEY.MOTHER_CONTACT_PHONE_NUMBER;
                }

                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    try {
                        jsonObject.put(key, value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            String uri = getDristhiConfiguration().dristhiBaseURL() + SEARCH_URL;
            Timber.i("Advance Search URI: %s ", uri);
            return getHttpAgent().post(uri, jsonObject.toString());
        }
        return new Response<>(ResponseStatus.failure, "[]");
    }

    // TODO delete this extracted GET request approach which is being used for reference
    private Response<String> searchUsingOldApproachWithGetRequest(Map<String, String> searchParameters){
        if (!searchParameters.isEmpty()) {
            String paramString = "";
            for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (DBConstants.KEY.MOTHER_GUARDIAN_PHONE_NUMBER.equals(key)) {
                    key = DBConstants.KEY.MOTHER_CONTACT_PHONE_NUMBER;
                }
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    value = urlEncode(value);
                    String param = key.trim() + "=" + value.trim();
                    if (StringUtils.isBlank(paramString)) {
                        paramString = "?" + param;
                    } else {
                        paramString += "&" + param;
                    }
                }

            }
            String uri = getDristhiConfiguration().dristhiBaseURL() + SEARCH_URL + paramString;
            Timber.i("Advance Search URI: %s ", uri);
            return getHttpAgent().fetch(uri);
        }
        return new Response<>(ResponseStatus.failure, "[]");
    }
    /**
     * This method performs search using the endpoint rest/client/search. The query will search mother
     * and child separately and combine
     *
     * @param searchParameters Filter parameters
     * @return Payload string of the response of search
     */
    private Response<String> retrieveRemoteClients(Map<String, String> searchParameters) {
        SearchMother searchMother = null;
        try {
            searchMother = new SearchMother(searchParameters).invoke();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert searchMother != null;
        Response<String> motherSearchResult = searchMother.getMotherSearchResult();

        JSONObject childSearchJSONObject = null;
        try {
            childSearchJSONObject = generateChildSearchParameters(searchParameters);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert childSearchJSONObject != null;
        if (childSearchJSONObject.length()>0) {
            String searchEndpoint = getDristhiConfiguration().dristhiBaseURL() + NEW_ADVANCE_SEARCH_URL;
            Timber.i("Child Search URI: %s%s", searchEndpoint, childSearchJSONObject.toString());
            Response<String> childSearchResults = getHttpAgent().post(searchEndpoint, childSearchJSONObject.toString());
            if (childSearchResults.status() == ResponseStatus.success && motherSearchResult != null && motherSearchResult.status() == ResponseStatus.success) {
                try {
                    JSONArray mothersJsonArray = new JSONArray(motherSearchResult.payload());
                    JSONArray childrenJsonArray = new JSONArray(childSearchResults.payload());
                    for (int index = 0; index < mothersJsonArray.length(); index++) {
                        childrenJsonArray.put(mothersJsonArray.get(index));
                    }
                    return new Response<>(ResponseStatus.success, childrenJsonArray.toString());
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
            return childSearchResults;
        }
        return motherSearchResult != null ? motherSearchResult : new Response<>(ResponseStatus.failure, "[]");
    }

    public void setMotherGuardianNumber(String motherGuardianNumber) {
        this.motherGuardianNumber = motherGuardianNumber;
    }

    public String getMotherGuardianPhoneNumber() {
        return motherGuardianNumber;
    }

    private JSONObject generateChildSearchParameters(Map<String, String> searchParameters) throws JSONException {
        removeMotherSearchParameters(searchParameters);

        String identifier = searchParameters.remove(Constants.KEY.ZEIR_ID);
        //Search by ZEIR id and include mother relationship when identifier is provided
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotBlank(identifier)) {
            jsonObject.put("identifier", identifier);
            jsonObject.put("relationships", "mother");
            return jsonObject;
        }

        //Handle name param - use either firs/last name //TODO server does not support full name
        String name = searchParameters.remove(Constants.KEY.FIRST_NAME);
        String lastname = searchParameters.remove(Constants.KEY.LAST_NAME);
        name = StringUtils.isNotBlank(name) ? name : lastname;

        if (StringUtils.isNotBlank(name)) {
            jsonObject.put("name", name);
        }

        //Handle birth dates param
        String birthDate = getChildBirthDateParameter(searchParameters);

        if (StringUtils.isNotBlank(birthDate)) {
            jsonObject.put("birthdate", birthDate);
        }

        //Handle other client attributes
        String formattedAttributes = getChildClientAttributes(searchParameters);
        if (StringUtils.isNotBlank(formattedAttributes)) {
            jsonObject.put("attribute", formattedAttributes);
        }
        if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(birthDate) || StringUtils.isNotBlank(formattedAttributes)) {
            jsonObject.put("relationships", "mother");
        }
        return jsonObject;
    }

    @NotNull
    private String getChildBirthDateParameter(Map<String, String> searchParameters) {
        String birthDate = "";
        String birthDatesString = searchParameters.remove(Constants.KEY.BIRTH_DATE);

        String[] birthDates = birthDatesString != null ? birthDatesString.split(":") : new String[]{};
        if (birthDates != null && birthDates.length == 2 ) {
            birthDate = String.format("%s:%s", birthDates[0], birthDates[1]);
        }
        return birthDate;
    }

    @Nullable
    private String getChildClientAttributes(Map<String, String> searchParameters) {
        StringBuilder clientAttributes = new StringBuilder();
        for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equalsIgnoreCase(Constants.CHILD_STATUS.ACTIVE) && value.equalsIgnoreCase("true") ||
                    key.equalsIgnoreCase(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP) && value.equalsIgnoreCase("false") ||
                    key.equalsIgnoreCase(Constants.CHILD_STATUS.INACTIVE) && value.equalsIgnoreCase("false")) {
                continue;
            }
            clientAttributes.append(String.format("%s:%s,", key, value));
        }

        String formattedAttributes = null;
        if (StringUtils.isNotBlank(clientAttributes)) {
            formattedAttributes = clientAttributes.toString().replaceAll(",$", "").trim();
        }

        return formattedAttributes;
    }

    protected void removeMotherSearchParameters(Map<String, String> searchParameters) {
        searchParameters.remove(Constants.KEY.MOTHER_FIRST_NAME);
        searchParameters.remove(Constants.KEY.MOTHER_LAST_NAME);
        searchParameters.remove(getMotherGuardianPhoneNumber());
    }

    public DristhiConfiguration getDristhiConfiguration() {
        if (this.dristhiConfiguration == null) {
            this.dristhiConfiguration = CoreLibrary.getInstance().context().configuration();
        }
        return this.dristhiConfiguration;
    }

    public void setDristhiConfiguration(DristhiConfiguration dristhiConfiguration) {
        this.dristhiConfiguration = dristhiConfiguration;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public HTTPAgent getHttpAgent() {
        if (this.httpAgent == null) {
            this.httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        }
        return this.httpAgent;

    }

    private class SearchMother {
        private final Map<String, String> searchParameters;
        private Response<String> motherSearchResult;

        public SearchMother(Map<String, String> searchParameters) {
            this.searchParameters = searchParameters;
        }

        public Response<String> getMotherSearchResult() {
            return motherSearchResult;
        }

        public SearchMother invoke() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            String searchEndpoint = getDristhiConfiguration().dristhiBaseURL() + NEW_ADVANCE_SEARCH_URL;

            String name = searchParameters.remove(Constants.KEY.MOTHER_FIRST_NAME);
            String lastname = searchParameters.remove(Constants.KEY.MOTHER_LAST_NAME);
            name = StringUtils.isNotBlank(name) ? name : lastname;

            if (StringUtils.isNotBlank(name)) {
                jsonObject.put("name", name);
            }

            String phoneNumber = searchParameters.remove(getMotherGuardianPhoneNumber());
            if(StringUtils.isNotBlank(phoneNumber)){
                String attribute = String.format("%s:%s", getMotherGuardianPhoneNumber(), phoneNumber);
                jsonObject.put("attribute", attribute);
            }
            if(jsonObject.length() > 0){
                jsonObject.put("searchRelationship", Constants.KEY.MOTHER);
                motherSearchResult = getHttpAgent().post(searchEndpoint, jsonObject.toString());
                Timber.i("Mother Search URI: %s%s", searchEndpoint, jsonObject.toString());
            }

            return this;
        }
    }
}
