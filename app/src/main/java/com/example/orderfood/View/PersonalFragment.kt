package com.example.orderfood.View

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.orderfood.R
import com.example.orderfood.SignInActivity
import com.example.orderfood.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_fragment_personal.*

class PersonalFragment : Fragment() {
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.activity_profile_fragment_personal, container, false)
        profileViewModel = activity?.let { ViewModelProvider(it).get(ProfileViewModel::class.java) }!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        connectUI()
    }

    private fun connectUI() {
//        profileViewModel.onGetProfile()
//        profileViewModel.userName.observe(viewLifecycleOwner, Observer {
//            fragment_personal_tvFullName.text = it
//        })
//        profileViewModel.userEmail.observe(viewLifecycleOwner, Observer {
//            fragment_personal_tvEmail.text = it
//        })
//        profileViewModel.isUpdateEmailSuccess.observe(viewLifecycleOwner, Observer {
//            fragment_personal_tvEmail.text =  profileViewModel.userEmail.value
//            profileViewModel.isUpdateEmailSuccess.value = false
//        })
//        profileViewModel.isUpdatenameSuccess.observe(viewLifecycleOwner, Observer {
//            fragment_personal_tvEmail.text =  profileViewModel.userName.value
//            profileViewModel.isUpdatenameSuccess.value = false
//        })
    }

    private fun setUpView() {
        fragment_personal_tvUpdateName.setOnClickListener { replaceFragment(UpdateNameFragment()) }
        fragment_personal_tvUpdateEmail.setOnClickListener { replaceFragment(UpdateEmailFragment()) }
        fragment_personal_tvChangePassword.setOnClickListener { replaceFragment(ChangePasswordFragment()) }
        fragment_personal_tvAddress.setOnClickListener { replaceFragment(UpdateAddressFragment()) }
        fragment_personal_tvUpdateAddress.setOnClickListener { replaceFragment(UpdateAddressFragment()) }
        fragment_personal_tvUpdateCard.setOnClickListener { replaceFragment(UpdateCardFragment()) }
        fragment_personal_tvCard.setOnClickListener { replaceFragment(UpdateCardFragment()) }
        fragment_personal_tvUpdateComment.setOnClickListener { replaceFragment(MyCommentsFragment()) }
        fragment_personal_tvComment.setOnClickListener { replaceFragment(MyCommentsFragment()) }
        fragment_personal_tvSignOut.setOnClickListener {
            profileViewModel.onSignOut()
            startActivity(Intent(activity, SignInActivity::class.java))
            activity?.finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.act_profile_container, fragment)
        transaction.addToBackStack("personal")
        transaction.commit()
    }
}