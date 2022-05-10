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
import kotlinx.android.synthetic.main.activity_profile_fragment_change_password.*

class ChangePasswordFragment : Fragment() {
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.activity_profile_fragment_change_password, container, false)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectUI()
        setUpView()
    }

    private fun connectUI() {
        profileViewModel.noti.observe(viewLifecycleOwner, Observer {
            fragment_change_password_tvNotification.text = it
        })
        profileViewModel.isChangePassSuccess.observe(viewLifecycleOwner, Observer {
            if (it == true){
                showDialog()
            }
        })
    }

    private fun setUpView() {
        fragment_change_password_ivBack.setOnClickListener { finishFragment() }
        fragment_change_password_btnUpdate.setOnClickListener {
            val oldPassword = fragment_change_password_etPasswordRecent.text.toString().trim()
            val newPassword = fragment_change_password_etNewPassword.text.toString().trim()
            val confirmPassword = fragment_change_password_etConfirmPassword.text.toString().trim()
            var checkPassword = profileViewModel.checkPasswordInput(newPassword)
            if (checkPassword){
                fragment_change_password_tvCheckPassword.visibility = View.GONE
            }else{
                fragment_change_password_tvCheckPassword.visibility = View.VISIBLE
            }
            var match = profileViewModel.checkPasswordMatch(newPassword,confirmPassword)
            if (match){
                if(checkPassword){
                    profileViewModel.onChangePassword(newPassword, oldPassword)
                }
                fragment_change_password_tvNotMatch.visibility = View.GONE
            }else{
                fragment_change_password_tvNotMatch.visibility = View.VISIBLE
            }
        }
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
        tvTitle.text = "Change password"
        tvContent.text = "Change password successfully"
        btnOk?.setOnClickListener {
            dialog.dismiss()
            finishFragment()
        }

        dialog.show()
    }
    private fun finishFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.commit()
    }
}