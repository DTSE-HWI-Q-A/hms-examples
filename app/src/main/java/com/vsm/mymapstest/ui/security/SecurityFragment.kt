package com.vsm.mymapstest.ui.security

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vsm.mymapstest.MainContract
import com.vsm.mymapstest.R
import com.vsm.mymapstest.model.Post
import com.vsm.mymapstest.ui.adapters.PostAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecurityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecurityFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val TAG: String = SecurityFragment::class.java.getSimpleName()
    private var mPresenter: MainContract.Presenter? = null

    private var mProgressBar: ProgressBar? = null
    private var mLayout: CoordinatorLayout? = null
    private var mPostList: RecyclerView? = null
    private val mPostAdapter: PostAdapter? = null
    private var mEmptyView: View? = null
    private var mErrorSnackbar: Snackbar? = null
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mPostLoadingListener: PostAdapter.PostAdapterCallback =
            object : PostAdapter.PostAdapterCallback {
                override fun onPostImageLoadingError(
                    error: String?,
                    e: Exception?
                ) {
                    mPresenter!!.onLoadPostImageError(error, e)
                }
            }
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_security, container, false)
        mProgressBar = rootView.findViewById<View>(R.id.progressBar) as ProgressBar
        mLayout =
            rootView.findViewById<View>(R.id.layout_coordinator) as CoordinatorLayout
        mPostList = rootView.findViewById<View>(R.id.post_list) as RecyclerView
        mEmptyView = rootView.findViewById<View>(R.id.empty_view)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        mPostList!!.layoutManager = layoutManager
        mPostList!!.adapter = mPostAdapter

        rootView.findViewById<View>(R.id.load_posts)
            .setOnClickListener(View.OnClickListener { onLoadPostsClick() })



        return rootView
    }

    override fun onResume() {
        super.onResume()
        mPresenter!!.start()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecurityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecurityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun onLoadPostsClick() {
        if (mPresenter == null) {
            return
        }
        mPresenter!!.loadPosts()
    }

    fun setPresenter(presenter: MainContract.Presenter) {
        mPresenter = presenter
    }

    fun setLoadingPosts(isLoading: Boolean) {
        mProgressBar!!.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun setPosts(posts: Array<Post?>?) {
        mPostAdapter!!.setPosts(posts!!)
        if (posts == null || posts.size < 1) {
            mPostList!!.visibility = View.GONE
        } else {
            mPostList!!.visibility = View.VISIBLE
        }
    }

    fun showError(@NonNull title: String?, error: String?) {
        Log.e(
            TAG,
            error ?: title
        )
        mErrorSnackbar = Snackbar.make(mLayout!!, title!!, Snackbar.LENGTH_INDEFINITE)
        mErrorSnackbar!!.show()
    }

    fun hideError() {
        if (mErrorSnackbar != null) {
            mErrorSnackbar!!.dismiss()
        }
    }

    fun showNoPostsMessage(showMessage: Boolean) {
        mEmptyView!!.visibility = if (showMessage) View.VISIBLE else View.GONE
    }
}
