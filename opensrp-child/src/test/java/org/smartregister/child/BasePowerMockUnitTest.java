package org.smartregister.child;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.SyncFilter;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AppProperties;

/**
 * Created by ndegwamartin on 14/07/2020.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, AppProperties.class, SyncFilter.class, FormEntityConstants.class, FormEntityConstants.Person.class, FormEntityConstants.Address.class, FormEntityConstants.Encounter.class
        , FormEntityConstants.PersonAddress.class, FormEntityConstants.FieldDataType.class, FormEntityConstants.FieldType.class, FormEntityConstants.FormEntity.class, VaccineRepository.class, VaccineRepo.Vaccine.class, Context.class, Vaccine.class, WeightRepository.class
        , HeightRepository.class, WeightWrapper.class, HeightWrapper.class, CommonPersonObject.class, org.smartregister.immunization.domain.jsonmapping.Vaccine.class, Event.class, CommonPersonObjectClient.class, AllSharedPreferences.class, LoginResponseData.class, FetchStatus.class,
        LoginResponse.class, AccountAuthenticatorXml.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "androidx.*", "javax.management.*", "org.xmlpull.v1.*"})
public abstract class BasePowerMockUnitTest {
}
