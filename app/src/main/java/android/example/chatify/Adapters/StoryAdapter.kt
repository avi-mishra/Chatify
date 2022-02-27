package android.example.chatify.Adapters

import android.content.Intent
import android.example.chatify.Chat.MessageActivity
import android.example.chatify.Model.Friend
import android.example.chatify.Model.User
import android.example.chatify.R
import android.example.chatify.Story.ViewStoryActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.min

class StoryAdapter(private val mList: List<User>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.story_layout, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("fkjn",mList.toString())
        Picasso.get().load(mList[position].pic).placeholder(R.drawable.elon).into(holder.imageView)
        holder.name.setText(mList[position].name)
        holder.imageView.setOnClickListener {
            val intent=Intent(holder.imageView.context,ViewStoryActivity::class.java)
            val id=mList[position].email?.split(".")
            intent.putExtra("storyId", id?.get(0))
            holder.imageView.context.startActivity(intent)
        }
        }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: CircleImageView = itemView.findViewById(R.id.ivStoryPic)
        val name: TextView =itemView.findViewById(R.id.tvStoryName)
    }
}