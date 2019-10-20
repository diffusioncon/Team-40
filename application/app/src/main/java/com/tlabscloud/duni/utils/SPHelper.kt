package com.tlabscloud.duni.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SPHelper constructor(context: Context) {
    private val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val walletProp = "wallet"
    private val walletKeyProp = "wallet_private_key"
    private val isLoginMockProp = "is_login_mock"

    fun getWallet() = getPreferenceOrDefault(walletProp, "")

    fun getWalletKey() = getPreferenceOrDefault(walletKeyProp, "")

    fun isLoginMock(): Boolean = pref.getBoolean(isLoginMockProp, false)

    fun setLoginMock(value: Boolean) {
        pref.edit().putBoolean(isLoginMockProp, value).apply()
    }

    /*suspend fun storeWallet(wallet: WalletDto): Boolean = withContext(IO) {
        pref.edit()
            .putString(walletProp, wallet.address)
            .putString(walletKeyProp, wallet.privateKey)
            .commit()
    }*/

    fun isWalletPresented(): Boolean = getWallet().isNotEmpty()

    private fun getPreferenceOrDefault(preference: String, default: String): String {
        val result = pref.getString(preference, default)
        if (result == null || result.isEmpty()) {
            return default
        }
        return result
    }
}
