package com.sigma.sportsup.ui.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sigma.sportsup.UserViewModel
import com.sigma.sportsup.data.Comment
import com.sigma.sportsup.databinding.FragmentEventCommentsBinding
import com.sigma.sportsup.ui.search.SearchViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat

class EventCommentsFragment : Fragment() {


    private var _binding: FragmentEventCommentsBinding? = null

    private lateinit var userViewModel: UserViewModel

    private val binding get() = _binding!!

    private lateinit var eventsViewModel: EventDetailsViewModel

    private var eventId: String? = null
    private var eventName: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentEventCommentsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        eventsViewModel = ViewModelProvider(this).get(EventDetailsViewModel::class.java)


        if (arguments != null) {
            eventId = arguments?.getString("eventId")
            eventName = arguments?.getString("eventName")
        }

        eventId?.let { eventsViewModel.getEventComments(it, eventName!!) }

        eventsViewModel.gameCommentsLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val adapter = EventCommentsItemAdapter(requireContext(), it)
                binding.commentRecyclerView.adapter = adapter
                binding.txtNoComments.visibility = View.INVISIBLE
            } else {
                binding.txtNoComments.visibility = View.VISIBLE
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val commentEditText = binding.newCommentTextInputEditText
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnSendComment.setOnClickListener {

            val comment = commentEditText.text.toString()
            if (comment.isNotEmpty()) {
                val time = SimpleDateFormat("HH:mm a").format(System.currentTimeMillis())
                val date = DateFormat.getDateInstance().format(System.currentTimeMillis()) + " " + time
                val userInfo = userViewModel.currentUser.value!!
                eventsViewModel.addComment(
                    eventId!!,
                    eventName!!,
                    Comment(comment, date, "", userInfo.name!!, "", userInfo.id!!)
                )
                commentEditText.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}