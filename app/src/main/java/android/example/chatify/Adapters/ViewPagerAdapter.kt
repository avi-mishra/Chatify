package android.example.chatify.Adapters

import android.content.Context
import android.example.chatify.Model.Story
import android.example.chatify.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pt.tornelas.segmentedprogressbar.SegmentedProgressBar


class ViewPagerAdapter     // Constructor of our ViewPager2Adapter class
    (private val ctx: Context,private val mStories: List<Story>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    // Array of images
    // Adding images from drawable folder
    // This method returns our layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.story_element, parent, false)
        return ViewHolder(view)
    }

    // This method binds the screen with the view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // This will set the images in imageview
        Picasso.get().load(mStories[position].image).into(holder.image)
    }

    // This Method returns the size of the Array
    override fun getItemCount(): Int {
        return mStories.size
    }

    // The ViewHolder class holds the view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image=itemView.findViewById<ImageView>(R.id.image)
        var story_photo=itemView.findViewById<ImageView>(R.id.story_photo)
        var story_username=itemView.findViewById<TextView>(R.id.story_username)
        var storyTime=itemView.findViewById<TextView>(R.id.storyTime)
        var skip=itemView.findViewById<View>(R.id.skip)
        var reverse=itemView.findViewById<View>(R.id.reverse)
        var tvStoryText=itemView.findViewById<TextView>(R.id.tvStoryText)


    }
}