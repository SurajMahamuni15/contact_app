package com.example.contentproviderdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.contentproviderdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var list: MutableLiveData<List<ContactModel>>? = null
    private lateinit var adapter: ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list?.postValue(getContact())
        initializedAdapter()
    }

    private fun initializedAdapter() {
        adapter = ContactAdapter()
        list?.observe(this) {
            val contactList = it as ArrayList<ContactModel>
            adapter.setData(contactList)
        }
        binding.contactRv.adapter = adapter
    }

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                list?.postValue(getContact())
            }
        }

    @SuppressLint("Range")
    private fun getContact(): ArrayList<ContactModel> {
        val contact = ArrayList<ContactModel>()
        checkSkd {
            isPermissionGranted(this, android.Manifest.permission.READ_CONTACTS) {
                if (it) {
                    val contactResolver = applicationContext.contentResolver
                    val cursor =
                        contentResolver.query(
                            ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null
                        )
                    if (cursor?.moveToFirst() == true) {
                        while (cursor.moveToNext()) {
                            contact.add(
                                ContactModel(
                                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
                                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                                )
                            )
                        }
                    }
                } else {
                    registerForActivityResult.launch(android.Manifest.permission.READ_CONTACTS)
                }
            }
        }
        return contact
    }

    private inline fun checkSkd(call: () -> Unit) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            call.invoke()
        }
    }

    private inline fun isPermissionGranted(
        context: Context,
        permission: String,
        call: (Boolean) -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            call.invoke(true)
        } else {
            call.invoke(false)
        }
    }
}