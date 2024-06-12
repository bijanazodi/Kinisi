package com.kinisi.trailtracker.ui.profile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kinisi.trailtracker.R
import com.kinisi.trailtracker.databinding.FragmentHomeBinding
import com.kinisi.trailtracker.databinding.FragmentProfileBinding
import android.widget.Button
import androidx.navigation.findNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.kinisi.trailtracker.MainActivity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.github.mikephil.charting.data.Entry
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.osmdroid.util.Distance

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var Distance: java.util.ArrayList<Double> = java.util.ArrayList()
    var FloatDistance = 7f
    var FloatDistance2 = 0f
    var FloatDistance3 = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Brings to reviews activity on review button click
    /*    val reviewBtn: Button = binding.reviewsButton

        reviewBtn.setOnClickListener {
            val intent = Intent(context, ReviewsActivity::class.java)
            startActivity(intent)
        }

        //Brings to history activity on history button click
        val historyBtn: Button = binding.historyButton

        historyBtn.setOnClickListener {
            val intent = Intent(context, HistoryActivity::class.java)
            startActivity(intent)
        }
*/
        //Brings to Stats activity on stats button click
        val statsBtn: Button = binding.statsButton

        statsBtn.setOnClickListener {
            val intent = Intent(context, Stats::class.java)
            startActivity(intent)
        }
        //Brings to UpdateProfile on settings button click
        val settingsBtn: Button = binding.settingsButton

        settingsBtn.setOnClickListener {
            val intent = Intent(context, UpdateProfile::class.java)
            startActivity(intent)
        }

        val shareBtn: Button = binding.shareButton
        shareBtn.setOnClickListener {
            copyTextToClipboard()
            pasteTextFromClipboard()
        }

        val fbBtn: ImageButton = binding.fbButton
        fbBtn.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/"))
            startActivity(browserIntent)
        }

        val twitterBtn: ImageButton = binding.twitterButton
        twitterBtn.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/"))
            startActivity(browserIntent)
        }

        val instaBtn: ImageButton = binding.instaButton
        instaBtn.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.instagram.com/"))
            startActivity(browserIntent)
        }
        return root
    }

    private fun copyTextToClipboard() {
        val docRef =  Firebase.firestore.collection("userTotalDistance").document("ciJDSJnCFn6yCU9et7qK")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    Distance = document.get("userTotalDistance") as java.util.ArrayList<Double>
                    val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    var clipData = ClipData.newPlainText("simple text", "I just traveled " + Distance[0] + " miles with Kinisi!")
                    clipboardManager.setPrimaryClip(clipData)

                    Toast.makeText(requireContext(),
                        "Copied to the Clipboard", Toast.LENGTH_SHORT).show()


                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }



    }

    private fun pasteTextFromClipboard() {
        var pasteData: String = ""
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var item = clipboardManager.primaryClip?.getItemAt(0)?.text
        pasteData = item as String
        print(pasteData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}