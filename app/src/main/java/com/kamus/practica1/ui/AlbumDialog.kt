package com.kamus.practica1.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
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
        albumSongs = 0
    ),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit
): DialogFragment() {
    private var _binding: AlbumDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog

    private var saveButton: Button? = null

    private lateinit var repository: AlbumRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = AlbumDialogBinding.inflate(requireActivity().layoutInflater)

        repository = (requireContext().applicationContext as AlbumsDBApp).repository

        builder = AlertDialog.Builder(requireContext())

        binding.apply {
            tfAlbumTitle.setText(album.albumTitle)
            tfAlbumArtist.setText(album.albumArtist)
            tfAlbumYear.setText(album.albumYear)
            tfAlbumSongs.setText(album.albumSongs.toString())
        }

        dialog = if(newAlbum){
            /* Create */
            buildDialog("Guardar", "Cancelar", {
                album.albumTitle = binding.tfAlbumTitle.text.toString()
                album.albumArtist = binding.tfAlbumArtist.text.toString()
                album.albumYear = binding.tfAlbumYear.text.toString()
                album.albumSongs = binding.tfAlbumSongs.text.toString().toInt()

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
            buildDialog("Actualizar", "Borrar",{
                album.albumTitle = binding.tfAlbumTitle.text.toString()
                album.albumArtist = binding.tfAlbumArtist.text.toString()
                album.albumYear = binding.tfAlbumYear.text.toString()
                album.albumSongs = binding.tfAlbumSongs.text.toString().toInt()

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
            binding.tfAlbumSongs.text.toString().isNotEmpty())

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
}