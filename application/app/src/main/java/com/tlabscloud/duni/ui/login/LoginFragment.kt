package com.tlabscloud.duni.ui.login

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tlabscloud.duni.ui.login.LoginFragmentArgs.fromBundle
import com.tlabscloud.duni.MainActivity
import com.tlabscloud.duni.R
import com.tlabscloud.duni.utils.Result
import com.tlabscloud.duni.utils.ScopedFragment
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class LoginFragment : ScopedFragment(), KodeinAware {
    override val kodein by kodein()
    companion object {
        fun newInstance() = LoginFragment()
    }


    private val viewModel: LoginViewModel by instance()

    private val token by lazy {
        if (arguments != null) fromBundle(arguments!!).token else ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        handleJolocomAuthResponse()
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
       // viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        login.setOnClickListener {
            jolocomRedirect()
        }
        // TODO: Use the ViewModel
    }

    private fun handleJolocomAuthResponse() {
        if (token.isNotEmpty()) {
            postToken(token)
        } else {
           // requireActivity().toast("TokenEmpty")
        }
    }
    private fun postToken(token: String) = launch(Dispatchers.Main) {
        val result = viewModel.postToken(token)
        if (result is Result.Error) {
            requireActivity().toast(result.exception.message ?: "Please, try again later")
        }
    }

    private fun jolocomRedirect()  = launch {
        withContext(Dispatchers.Main) {
         login.isEnabled = false
            when (val tokenDto = viewModel.buildJolocomLink()) {
                is Result.Success -> try {
                    val link = tokenDto.data.token
                    viewModel.storeCredentialRequest(link)
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse("jolocomwallet://consent/$link")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    // if there is no Jolocom installed - launch Google Play
                    val jolocomPackageName = resources.getString(R.string.jolocom_package_name)
                    val googlePlayMarketLink = resources.getString(R.string.google_play_market_link_template)
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayMarketLink + jolocomPackageName)))
                    } catch (e1: ActivityNotFoundException) {
                        // if Google Play is not installed, redirect user to browser
                        val googlePlayWebLink = resources.getString(R.string.google_play_web_link_template)
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayWebLink + jolocomPackageName)))
                    }
                }
                is Result.Error -> {
                    requireActivity().toast(tokenDto.exception.localizedMessage)
                    requireActivity().findViewById<Button>(R.id.login).isEnabled = true
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val act = requireActivity() as MainActivity
        act.navBarVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        val act = requireActivity() as MainActivity
        act.navBarVisibility(true)
    }
}
