package org.smartregister.child.interactor;

import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.Constants;
import org.smartregister.domain.Response;
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
    private AppExecutors appExecutors;
    private HTTPAgent httpAgent;
    private DristhiConfiguration dristhiConfiguration;

    public ChildAdvancedSearchInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    ChildAdvancedSearchInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void search(final Map<String, String> editMap, final ChildAdvancedSearchContract.InteractorCallBack callBack,
                       final String opensrpID) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final Response<String> response = globalSearch(editMap);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResultsFound(response, opensrpID);
                    }
                });
            }
        };

        appExecutors.networkIO().execute(runnable);
    }

    private Response<String> globalSearch(Map<String, String> searchParameters) {
        String baseUrl = getDristhiConfiguration().dristhiBaseURL();
        String paramString = "";
        String uri;
        enhanceStatusFilter(searchParameters);
        if (Boolean.parseBoolean(ChildLibrary.getInstance().getProperties()
                .getProperty(ChildAppProperties.KEY.USE_NEW_ADVANCE_SEARCH_APPROACH, "false"))) {

            uri = String.format("%s%s%s", baseUrl, NEW_ADVANCE_SEARCH_URL, generateParamString(searchParameters));
        } else {
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

            }
            uri = String.format("%s%s%s", baseUrl, SEARCH_URL, paramString);
        }

        Timber.i("Advance Search URI: %s ", uri);
        return getHttpAgent().fetch(uri);
    }

    private String generateParamString(Map<String, String> searchParameters) {
        StringBuilder stringBuilder = new StringBuilder("");
        String fullName = "";
        StringBuilder clientAttributes = new StringBuilder("attribute=");
        if (searchParameters.containsKey(Constants.KEY.FIRST_NAME)) {
            fullName = searchParameters.get(Constants.KEY.FIRST_NAME);
        }
        if (searchParameters.containsKey(Constants.KEY.LAST_NAME)) {
            fullName = String.format("%s %s", fullName, searchParameters.get(Constants.KEY.LAST_NAME));
        }
        for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (Constants.KEY.FIRST_NAME.equalsIgnoreCase(key) || Constants.KEY.LAST_NAME.equalsIgnoreCase(key)) {
                continue;
            }
            clientAttributes.append(String.format("%s:%s,", key, value));

        }
        if (StringUtils.isNoneBlank(fullName)) {
            stringBuilder.append("?name=").append(fullName);
        }
        String formattedAttributes = clientAttributes.toString().replaceAll(",$", "").trim();
        if (clientAttributes.toString().contains(":") && StringUtils.isNoneBlank(fullName)) {
            stringBuilder.append(String.format("&%s", formattedAttributes));
        } else {
            stringBuilder.append(formattedAttributes);
        }
        if(StringUtils.isNoneBlank(fullName) || StringUtils.isNoneBlank(formattedAttributes)){
            stringBuilder.append("&relationships=mother");
        }
        return stringBuilder.toString();
    }

    private void enhanceStatusFilter(Map<String, String> map) {

        if (!map.containsKey(Constants.CHILD_STATUS.ACTIVE)) {
            map.put(Constants.CHILD_STATUS.ACTIVE, "false");
        }
        if (!map.containsKey(Constants.CHILD_STATUS.INACTIVE)) {
            map.put(Constants.CHILD_STATUS.INACTIVE, "false");
        }

        if (!map.containsKey(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP)) {
            map.put(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, "false");
        }

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

    public void setHttpAgent(HTTPAgent httpAgent) {
        this.httpAgent = httpAgent;
    }
}
