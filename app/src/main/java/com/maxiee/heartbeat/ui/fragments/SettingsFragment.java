package com.maxiee.heartbeat.ui.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.backup.BackupAllTask;
import com.maxiee.heartbeat.backup.BackupManager;
import com.maxiee.heartbeat.backup.RestoreAllTask;
import com.maxiee.heartbeat.common.ThemeUtils;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.ui.CrashListActivity;
import com.maxiee.heartbeat.ui.PatternActivity;

/**
 * Created by maxiee on 15-6-28.
 * Thanks to fython/NHentai-android (https://github.com/fython/NHentai-android)
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private final static int RESTORE_REQUEST = 1127;
    private final static int RESTORE_ALL_REQUEST = 1128;
    private final static String GITHUB_URL = "https://github.com/maxiee/HeartBeat";
    private final static String Weibo_URL = "http://weibo.com/maxiee";
    private final static String EMAIL = "maxieewong@gmail.com";
    private final static String XXXXL = "http://coolapk.com/u/421881";
    private final static String DONATE_MAIL = "maxer_ray@163.com";

    private Preference mThemePref;
    private Preference mPatternPref;
    private Preference mVersionPref;
    private Preference mGitHubPref;
    private Preference mWeiboPref;
    private Preference mCrashPref;
    private Preference mEmailPref;
    private Preference mThanksXXXXL;
    private Preference mBackupSDPref;
    private Preference mBackupCloudPref;
    private Preference mBackupAllPref;
    private Preference mRestorePref;
    private Preference mRestoreAllPref;
    private Preference mDonatePref;
    private Preference mLicensesPref;
    private SharedPreferences mPrefs;

    private String mPattern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mThemePref =        findPreference("change_theme");
        mPatternPref =      findPreference("pattern");
        mVersionPref =      findPreference("version");
        mGitHubPref =       findPreference("github");
        mWeiboPref =        findPreference("weibo");
        mCrashPref =        findPreference("crash");
        mEmailPref =        findPreference("email");
        mThanksXXXXL =      findPreference("icon_thanks");
        mBackupSDPref =     findPreference("backup_sd");
        mBackupCloudPref =  findPreference("backup_cloud");
        mBackupAllPref =    findPreference("backup_all");
        mRestorePref =      findPreference("restore");
        mRestoreAllPref =   findPreference("restore_all");
        mDonatePref =       findPreference("donate");
        mLicensesPref =     findPreference("licenses");

        String version = "Unknown";
        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            version += " (" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode + ")";
        } catch (Exception e) {e.printStackTrace();}

        mPrefs = getActivity().getSharedPreferences("hb", Context.MODE_PRIVATE);
        mVersionPref.setSummary(version);
        mGitHubPref.setOnPreferenceClickListener(this);
        mGitHubPref.setSummary(GITHUB_URL);
        mDonatePref.setOnPreferenceClickListener(this);
        mDonatePref.setSummary(getString(R.string.donate_summary) + " " + DONATE_MAIL);
        mWeiboPref.setOnPreferenceClickListener(this);
        mWeiboPref.setSummary(Weibo_URL);
        mCrashPref.setSummary(getString(R.string.settings_crash_summary));
        mCrashPref.setOnPreferenceClickListener(this);
        mEmailPref.setSummary(EMAIL);
        mEmailPref.setOnPreferenceClickListener(this);
        mThanksXXXXL.setOnPreferenceClickListener(this);
        mBackupSDPref.setOnPreferenceClickListener(this);
        mBackupCloudPref.setOnPreferenceClickListener(this);
        mBackupAllPref.setOnPreferenceClickListener(this);
        mRestorePref.setOnPreferenceClickListener(this);
        mRestoreAllPref.setOnPreferenceClickListener(this);
        mThemePref.setOnPreferenceClickListener(this);
        mLicensesPref.setOnPreferenceClickListener(this);
        initPattern();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPattern();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mWeiboPref) {
            Uri uri = Uri.parse(Weibo_URL);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        }
        if (preference == mGitHubPref) {
            Uri uri = Uri.parse(GITHUB_URL);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        }
        if (preference == mCrashPref) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.setClass(getActivity(), CrashListActivity.class);
            startActivity(intent);
            return true;
        }
        if (preference == mPatternPref) {
            onPatternClick();
            return true;
        }
        if (preference == mThanksXXXXL) {
            Uri uri = Uri.parse(XXXXL);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        }
        if (preference == mBackupSDPref) {
            if (BackupManager.backupSD(getActivity()) == null) {
                Snackbar.make(getView(), getString(R.string.backup_failed), Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(getView(), getString(R.string.backup_ok), Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }
        if (preference == mBackupCloudPref) {
            String ret = BackupManager.backupCloud(getActivity());
            if (ret != null) Snackbar.make(getView(), ret, Snackbar.LENGTH_LONG).show();
            return true;
        }
        if (preference == mBackupAllPref) {
            new BackupAllTask(getActivity()).execute();
        }
        if (preference == mRestorePref) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.setType("file/*");
            startActivityForResult(i, RESTORE_REQUEST);
            return true;
        }
        if (preference == mRestoreAllPref) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.setType("file/*");
            startActivityForResult(i, RESTORE_ALL_REQUEST);
            return true;
        }
        if (preference == mEmailPref) {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setData(Uri.parse("mailto:"));
            i.putExtra(Intent.EXTRA_EMAIL, new String[] {EMAIL});
            if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(i);
            }
            return true;
        }
        if (preference == mDonatePref) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Donate", DONATE_MAIL);
            clipboard.setPrimaryClip(clip);
            Snackbar.make(getView(), getString(R.string.copy_finished), Snackbar.LENGTH_LONG).show();
            return true;
        }
        if (preference == mThemePref) {
            ThemeUtils.chooseThemeDialog(getActivity());
            return true;
        }
        if (preference == mLicensesPref) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/licenses.html");
            new AlertDialog.Builder(getActivity())
                    .setView(webView)
                    .setCancelable(true)
                    .show();
        }
        return false;
    }

    private void initPattern() {
        mPattern = mPrefs.getString(
                "pattern",
                ""
        );
        if (mPattern.isEmpty()) {
            mPatternPref.setSummary(getString(R.string.pattern_empty));
        } else {
            mPatternPref.setSummary(getString(R.string.setted));
        }
        mPatternPref.setOnPreferenceClickListener(this);
    }

    private void onPatternClick() {
        if (mPattern.isEmpty()) {
            Intent i = new Intent();
            i.setClass(getActivity(), PatternActivity.class);
            i.putExtra(PatternActivity.ACTION, PatternActivity.SET);
            startActivity(i);
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
            builder.setTitle(getString(R.string.choice));
            builder.setItems(
                    new String[]{
                            getString(R.string.settings_pattern_cancel),
                            getString(R.string.settings_pattern_change)
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent i = new Intent(getActivity(), PatternActivity.class);
                                i.putExtra(PatternActivity.ACTION, PatternActivity.CANCEL);
                                startActivity(i);
                                dialog.dismiss();
                            } else if (which == 1) {
                                Intent i = new Intent(getActivity(), PatternActivity.class);
                                i.putExtra(PatternActivity.ACTION, PatternActivity.MODIFY);
                                startActivity(i);
                                dialog.dismiss();
                            }
                        }
                    }
            );
            builder.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESTORE_REQUEST && resultCode == Activity.RESULT_OK) {
            String ret = BackupManager.restore(getActivity(), data);
            DataManager dm = DataManager.getInstance(getActivity());
            dm.reload();
            Snackbar.make(getView(), ret, Snackbar.LENGTH_LONG).show();
        }
        if (requestCode == RESTORE_ALL_REQUEST && resultCode == Activity.RESULT_OK) {
            new RestoreAllTask(getActivity()).execute(data);
        }
    }
}
