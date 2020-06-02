package com.vsm.mymapstest.ui.adapters

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley
import com.vsm.mymapstest.R
import com.vsm.mymapstest.model.Post

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val TAG = "PostAdapter"
    private var mCallback: PostAdapterCallback? = null
    private var mPosts: Array<Post?> = arrayOfNulls<Post>(0)
    private var mImageLoader: ImageLoader? = null

    interface PostAdapterCallback {
        fun onPostImageLoadingError(
            error: String?,
            e: Exception?
        )
    }

    fun PostAdapter(
        context: Context?,
        callback: PostAdapterCallback?
    ) {
        mCallback = callback

        // Set up a new ImageLoader that does not cache any requests.
        mImageLoader =
            object : ImageLoader(Volley.newRequestQueue(context), object : ImageCache {
                override fun getBitmap(s: String): Bitmap? {
                    return null
                }

                override fun putBitmap(s: String, bitmap: Bitmap) {
                    // Do not cache this bitmap.
                }
            }) {
                override fun onGetImageError(
                    cacheKey: String,
                    error: VolleyError
                ) {
                    super.onGetImageError(cacheKey, error)
                    mCallback!!.onPostImageLoadingError(error.message, error)
                }
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layout: View =
            LayoutInflater.from(parent.context).inflate(R.layout.post_row, parent, false)
        val holder = PostViewHolder(layout)
        holder.image.setDefaultImageResId(R.drawable.ic_loading)
        holder.image.setErrorImageResId(R.drawable.ic_error)
        return holder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: Post? = mPosts[position]
        holder.name.setText(post?.name)
        holder.text.setText(post?.message)
        holder.image.setImageUrl(post?.profileImage, mImageLoader)
        Log.d(TAG, "loading image: " + post?.profileImage)
    }

    fun setPosts(posts: Array<Post?>) {
        mPosts = posts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    class PostViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var image: NetworkImageView
        var name: TextView
        var text: TextView

        init {
            image = itemView.findViewById<View>(R.id.post_image) as NetworkImageView
            name = itemView.findViewById<View>(R.id.post_name) as TextView
            text = itemView.findViewById<View>(R.id.post_text) as TextView
        }
    }
}