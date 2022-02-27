package android.example.chatify.Adapters

import android.content.Intent
import android.example.chatify.Chat.MessageActivity
import android.example.chatify.Model.Friend
import android.example.chatify.R
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Collections.min
import kotlin.math.min

class FriendAdapter(private val mList: List<String>,private val mFriends:List<Friend>) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.messages_layout, parent, false)


        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
//        holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.message.text = mList[position]
        holder.name.text= mFriends[position].name.toString()
        Picasso.get().load(mFriends[position].pic.toString()).placeholder(R.drawable.elon).into(holder.imageView)
        if(mFriends[position].status.toString()=="online"){
            holder.status.visibility=View.VISIBLE
        }
        holder.clFriend.setOnClickListener {
            val intent= Intent(holder.clFriend.context,MessageActivity::class.java)
            intent.putExtra("name",mFriends[position].name)
            intent.putExtra("pic",mFriends[position].pic)
            intent.putExtra("email",mFriends[position].email)
            holder.clFriend.context.startActivity(intent)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return min(mList.size,mFriends.size)
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: CircleImageView = itemView.findViewById(R.id.ivProfilePic)
        val message: TextView = itemView.findViewById(R.id.tvLastMessage)
        val name:TextView=itemView.findViewById(R.id.tvName)
        val status:CircleImageView=itemView.findViewById(R.id.cvFriendStatus)
        val clFriend:ConstraintLayout=itemView.findViewById(R.id.clFriend)
    }
}