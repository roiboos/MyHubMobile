package smarthome.petersen.com.myhub.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by mull12 on 14.02.2018.
 */

public class MyHubViewModelFactory implements ViewModelProvider.Factory
{
    private String _userid;

    public MyHubViewModelFactory(String userid)
    {
        _userid = userid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    {
        return (T) new MyHubViewModel(_userid);
    }
}
