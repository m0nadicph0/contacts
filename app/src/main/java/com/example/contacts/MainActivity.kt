package com.example.contacts

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var contactsDao: ContactDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        contactsDao = (application as ContactsApp).db.contactDao()

        lifecycleScope.launch {
            contactsDao?.fetchAll()?.collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, contactsDao!!)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(
        contacts: java.util.ArrayList<Contact>,
        contactsDao: ContactDao
    ) {
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ContactsAdapter(contacts,{updateId ->
                updateRecordDialog(updateId,contactsDao)
            }){ deleteId->
                lifecycleScope.launch {
                    contactsDao.fetchById(deleteId).collect {
                        if (it != null) {
                            deleteRecordAlertDialog(deleteId, contactsDao, it)
                        }
                    }
                }

            }
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsList?.adapter = itemAdapter
    }

    private fun deleteRecordAlertDialog(id: Int, contactsDao: ContactDao, it: Contact) {
        lifecycleScope.launch {
            contactsDao.delete(Contact(id))
        }
    }

    private fun updateRecordDialog(id: Int, contactsDao: ContactDao) {
        val updateDialog = Dialog(this)
        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.add_contact_dialog)
        val etName  = updateDialog.findViewById<EditText>(R.id.etName)
        val etEmail = updateDialog.findViewById<EditText>(R.id.etEmail)
        val etPhone = updateDialog.findViewById<EditText>(R.id.etPhone)
        val btnAdd = updateDialog.findViewById<Button>(R.id.btAdd)
        val btnCancel = updateDialog.findViewById<Button>(R.id.btCancel)

        lifecycleScope.launch {
            contactsDao.fetchById(id).collect {
                if (it != null) {
                    etName.setText(it.name)
                    etEmail.setText(it.email)
                    etPhone.setText(it.phone)
                }
            }
        }
        btnAdd.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    contactsDao.update(Contact(id, name, phone, email))
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG)
                        .show()
                    updateDialog.dismiss() // Dialog will be dismissed
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        btnCancel.setOnClickListener{
            updateDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        updateDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contacts_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_contact -> {
                onAddContact()
                return true
            }
            R.id.action_share_contact -> {
                onShareContact()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onShareContact() {
        AlertDialog.Builder(this).setTitle("Alert").setMessage("Sharing Contact").show()
    }

    private fun onAddContact() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_contact_dialog)
        dialog.findViewById<Button>(R.id.btAdd).setOnClickListener{ onSaveContact(dialog)}
        dialog.findViewById<Button>(R.id.btCancel).setOnClickListener{ onCancelContact(dialog)}
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun onCancelContact(dialog: Dialog) {
        dialog.dismiss()
    }

    private fun onSaveContact(dialog: Dialog) {
        val name = dialog.findViewById<EditText>(R.id.etName).text.toString()
        val phone = dialog.findViewById<EditText>(R.id.etPhone).text.toString()
        val email = dialog.findViewById<EditText>(R.id.etEmail).text.toString()
        lifecycleScope.launch{
            contactsDao?.insert(Contact(name = name, phone = phone, email = email))
        }
        dialog.dismiss()
    }


}