package android.example.chatify.Adapters

import android.content.Context
import android.content.SharedPreferences
import android.example.chatify.Model.Message
import android.example.chatify.R
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MessageAdapter(private val mList: List<Message>,private val myId:String,private val friendPic:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType==1){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_row_send, parent, false)
            SendViewHolder(view)
        } else {
            val view=LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_row_layout, parent, false)
            ReceiveViewHolder(view)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("messagess",mList.toString());
       if(getItemViewType(position)==1){
           (holder as SendViewHolder).message.text=mList[position].message
           val sharedPrefFile = "kotlinsharedpreference"
           val sharedPreferences: SharedPreferences = (holder.cvPic.context).getSharedPreferences(sharedPrefFile,
               Context.MODE_PRIVATE)
           val myPic=sharedPreferences.getString("pic","noPic")
           Picasso.get().load(myPic).placeholder(R.drawable.elon).into((holder as SendViewHolder).cvPic)

       }
        else{
           (holder as ReceiveViewHolder).message.text=mList[position].message
           Picasso.get().load(friendPic).placeholder(R.drawable.elon).into((holder as ReceiveViewHolder).cvPic)
       }
    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        val checkId=mList[position].fromId?.split(".")
        if(checkId?.get(0) ?:"" ==myId)
            return 1  //1 -> send
        else return 2  //2-> receive
    }

    // Holds the views for adding it to image and text
    class SendViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val cvPic:CircleImageView=itemView.findViewById(R.id.cvPic)
        val message: TextView = itemView.findViewById(R.id.message)
    }
    class ReceiveViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val message: TextView = itemView.findViewById(R.id.message)
        val cvPic:CircleImageView=itemView.findViewById(R.id.cvPic)
    }
}