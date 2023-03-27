package com.sigma.sportsup.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> _text = new MutableLiveData<String>();

    public ProfileViewModel() {
        _text.setValue("This is profile Fragment");
    }

    public LiveData<String> getText() {
        return _text;
    }
}
