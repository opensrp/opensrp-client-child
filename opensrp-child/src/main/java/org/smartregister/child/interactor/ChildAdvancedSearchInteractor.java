package org.smartregister.child.interactor;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.Constants;
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
        String paramString = "";
        if (!searchParameters.isEmpty()) {
            for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

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
        SearchMother searchMother = new SearchMother(searchParameters).invoke();
        Response<String> motherSearchResult = searchMother.getMotherSearchResult();

        String childSearchParameters = generateChildSearchParameters(searchParameters);
        if (StringUtils.isNotBlank(childSearchParameters)) {
            String searchEndpoint = getDristhiConfiguration().dristhiBaseURL() + NEW_ADVANCE_SEARCH_URL;
            Timber.i("Child Search URI: %s%s", searchEndpoint, childSearchParameters);
            Response<String> childSearchResults = getHttpAgent().fetch(searchEndpoint + childSearchParameters);
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

    private String generateChildSearchParameters(Map<String, String> searchParameters) {
        removeMotherSearchParameters(searchParameters);

        StringBuilder queryParamStringBuilder = new StringBuilder();

        String identifier = searchParameters.remove(Constants.KEY.ZEIR_ID);

        //Search by ZEIR id and include mother relationship when identifier is provided
        if (StringUtils.isNotBlank(identifier)) {
            queryParamStringBuilder.append("?identifier=").append(identifier);
            queryParamStringBuilder.append("&relationships=mother");
            return queryParamStringBuilder.toString();
        }

        //Handle name param - use either firs/last name //TODO server does not support full name
        String name = searchParameters.remove(Constants.KEY.FIRST_NAME);
        String lastname = searchParameters.remove(Constants.KEY.LAST_NAME);
        name = StringUtils.isNotBlank(name) ? name : lastname;

        if (StringUtils.isNotBlank(name)) {
            queryParamStringBuilder.append("?name=").append(name);
        }

        //Handle birth dates param
        String birthDate = getChildBirthDateParameter(searchParameters, name);

        if (StringUtils.isNotBlank(birthDate)) {
            queryParamStringBuilder.append(birthDate);
        }

        //Handle other client attributes
        String formattedAttributes = getChildClientAttributes(searchParameters, queryParamStringBuilder,
                StringUtils.isNotBlank(name) || StringUtils.isNotBlank(birthDate));
        if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(birthDate) || StringUtils.isNotBlank(formattedAttributes)) {
            queryParamStringBuilder.append("&relationships=mother");
        }
        return queryParamStringBuilder.toString();
    }

    @NotNull
    private String getChildBirthDateParameter(Map<String, String> searchParameters, String name) {
        String birthDate = "";
        String birthDatesString = searchParameters.remove(Constants.KEY.BIRTH_DATE);

        String[] birthDates = birthDatesString != null ? birthDatesString.split(":") : new String[]{};
        if (birthDates != null && birthDates.length == 2 && StringUtils.isNotBlank(name)) {
            birthDate = String.format("&birthdate=%s:%s", birthDates[0], birthDates[1]);
        } else if (birthDates != null && birthDates.length == 2 && StringUtils.isBlank(name)) {
            birthDate = String.format("?birthdate=%s:%s", birthDates[0], birthDates[1]);
        }

        return birthDate;
    }

    @Nullable
    private String getChildClientAttributes(Map<String, String> searchParameters, StringBuilder queryParamStringBuilder, boolean nameBirthDateAttributesPresent) {
        StringBuilder clientAttributes = new StringBuilder();
        for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
            String key = entry.getKey();
            String value = urlEncode(entry.getValue());
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
        if (StringUtils.isNotBlank(formattedAttributes) && nameBirthDateAttributesPresent) {
            queryParamStringBuilder.append("&attribute=").append(formattedAttributes);
        } else if (StringUtils.isNotBlank(formattedAttributes) && !nameBirthDateAttributesPresent) {
            queryParamStringBuilder.append("?attribute=").append(formattedAttributes);
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

        public SearchMother invoke() {
            String searchEndpoint = getDristhiConfiguration().dristhiBaseURL() + NEW_ADVANCE_SEARCH_URL;
            String motherSearchParameters = "";

            String name = searchParameters.remove(Constants.KEY.MOTHER_FIRST_NAME);
            String lastname = searchParameters.remove(Constants.KEY.MOTHER_LAST_NAME);
            name = StringUtils.isNotBlank(name) ? name : lastname;

            if (StringUtils.isNotBlank(name)) {
                motherSearchParameters = String.format("?name=%s", name);
            }

            String phoneNumber = searchParameters.remove(getMotherGuardianPhoneNumber());
            if (StringUtils.isNotBlank(motherSearchParameters) && StringUtils.isNotBlank(phoneNumber)) {
                motherSearchParameters = String.format("%s&attribute=%s:%s", motherSearchParameters, getMotherGuardianPhoneNumber(), phoneNumber);
            } else if (StringUtils.isBlank(motherSearchParameters) && StringUtils.isNotBlank(phoneNumber)) {
                motherSearchParameters = String.format("?attribute=%s:%s", getMotherGuardianPhoneNumber(), phoneNumber);
            }

            if (StringUtils.isNotBlank(motherSearchParameters)) {
                motherSearchParameters = String.format("%s&searchRelationship=%s", motherSearchParameters, Constants.KEY.MOTHER);
                motherSearchResult = getHttpAgent().fetch(searchEndpoint + motherSearchParameters);
                Timber.i("Mother Search URI: %s%s", searchEndpoint, motherSearchParameters);
            }
            return this;
        }
    }
}
