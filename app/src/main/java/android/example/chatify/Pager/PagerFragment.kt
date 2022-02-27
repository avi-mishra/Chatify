package android.example.chatify.Pager

import android.example.chatify.Model.Story
import android.example.chatify.R
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

private const val PAGE_INDEX = "PAGE_INDEX"

class PageFragment : Fragment(), RequestListener<Drawable> {

    lateinit var progressBar:ProgressBar
    lateinit var image:AppCompatImageView
    lateinit var story_photo:CircleImageView
    lateinit var story_username:TextView
    lateinit var story_time:TextView
    lateinit var story:Story

    companion object {
        @JvmStatic
        fun newInstance(pageIndex: Int,stories: Story) =
            PageFragment().apply {
                arguments = Bundle().apply {
                    story=stories
                    Log.d("storriess",""+story)
                    putInt(PAGE_INDEX, pageIndex)
                }
            }
    }

    private var pageIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pageIndex = it.getInt(PAGE_INDEX)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_page, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressBar=view.findViewById(R.id.progressBar)
        story_photo=view.findViewById(R.id.story_photo)
        story_username=view.findViewById(R.id.story_username)
        story_time=view.findViewById(R.id.story_time)
        image=view.findViewById(R.id.image)
        progressBar.visibility = View.VISIBLE
        val aleatoryIndex = (1..50).random()

        Glide.with(this)
            .load(story.image)
            .listener(this)
            .into(image)
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        return true
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        progressBar.visibility = View.GONE
        return false
    }
}