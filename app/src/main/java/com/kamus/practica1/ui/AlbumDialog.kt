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
import android.widget.Toast
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
    private val message: (String) -> Unit
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
                saveButton?.isEnabled = validateFields()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        
        dialog = if(newAlbum){
            /* Create */
            buildDialog("Guardar", "Cancelar", {

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
                    message("Juego guardado exitosamente")
                    updateUI()
                }catch (e: IOException){
                    e.printStackTrace()
                    message("error al guardar el juego")
                }
            },{
                /* Cancelar */
            })
        }else{
            /* Update */

            spinner.setSelection(resources.getStringArray(R.array.spinnerOpt).indexOf(album.albumGenre))

            buildDialog("Actualizar", "Borrar",{
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
                    message("Juego actualizado exitosamente")
                    updateUI()
                }catch (e: IOException){
                    e.printStackTrace()
                    message("Error al actualizar el juego")
                }
            },{
                /* Delete */
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Realmente quieres eliminar el album ${album.albumTitle}?")
                    .setPositiveButton("Aceptar"){ _, _ ->
                        try{
                            lifecycleScope.launch {
                                repository.deleteAlbum(album)
                            }
                            message("Juego eliminado exitosamente")
                            updateUI()
                        }catch (e: IOException){
                            e.printStackTrace()
                            message("Error al eliminar el juego")
                        }
                    }
                    .setNegativeButton("Cancelar"){ dialog, _ ->
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
        saveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.tfAlbumTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tfAlbumArtist.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tfAlbumYear.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tfAlbumSongs.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })
    }

    private fun validateFields() = (
            binding.tfAlbumTitle.text.toString().isNotEmpty() &&
            binding.tfAlbumArtist.text.toString().isNotEmpty() &&
            binding.tfAlbumYear.text.toString().isNotEmpty() &&
            binding.tfAlbumSongs.text.toString().isNotEmpty() &&
            spinnerSelectedId != 0 &&
            spinnerSelectedId != resources.getStringArray(R.array.spinnerOpt).indexOf(album.albumGenre))

    private fun buildDialog(btn1: String, btn2: String, positiveButton: () -> Unit, negativeButton: () -> Unit): Dialog =
        builder.setView(binding.root)
            .setTitle("Album")
            .setPositiveButton(btn1, DialogInterface.OnClickListener { _, _ ->
                positiveButton()
            })
            .setNegativeButton(btn2){ _, _ ->
                negativeButton()
            }
            .create()

    fun imageSelection(image: String): Int{
        return when(image){
            "Brit Pop" -> R.drawable.britpop
            "Rock" -> R.drawable.rock
            "Metal" -> R.drawable.metal
            "House" -> R.drawable.house
            "Electronic" -> R.drawable.electronic
            "Indie" -> R.drawable.indie
            "Jazz" -> R.drawable.jazz
            else -> R.drawable.headphones
        }
    }
}