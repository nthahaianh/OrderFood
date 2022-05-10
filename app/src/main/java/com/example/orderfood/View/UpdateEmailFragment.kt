package com.example.orderfood.View

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.R
import com.example.orderfood.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_fragment_update_email.*

class UpdateEmailFragment : Fragment() {
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =
            inflater.inflate(R.layout.activity_profile_fragment_update_email, container, false)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    private fun finishFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }

    private fun connectUI() {
        profileViewModel.onGetProfile()
        profileViewModel.isUpdateEmailSuccess.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                showDialog()
            } else {
                fragment_update_email_tvNotification.text = profileViewModel.noti.value
            }
        })
        profileViewModel.noti.observe(viewLifecycleOwner, Observer {
            fragment_update_email_tvNotification.text = it
        })
    }

    private fun setUpView() {
        fragment_update_email_btnUpdate.setOnClickListener {
            val email = fragment_update_email_etEmail.text.toString().trim()
            val password = fragment_update_email_etPassword.text.toString().trim()
            profileViewModel.onUpdateEmail(email, password)
        }
        fragment_update_email_etEmail.setText(profileViewModel.userEmail.value)
        fragment_update_email_ivBack.setOnClickListener { finishFragment() }
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_success)

        val window: Window = dialog?.window ?: return
        val gravity = Gravity.CENTER

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes: WindowManager.LayoutParams = window.attributes
        windowAttributes.gravity = gravity
        window.attributes = windowAttributes

        if (Gravity.BOTTOM == gravity) dialog.setCancelable(true) else dialog.setCancelable(false)

        val tvTitle = dialog.findViewById<TextView>(R.id.dialog_success_tvTitle)
        val tvContent = dialog.findViewById<TextView>(R.id.dialog_success_tvContent)
        val btnOk = dialog.findViewById<Button>(R.id.dialog_success_btnOk)
        tvTitle.text = "Update email"
        tvContent.text = "Update email in successfully!"
        btnOk?.setOnClickListener {
            dialog.dismiss()
            finishFragment()
        }
        dialog.show()
    }
}