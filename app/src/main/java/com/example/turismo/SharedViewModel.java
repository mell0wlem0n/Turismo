package com.example.turismo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> addIconTrigger = new MutableLiveData<>();
    private final MutableLiveData<Integer> iconCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> selectedGroup = new MutableLiveData<>();

    public void triggerAddIcon() {
        addIconTrigger.setValue(true);
    }

    public LiveData<Boolean> getAddIconTrigger() {
        return addIconTrigger;
    }

    public void clearTrigger() {
        addIconTrigger.setValue(false);
    }

    public void incrementIconCount() {
        int currentCount = iconCount.getValue() == null ? 0 : iconCount.getValue();
        iconCount.setValue(currentCount + 1);
    }

    public LiveData<Integer> getIconCount() {
        return iconCount;
    }
    public void selectGroup(int groupId) {
        selectedGroup.setValue(groupId);
    }

    public LiveData<Integer> getSelectedGroup() {
        return selectedGroup;
    }
    public void clearSelectedGroup() {
        selectedGroup.setValue(null);
    }
}
