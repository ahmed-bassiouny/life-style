package lifestyle.com.lifestyle.controller;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import bassiouny.ahmed.genericmanager.SharedPrefManager;
import lifestyle.com.lifestyle.R;
import lifestyle.com.lifestyle.activity.CalcCaloryActivity;
import lifestyle.com.lifestyle.activity.HomeActivity;
import lifestyle.com.lifestyle.api.RequestCallback;
import lifestyle.com.lifestyle.base.ui.BaseController;
import lifestyle.com.lifestyle.helper.Constants;
import lifestyle.com.lifestyle.interactor.IUserInteractor;
import lifestyle.com.lifestyle.interactor.UserInteractor;
import lifestyle.com.lifestyle.model.User;

public class LoginController extends BaseController {

    private IUserInteractor interactor;


    public LoginController(Activity activity, Fragment fragment) {
        super(activity, fragment);
        interactor = new UserInteractor();
    }

    public void login(String email) {
        if (networkAvailable()) {
            getFragment().startLoading();
            interactor.loginSocial(email, callback);
        } else {
            showAlertConnection();
            getFragment().endLoading();
        }
    }

    private RequestCallback<User> callback = new RequestCallback<User>() {
        @Override
        public void success(User user) {
            SharedPrefManager.setObject(Constants.USER, user);
            getFragment().endLoading();
            if(user.getBirthday().isEmpty()){
                // open calc calory
                launchActivityWithFinish(CalcCaloryActivity.class);
            }else {
                launchActivityWithFinish(HomeActivity.class);
            }
        }

        @Override
        public void failed(String msg) {
            showMessage(msg);
            getFragment().endLoading();
        }
    };

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            login(account.getEmail());

        } catch (ApiException e) {
            showErrorMessage(e.getMessage());
            getFragment().endLoading();
        }
    }
}
