package org.smartregister.child.util;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.AppProperties;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ChildDbUtilsTest extends BaseUnitTest {

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fetchChildFirstGrowthAndMonitoring() {
        String baseEntityId = "2323-24324";
        String dateCreated = "2020-01-22 10:28:38";

        Cursor weightCursor = Mockito.mock(Cursor.class);
        Mockito.when(sqLiteDatabase.query("weights", new String[]{"kg", "created_at"},
                "base_entity_id = ?",
                new String[]{baseEntityId}, null, null, "created_at asc", "1")).thenReturn(weightCursor);
        Mockito.when(weightCursor.getCount()).thenReturn(1);
        Mockito.when(weightCursor.moveToNext()).thenReturn(true);
        Mockito.when(weightCursor.getString(0)).thenReturn("20");
        Mockito.when(weightCursor.getString(1)).thenReturn(dateCreated);


        Cursor heightCursor = Mockito.mock(Cursor.class);
        Mockito.when(sqLiteDatabase.query("heights", new String[]{"cm", "created_at"},
                "base_entity_id = ? and created_at = ?",
                new String[]{baseEntityId, dateCreated}, null, null, null, "1")).thenReturn(heightCursor);
        Mockito.when(heightCursor.getCount()).thenReturn(1);
        Mockito.when(heightCursor.moveToNext()).thenReturn(true);
        Mockito.when(heightCursor.getString(0)).thenReturn("30");

        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(childLibrary.getRepository()).thenReturn(repository);
        AppProperties appProperties = Mockito.mock(AppProperties.class);
        Context context = Mockito.mock(Context.class);
        Mockito.when(appProperties.hasProperty(ChildAppProperties.KEY.MONITOR_HEIGHT)).thenReturn(true);
        Mockito.when(appProperties.getPropertyBoolean(ChildAppProperties.KEY.MONITOR_HEIGHT)).thenReturn(true);
        Mockito.when(context.getAppProperties()).thenReturn(appProperties);
        Mockito.when(coreLibrary.context()).thenReturn(context);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        HashMap<String, String> result = ChildDbUtils.fetchChildFirstGrowthAndMonitoring(baseEntityId);
        Assert.assertEquals("20", result.get("birth_weight"));
        Assert.assertEquals("30", result.get("birth_height"));
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void testUpdateChildDetailsValueShouldPassCorrectValues() {
        String baseEntityId = "234-2323";
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(childLibrary.getRepository()).thenReturn(repository);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, BaseChildRegisterActivity.class, true, new RegisterQueryProvider());
        Mockito.when(childLibrary.metadata()).thenReturn(childMetadata);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildDbUtils.updateChildDetailsValue("inactive", "false", baseEntityId);
        ContentValues contentValues = new ContentValues();
        contentValues.put("inactive", "false");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.eq(childMetadata.getRegisterQueryProvider().getChildDetailsTable()),
                Mockito.eq(contentValues), Mockito.eq(Constants.KEY.BASE_ENTITY_ID + "= ?"), Mockito.eq(new String[]{baseEntityId}));
    }

    @Test
    public void testFetchChildDetailsShouldPassCorrectValues() {
        String baseEntityId = "234-2323";
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(childLibrary.getRepository()).thenReturn(repository);
        EventClientRepository eventClientRepository = Mockito.mock(EventClientRepository.class);
        ArrayList<HashMap<String, String>> hashMaps = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("first_name", "John");
        hashMaps.add(map);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, BaseChildRegisterActivity.class, true, new RegisterQueryProvider());
        Mockito.when(eventClientRepository.rawQuery(sqLiteDatabase, childMetadata.getRegisterQueryProvider().mainRegisterQuery()
                + " WHERE " + childMetadata.getRegisterQueryProvider().getDemographicTable() + ".id = '" + baseEntityId + "' LIMIT 1")).thenReturn(hashMaps);
        Mockito.when(childLibrary.eventClientRepository()).thenReturn(eventClientRepository);
        Mockito.when(childLibrary.metadata()).thenReturn(childMetadata);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildDbUtils.fetchChildDetails(baseEntityId);
        Mockito.verify(eventClientRepository, Mockito.times(1)).rawQuery(Mockito.eq(sqLiteDatabase), Mockito.eq(childMetadata.getRegisterQueryProvider().mainRegisterQuery()
                + " WHERE " + childMetadata.getRegisterQueryProvider().getDemographicTable() + ".id = '" + baseEntityId + "' LIMIT 1"));
    }

    @Test
    public void testFetchChildDetailsShouldCreateValidCommonPersonObjectClientInstance() {

        String baseEntityId = "4834-343-44234-2323";

        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);

        Mockito.when(childLibrary.getRepository()).thenReturn(repository);
        EventClientRepository eventClientRepository = Mockito.mock(EventClientRepository.class);

        ArrayList<HashMap<String, String>> hashMaps = new ArrayList<>();

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put(DBConstants.KEY.FIRST_NAME, "Alfred");
        childDetails.put(DBConstants.KEY.ZEIR_ID, "100003U");
        childDetails.put(DBConstants.KEY.GENDER, "Male");
        hashMaps.add(childDetails);

        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, BaseChildRegisterActivity.class, true, new RegisterQueryProvider());
        Mockito.when(eventClientRepository.rawQuery(sqLiteDatabase, metadata.getRegisterQueryProvider().mainRegisterQuery()
                + " WHERE " + metadata.getRegisterQueryProvider().getDemographicTable() + ".id = '" + baseEntityId + "' LIMIT 1")).thenReturn(hashMaps);

        Mockito.when(childLibrary.eventClientRepository()).thenReturn(eventClientRepository);
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        CommonPersonObjectClient personObjectClient = ChildDbUtils.fetchCommonPersonObjectClientByBaseEntityId(baseEntityId);
        Mockito.verify(eventClientRepository, Mockito.times(1)).rawQuery(Mockito.eq(sqLiteDatabase), Mockito.eq(metadata.getRegisterQueryProvider().mainRegisterQuery()
                + " WHERE " + metadata.getRegisterQueryProvider().getDemographicTable() + ".id = '" + baseEntityId + "' LIMIT 1"));

        Assert.assertNotNull(personObjectClient);
        Assert.assertNotNull(personObjectClient.getCaseId());
        Assert.assertEquals(baseEntityId, personObjectClient.getCaseId());
        Assert.assertNotNull(personObjectClient.getName());
        Assert.assertEquals("child", personObjectClient.getName());
        Assert.assertNotNull(personObjectClient.getColumnmaps());
        Assert.assertEquals(3, personObjectClient.getColumnmaps().size());
        Assert.assertEquals(3, personObjectClient.getDetails().size());
        Assert.assertEquals("Alfred", personObjectClient.getDetails().get(DBConstants.KEY.FIRST_NAME));
        Assert.assertEquals("Male", personObjectClient.getDetails().get(DBConstants.KEY.GENDER));
        Assert.assertEquals("100003U", personObjectClient.getDetails().get(DBConstants.KEY.ZEIR_ID));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }
}