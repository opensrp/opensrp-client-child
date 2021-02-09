package org.smartregister.child.contract;

import org.smartregister.child.domain.WrapperParam;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 01/12/2020.
 */
public interface ChildUnderFiveFragmentContract {

    interface Presenter {

        Photo getProfilePhotoByClient(Map<String, String> detailsMap);

         Photo getProfilePhotoByClient(CommonPersonObjectClient childDetails);

        String constructChildName(Map<String, String> detailsMap);

        List<Weight> getWeights(String baseEntityId, List<Weight> weights);

        List<Height> getHeights(String baseEntityId, List<Height> heights);

        List<Height> getChildSpecificHeights(String baseEntityId, List<Height> heights);

        List<Weight> getChildSpecificWeights(String baseEntityId, List<Weight> weights);

        Date getBirthDate(Map<String, String> detailsMap);

        WeightWrapper getWeightWrapper(WrapperParam weightParam);

        HeightWrapper getHeightWrapper(WrapperParam heightParam);

        Weight getWeight(List<Weight> weights, long dateRecordedTimestamp);

        Height getHeight(List<Height> heights, long dateRecordedTimestamp);

        void sortTheWeightsInDescendingOrder(List<Weight> weightList);

        void sortTheHeightsInDescendingOrder(List<Height> heightList);

        WrapperParam getWrapperParam(Map<String, String> detailsMap, long growthRecordPosition);
    }
}
