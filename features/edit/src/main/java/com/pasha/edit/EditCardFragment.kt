package com.pasha.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasha.edit.R
import com.pasha.edit.databinding.FragmentEditCardBinding
import ezvcard.VCard


class EditCardFragment : Fragment() {
    private var _binding: FragmentEditCardBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vcard = VCard()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}