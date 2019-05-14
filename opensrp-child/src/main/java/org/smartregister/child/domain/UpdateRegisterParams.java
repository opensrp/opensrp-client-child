package org.smartregister.child.domain;

import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.BaseRepository;

/**
 * Created by ndegwamartin on 05/05/2019.
 */
public class UpdateRegisterParams {


    private String status = BaseRepository.TYPE_Unsynced;

    private boolean editMode;

    private FormTag formTag;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public FormTag getFormTag() {
        return formTag;
    }

    public void setFormTag(FormTag formTag) {
        this.formTag = formTag;
    }
}
