package com.kamus.practica1.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.kamus.practica1.R
import com.kamus.practica1.application.AlbumsDBApp
import com.kamus.practica1.data.AlbumRepository
import com.kamus.practica1.data.db.model.AlbumEntity
import com.kamus.practica1.databinding.AlbumDialogBinding
import kotlinx.coroutines.launch
import java.io.IOException

class AlbumDialog(
    private val newAlbum: Boolean = true,
    private var album: AlbumEntity = AlbumEntity(
        albumTitle = "",
        albumArtist = "",
        albumGenre = "",
        albumYear = "",
        albumSongs = ""
    ),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit,
    private val imageSelection: (String) -> Int
): DialogFragment() {
    private var _binding: AlbumDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog
    private var saveButton: Button? = null
    private lateinit var spinner: Spinner
    private var spinnerSelectedOption: String? = null
    private var spinnerSelectedId: Int = 0

    private lateinit var repository: AlbumRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = AlbumDialogBinding.inflate(requireActivity().layoutInflater)
        repository = (requireContext().applicationContext as AlbumsDBApp).repository
        builder = AlertDialog.Builder(requireContext())
        
        binding.apply {
            tfAlbumTitle.setText(album.albumTitle)
            tfAlbumArtist.setText(album.albumArtist)
            tfAlbumYear.setText(album.albumYear)
            tfAlbumSongs.setText(album.albumSongs)
            dialogImage.setImageResource(imageSelection(album.albumGenre))
        }

        /* Initialize Spinner */
        spinner = binding.spinnerGenre
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.spinnerOpt,
            android.R.layout.simple_spinner_item
        ). also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object: OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinnerSelectedOption = resources.getStringArray(R.array.spinnerOpt)[p2]
                spinnerSelectedId = p2
                saveButton?.isEnabled = validateFields(newAlbum)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        
        dialog = if(newAlbum){
            /* Create */
            buildDialog(getString(R.string.buttonCreate), getString(R.string.buttonCancel), {

                album.apply {
                    albumTitle = binding.tfAlbumTitle.text.toString()
                    albumArtist = binding.tfAlbumArtist.text.toString()
                    albumYear = binding.tfAlbumYear.text.toString()
                    albumSongs = binding.tfAlbumSongs.text.toString()
                    albumGenre = spinnerSelectedOption.toString()
                }

                try{
                    lifecycleScope.launch {
                        repository.insertAlbum(album)
                    }
                    message(getString(R.string.saved))
                    updateUI()
                }catch (e: IOException){
                    e.printStackTrace()
                    message(getString(R.string.saveError))
                }
            },{
                /* Cancelar */
            })
        }else{
            /* Update */

            spinner.setSelection(resources.getStringArray(R.array.spinnerOpt).indexOf(album.albumGenre))

            buildDialog(getString(R.string.buttonUpdate), getString(R.string.buttonDelete),{
                album.apply {
                    albumTitle = binding.tfAlbumTitle.text.toString()
                    albumArtist = binding.tfAlbumArtist.text.toString()
                    albumYear = binding.tfAlbumYear.text.toString()
                    albumSongs = binding.tfAlbumSongs.text.toString()
                    album.albumGenre = spinnerSelectedOption.toString()
                }

                try{
                    lifecycleScope.launch {
                        repository.updateAlbum(album)
                    }
                    message(getString(R.string.updated))
                    updateUI()
                }catch (e: IOException){
                    e.printStackTrace()
                    message(getString(R.string.updateError))
                }
            },{
                /* Delete */
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.dialogConfirmationTitle))
                    .setMessage(getString(R.string.dialogConfirmationMessage, album.albumTitle))
                    .setPositiveButton(getString(R.string.positiveButton)){ _, _ ->
                        try{
                            lifecycleScope.launch {
                                repository.deleteAlbum(album)
                            }
                            activity?.let {
                                message(it.getString(R.string.delete))
                            }
                            updateUI()
                        }catch (e: IOException){
                            e.printStackTrace()
                            message(getString(R.string.deleteError))
                        }
                    }
                    .setNegativeButton(getString(R.string.buttonCancel)){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            })
        }
        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        val alertDialog = dialog as AlertDialog
        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.tfAlbumTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields(newAlbum)
            }
        })

        binding.tfAlbumArtist.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields(newAlbum)
            }
        })

        binding.tfAlbumYear.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields(newAlbum)
            }
        })

        binding.tfAlbumSongs.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields(newAlbum)
            }
        })
    }

    private fun validateFields(newAlbum: Boolean): Boolean{
        return if(newAlbum){
            (binding.tfAlbumTitle.text.toString().isNotEmpty() &&
             binding.tfAlbumArtist.text.toString().isNotEmpty() &&
             binding.tfAlbumYear.text.toString().isNotEmpty() &&
             binding.tfAlbumSongs.text.toString().isNotEmpty() &&
             spinnerSelectedId != 0)
        }else{
            (binding.tfAlbumTitle.text.toString().isNotEmpty() &&
            binding.tfAlbumArtist.text.toString().isNotEmpty() &&
            binding.tfAlbumYear.text.toString().isNotEmpty() &&
            binding.tfAlbumSongs.text.toString().isNotEmpty() &&
            spinnerSelectedId != 0)
        }

    }




    private fun buildDialog(btn1: String, btn2: String, positiveButton: () -> Unit, negativeButton: () -> Unit): Dialog =
        builder.setView(binding.root)
            .setTitle(getString(R.string.dialogCreateTitle))
            .setPositiveButton(btn1) { _, _ ->
                positiveButton()
            }
            .setNegativeButton(btn2){ _, _ ->
                negativeButton()
            }
            .create()
}