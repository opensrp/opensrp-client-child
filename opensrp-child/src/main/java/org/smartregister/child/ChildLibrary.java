package org.smartregister.child;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppProperties;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.DrishtiApplication;

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

    private ChildLibrary(Context contextArg, Repository repositoryArg, ChildMetadata metadataArg, int applicationVersion, String applicationVersionName, int databaseVersion) {
        this.context = contextArg;
        this.repository = repositoryArg;
        this.metadata = metadataArg;
        this.applicationVersion = applicationVersion;
        this.applicationVersionName = applicationVersionName;
        this.databaseVersion = databaseVersion;
    }

    public static void init(Context context, Repository repository, ChildMetadata metadataArg, int applicationVersion, String applicationVersionName, int databaseVersion) {
        if (instance == null) {
            instance = new ChildLibrary(context, repository, metadataArg, applicationVersion, applicationVersionName, databaseVersion);
        }
    }

    /**
     * This init method is deprecated, use {@link #init(Context context, Repository repository, ChildMetadata metadataArg, int applicationVersion, String applicationVersionName, int databaseVersion)} instead which adds application version name.
     */
    @Deprecated
    public static void init(Context context, Repository repository, ChildMetadata metadataArg, int applicationVersion, int databaseVersion) {
        init(context, repository, metadataArg, applicationVersion, null, databaseVersion);

    }

    public static ChildLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call " + ChildLibrary.class.getName() +
                    ".init method in the onCreate method of " + "your Application class ");
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
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
}
