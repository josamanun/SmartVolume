package com.amaya.smartvolume.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class FragmentManager {

    public static void changeBetweenFragment(FragmentActivity fragmentActivity, String origin, String destination, Fragment destinationFragment, int contentId) {

        if (fragmentActivity.getSupportFragmentManager().findFragmentByTag(destination) != null) {
            //if the fragment exists, show it.
            showFragmentActivity(fragmentActivity, destination);
        } else {
            //if the fragment does not exist, add it to fragment manager.
            addFragmentActivity(fragmentActivity, destinationFragment, destination, contentId);
        }
        if (fragmentActivity.getSupportFragmentManager().findFragmentByTag(origin) != null) {
            //if the other fragment is visible, hide it.
            hideFragmentActivity(fragmentActivity, origin);
        }
    }

    private static void showFragmentActivity(FragmentActivity fragmentActivity, String fragmentTag) {
        fragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .show(fragmentActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag))
                .commit();
        fragmentActivity.getSupportFragmentManager().executePendingTransactions();
    }

    private static void hideFragmentActivity(FragmentActivity fragmentActivity, String fragmentTag) {
        fragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag))
                .commit();
        fragmentActivity.getSupportFragmentManager().executePendingTransactions();
    }

    public static void addFragmentActivity(FragmentActivity fragmentActivity, Fragment fragment, String fragmentTag, int contentId) {
        fragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .add(contentId, fragment, fragmentTag)
                .commit();
    }

}
