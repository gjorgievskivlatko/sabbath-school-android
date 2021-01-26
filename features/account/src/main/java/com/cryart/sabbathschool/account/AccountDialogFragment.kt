/*
 * Copyright (c) 2021. Adventech <info@adventech.io>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cryart.sabbathschool.account

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.cryart.sabbathschool.account.databinding.SsFragmentAccountBinding
import com.cryart.sabbathschool.core.misc.SSConstants
import com.cryart.sabbathschool.core.navigation.AppNavigator
import com.cryart.sabbathschool.core.navigation.Destination
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountDialogFragment : AppCompatDialogFragment() {

    @Inject
    lateinit var appNavigator: AppNavigator

    private val viewModel: AccountViewModel by viewModels()

    private var binding: SsFragmentAccountBinding? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setGravity(Gravity.TOP or Gravity.CENTER)
        binding = SsFragmentAccountBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding?.root)
        }

        viewModel.userInfoLiveData.observe(
            viewLifecycleOwner,
            {
                binding?.apply {
                    it.photo?.let {
                        userAvatar.load(it) {
                            error(R.drawable.ic_account_circle)
                            transformations(CircleCropTransformation())
                        }
                    }
                    userName.text = it.displayName ?: getString(R.string.ss_menu_anonymous_name)
                    userEmail.text = it.email ?: getString(R.string.ss_menu_anonymous_email)
                }
            }
        )

        binding?.apply {
            chipSignOut.setOnClickListener {
                viewModel.logoutClicked()
                appNavigator.navigate(requireActivity(), Destination.LOGIN)
            }

            navSettings.setOnClickListener {
                appNavigator.navigate(requireActivity(), Destination.SETTINGS)
                dismiss()
            }

            navShare.setOnClickListener {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        String.format(
                            "%s - %s",
                            getString(R.string.ss_menu_share_app_text),
                            SSConstants.SS_APP_PLAY_STORE_LINK
                        )
                    )
                    type = "text/plain"
                }
                if (sendIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.ss_menu_share_app)))
                }
                dismiss()
            }

            navAbout.setOnClickListener {
                appNavigator.navigate(requireActivity(), Destination.ABOUT)
                dismiss()
            }
        }
    }
}
