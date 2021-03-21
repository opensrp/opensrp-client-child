package org.smartregister.child;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppProperties;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import timber.log.Timber;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class ChildLibrary {

    private static ChildLibrary instance;
    private final Context context;
    private final Repository repository;
    private final ChildMetadata metadata;
    private final int applicationVersion;
    private final int databaseVersion;
    private String applicationVersionName;
    private UniqueIdRepository uniqueIdRepository;
    private EventClientRepository eventClientRepository;
    private ECSyncHelper syncHelper;
    private ClientProcessorForJava clientProcessorForJava;
    private Compressor compressor;
    private LocationPickerView locationPickerView;
    private EventBus eventBus;

    private ChildLibrary(Context contextArg, Repository repositoryArg, ChildMetadata metadataArg, int applicationVersion, int databaseVersion) {
        this.context = contextArg;
        this.repository = repositoryArg;
        this.metadata = metadataArg;
        this.applicationVersion = applicationVersion;
        this.databaseVersion = databaseVersion;
    }

    public static void init(Context context, Repository repository, ChildMetadata metadataArg, int applicationVersion,
                            int databaseVersion) {
        if (instance == null) {
            instance = new ChildLibrary(context, repository, metadataArg, applicationVersion, databaseVersion);
        }
    }

    public static ChildLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call " + ChildLibrary.class.getName() +
                    ".init method in the onCreate method of " + "your Application class ");
        }
        return instance;
    }

    public ChildMetadata metadata() {
        return metadata;
    }

    public int getApplicationVersion() {
        return applicationVersion;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    public Repository getRepository() {
        return repository;
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository();
        }
        return eventClientRepository;
    }

    public ECSyncHelper getEcSyncHelper() {
        if (syncHelper == null) {
            syncHelper = ECSyncHelper.getInstance(context().applicationContext());
        }
        return syncHelper;
    }

    public Context context() {
        return context;
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        if (clientProcessorForJava == null) {
            clientProcessorForJava = DrishtiApplication.getInstance().getClientProcessor();
        }
        return clientProcessorForJava;
    }

    public void setClientProcessorForJava(ClientProcessorForJava clientProcessorForJava) {
        this.clientProcessorForJava = clientProcessorForJava;
    }

    public Compressor getCompressor() {
        if (compressor == null) {
            compressor = new Compressor(this.context().applicationContext());
        }
        return compressor;
    }

    public LocationPickerView getLocationPickerView(android.content.Context context) {
        if (locationPickerView == null) {
            locationPickerView = new LocationPickerView(context);
            new Handler(context.getMainLooper()).post(() -> locationPickerView.init());

        }
        return locationPickerView;
    }

    public AppProperties getProperties() {
        return CoreLibrary.getInstance().context().getAppProperties();
    }

    public EventBus getEventBus() {

        if (eventBus == null) {
            Timber.e(" Event Bus instance does not exist!!! Pass the Implementing Application's Eventbus by invoking the " +
                    ChildLibrary.class.getCanonicalName() + ".setEventBus method from the onCreate method of " + "your Application class ");
        }

        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public String getApplicationVersionName() {
        return applicationVersionName;
    }

    public void setApplicationVersionName(String applicationVersionName) {
        this.applicationVersionName = applicationVersionName;
    }

    public LocationRepository getLocationRepository() {
        return CoreLibrary.getInstance().context().getLocationRepository();
    }

    public List<String> getAllowedLevelLocationIds(List<String> allowedLevels) {
        List<String> locationIds = new ArrayList<>();
        LocationHelper locationHelper = LocationHelper.getInstance();
        if (locationHelper != null) {
            locationIds = retrieveAllowedLocationsIds(locationHelper.map(), allowedLevels, locationIds);
        }
        return locationIds;
    }

    public List<String> retrieveAllowedLocationsIds(LinkedHashMap<String, TreeNode<String, Location>> map,
                                                    List<String> allowedLevels, List<String> locationIds) {
        if (map == null || map.isEmpty()) return locationIds;
        for (Map.Entry<String, TreeNode<String, Location>> treeNodeEntry : map.entrySet()) {
            TreeNode<String, Location> value = treeNodeEntry.getValue();

            addLocationId(allowedLevels, locationIds, value);

            if (value.getChildren() == null) {
                continue;
            }
            for (Map.Entry<String, TreeNode<String, Location>> childEntry : value.getChildren().entrySet()) {
                TreeNode<String, Location> childValue = childEntry.getValue();
                addLocationId(allowedLevels, locationIds, childValue);
                if (childValue.getChildren() == null) {
                    continue;
                }
                retrieveAllowedLocationsIds(childValue.getChildren(), allowedLevels, locationIds);
            }
        }
        return locationIds;
    }

    private void addLocationId(List<String> allowedLevels, List<String> locationIds, TreeNode<String, Location> value) {
        Location location = value.getNode();
        if (location.getTags() != null) {
            for (String tag : location.getTags()) {
                if (allowedLevels.contains(tag)) {
                    locationIds.add(location.getLocationId());
                }
            }
        }
    }

}
