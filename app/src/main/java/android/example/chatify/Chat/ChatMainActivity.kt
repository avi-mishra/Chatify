package android.example.chatify.Chat

import android.content.Intent
import android.example.chatify.Adapters.FriendAdapter
import android.example.chatify.Adapters.StoryAdapter
import android.example.chatify.Model.Friend
import android.example.chatify.Model.Message
import android.example.chatify.Model.User
import android.example.chatify.R
import android.example.chatify.Story.AddStoryActivity
import android.example.chatify.Story.ViewStoryActivity
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList


class ChatMainActivity : AppCompatActivity() {
    lateinit var imageUri: Uri
    lateinit var rvStory:RecyclerView
    lateinit var storyRef:DatabaseReference
    lateinit var names:ArrayList<String>
    lateinit var users:ArrayList<User>
    lateinit var storyAdapter:StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        var firebaseDatabase: DatabaseReference? =null
        val email=intent.getStringExtra("email")
        val name=intent.getStringExtra("name")
        val pic=intent.getStringExtra("pic")
        val status=intent.getStringExtra("status")
        val ivProfilePic=findViewById<CircleImageView>(R.id.ivProfilePic)
        val tvName=findViewById<TextView>(R.id.tvName)
        val tvLastMessage=findViewById<TextView>(R.id.tvLastMessage)
        var messageListener:ValueEventListener?=null
        val friends=ArrayList<Friend>()
        val lastMessage=ArrayList<String>()
        rvStory=findViewById(R.id.rvStory)
        storyRef=FirebaseDatabase.getInstance().getReference("Story")
        names= ArrayList()
        users= ArrayList()
        storyAdapter= StoryAdapter(users)
        rvStory.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true)
        rvStory.adapter=storyAdapter


        //story
        val ivAddNewStory=findViewById<CircleImageView>(R.id.ivAddNewStory)

        val recyclerview = findViewById<RecyclerView>(R.id.rvMessages)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = FriendAdapter(lastMessage,friends)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        //add profile pic and name
        Picasso.get().load(pic).placeholder(R.drawable.ic_launcher_foreground).into(ivProfilePic)
        tvName.text=name
        getStory()

        //check if user exits in database
        val id = email?.split(".")
        firebaseDatabase=FirebaseDatabase.getInstance().getReference("Users/").child(id?.get(0) ?: "")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if(!dataSnapshot.exists()){
                    firebaseDatabase?.setValue(User(name,email,pic,status))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        firebaseDatabase.addValueEventListener(postListener)

        //get Latest message
        val mp = HashMap<String, String>()
        messageListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(snapshot in dataSnapshot.children){
                    val message=snapshot.getValue<Message>()
                    if(message!=null) {
                        Log.d("datass",message.toString())
                        message.time?.let { message.message?.let { it1 -> mp.put(it, it1) } }
                    }
                }

//                val maxEntry = Collections.max(mp?.entries, java.util.Map.Entry.comparingByKey())
                val maxEntry = mp.maxByOrNull { it.key }
                if (maxEntry != null) {
                    lastMessage.add(maxEntry.value)

                    adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        //get friends data
        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val u=dataSnapshot.getValue<User>()
                friends.add(Friend(u?.name,u?.status,u?.pic,u?.email))
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }

        //get friend
        val firebaseMessageRef=FirebaseDatabase.getInstance().getReference("Messages/").child(id?.get(0) ?: "")
        val friendListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(snapshot in dataSnapshot.children){
                    val friend:String?=snapshot.key
                    if(friend!=null){
                        val ref= firebaseMessageRef.child(friend)
                        ref.addValueEventListener(messageListener)
                        FirebaseDatabase.getInstance().getReference("Users/")
                            .child(friend).addValueEventListener(dataListener)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        firebaseMessageRef.addValueEventListener(friendListener)

        //add story
        ivAddNewStory.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 1000)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1000) {
            imageUri = data?.data!!
//            imageView.setImageURI(imageUri)
            val intent=Intent(this@ChatMainActivity,AddStoryActivity::class.java)
            intent.putExtra("uri",imageUri.toString())
            startActivity(intent)
            finish()
        }
    }
    fun getStory(){
        storyRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(datasnap in snapshot.children){
                    var name=datasnap.key
                    var dbRef= name?.let {
                        var r=FirebaseDatabase.getInstance().getReference("Users/")
                        .child(it).addValueEventListener(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var user=snapshot.getValue<User>()
                                if (user != null) {
                                    users.add(user)
                                    storyAdapter.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}