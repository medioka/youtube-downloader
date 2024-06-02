package com.example.youtubedownloader.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.example.youtubedownloader.R
import com.example.youtubedownloader.core.utils.DialogUtils
import com.example.youtubedownloader.core.utils.YoutubeModuleUtils
import com.example.youtubedownloader.custom.CustomDialog
import com.example.youtubedownloader.custom.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : PreferenceFragmentCompat(),
    OnSharedPreferenceChangeListener {
    private lateinit var themePreference: Preference
    private lateinit var languagePreference: Preference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var updateChannelPreference: Preference
    private lateinit var versionPreference: Preference
    private lateinit var autoUpdatePreference: SwitchPreference

    private lateinit var themeKeyParsed: String
    private lateinit var languageKeyParsed: String
    private lateinit var versionKeyParsed: String
    private lateinit var updateKeyParsed: String
    private lateinit var autoUpdateKeyParsed: String

    private lateinit var currentTheme: String
    private lateinit var currentLanguage: String

    private lateinit var languageEntries: Array<String>
    private lateinit var themeEntries: Array<String>
    private lateinit var updateChannelEntries: Array<String>
    private lateinit var customDialog: CustomDialog
    private lateinit var youtubeModuleUtils: YoutubeModuleUtils
    private lateinit var dialogUtils: DialogUtils


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        customDialog = CustomDialog(requireActivity())
        youtubeModuleUtils = YoutubeModuleUtils(requireContext())
        dialogUtils = DialogUtils(requireActivity())
        initialize()
        setupTheme()
        setupLanguage()
        setupModule()
        setupUpdateChannel()
    }

    private fun initialize() {
        themeKeyParsed = getString(R.string.key_theme)
        languageKeyParsed = getString(R.string.key_language)
        updateKeyParsed = getString(updateChannelKey)
        versionKeyParsed = getString(versionKey)
        autoUpdateKeyParsed = getString(autoUpdateKey)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        themePreference = findPreference(themeKeyParsed)!!
        languagePreference = findPreference(languageKeyParsed)!!
        updateChannelPreference =
            findPreference(updateKeyParsed)!!
        versionPreference = findPreference(versionKeyParsed)!!
        autoUpdatePreference =
            findPreference(autoUpdateKeyParsed)!!

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        languageEntries = Language.entries.map { it.name }.toTypedArray()
        themeEntries = ThemeMode.entries.map { it.name }.toTypedArray()
        updateChannelEntries = arrayOf("nightly", "stable")

        currentTheme = sharedPreferences.getString(themeKeyParsed, themeEntries[0])!!
        currentLanguage = sharedPreferences.getString(languageKeyParsed, languageEntries[0])!!
        val currentVersion =
            sharedPreferences.getString(versionKeyParsed, "") ?: throw Exception("NUll preference")
        val updateChannelValue = sharedPreferences.getString(
            updateKeyParsed,
            "nightly"
        ) ?: throw Exception("NUll preference")
        val autoUpdateDefaultValue = sharedPreferences.getBoolean(autoUpdateKeyParsed, true)

        themePreference.summary = currentTheme
        languagePreference.summary = currentLanguage
        versionPreference.summary = currentVersion
        updateChannelPreference.summary = updateChannelValue
        autoUpdatePreference.isChecked = autoUpdateDefaultValue

        themeUpdater(ThemeMode.valueOf(currentTheme))
        languageUpdater(Language.valueOf(currentLanguage))
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            themeKeyParsed -> {
                val updatedTheme = sharedPreferences?.getString(key, themeEntries[0])!!
                if (currentTheme != updatedTheme) {
                    currentTheme = updatedTheme
                    themePreference.summary = updatedTheme
                    themeUpdater(ThemeMode.valueOf(updatedTheme))
                }
            }

            languageKeyParsed -> {
                val updatedLanguage = sharedPreferences?.getString(key, languageEntries[0])!!
                if (currentLanguage != updatedLanguage) {
                    currentLanguage = updatedLanguage
                    languageUpdater(Language.valueOf(updatedLanguage))
                    languagePreference.summary = updatedLanguage
                }
            }

            updateKeyParsed -> {
                val updatedChannel = sharedPreferences?.getString(key, "nightly")!!
                updateChannelPreference.summary = updatedChannel
            }

            versionKeyParsed -> {
                val updatedVersion = sharedPreferences?.getString(key, "")!!
                versionPreference.summary = updatedVersion
            }

            autoUpdateKeyParsed -> {
                val updatedStatus = sharedPreferences?.getBoolean(key, true)!!
                autoUpdatePreference.isChecked = updatedStatus
            }
        }

    }

    private fun setupTheme() {
        themePreference.setOnPreferenceClickListener { _ ->
            createDialogTheme()
            true
        }
    }

    private fun setupLanguage() {
        findPreference<Preference>(languageKeyParsed)?.setOnPreferenceClickListener { _ ->
            createDialogLanguage()
            true
        }
    }

    private fun setupModule() {
        val loadingDialog = LoadingDialog(requireActivity())
        findPreference<Preference>(requireActivity().getString(R.string.update_module))?.setOnPreferenceClickListener { _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                loadingDialog.startDialog().apply {
                    try {
                        withContext(Dispatchers.IO) { youtubeModuleUtils.updateYoutubeModule() }
                        youtubeModuleUtils.getCurrentModuleVersion()
                    } catch (e: Exception) {
                        dialogUtils.createToast("Error: $e")
                    } finally {
                        loadingDialog.closeDialog()
                    }
                }
            }
            true
        }
    }

    private fun setupUpdateChannel() {
        updateChannelPreference.setOnPreferenceClickListener {
            createDialogUpdateChannel()
            true
        }
    }

    private fun createDialogUpdateChannel() {
        val okClickListener = { index: Int ->
            sharedPreferences.edit()
                .putString(updateKeyParsed, updateChannelEntries[index])
                .apply()
        }
        val currentUpdateChannel =
            sharedPreferences.getString(updateKeyParsed, "nightly")!!
        customDialog.createChooserDialog(
            itemList = updateChannelEntries,
            currentItem = currentUpdateChannel,
            okClickListener
        )
    }

    private fun createDialogLanguage() {
        val okClickListener = { index: Int ->
            sharedPreferences.edit()
                .putString(languageKeyParsed, languageEntries[index])
                .apply()
        }
        customDialog.createChooserDialog(
            itemList = languageEntries,
            currentItem = currentLanguage,
            okClickListener
        )
    }

    private fun createDialogTheme() {
        val okClickListener = { index: Int ->
            sharedPreferences.edit()
                .putString(themeKeyParsed, themeEntries[index])
                .apply()
        }
        customDialog.createChooserDialog(
            itemList = themeEntries,
            currentItem = currentTheme,
            okClickListener
        )
    }

    private fun themeUpdater(themeMode: ThemeMode) {
        when (themeMode) {
            ThemeMode.Dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            ThemeMode.Light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun languageUpdater(language: Language) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language.locale)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    companion object {
        val updateChannelKey = R.string.key_updatechannel
        val versionKey = R.string.key_version
        val autoUpdateKey = R.string.key_autoupdate
    }
}

enum class Language(val locale: String) {
    English("en-us"),
    Bahasa("in")
}

enum class ThemeMode {
    System,
    Light,
    Dark
}

