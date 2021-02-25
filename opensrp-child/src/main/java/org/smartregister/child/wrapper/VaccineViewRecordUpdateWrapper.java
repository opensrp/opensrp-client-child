package org.smartregister.child.wrapper;

import org.smartregister.immunization.domain.Vaccine;

import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 04/03/2019.
 */
public class VaccineViewRecordUpdateWrapper extends BaseViewRecordUpdateWrapper {

    private List<Vaccine> vaccines;
    private Map<String, Object> nv = null;
    private List<Map<String, Object>> totalNv = null;

    public List<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    public Map<String, Object> getNv() {
        return nv;
    }

    public void setNv(Map<String, Object> nv) {
        this.nv = nv;
    }

    public List<Map<String, Object>> getTotalNv() {
        return totalNv;
    }

    public void setTotalNv(List<Map<String, Object>> totalNv) {
        this.totalNv = totalNv;
    }
}
