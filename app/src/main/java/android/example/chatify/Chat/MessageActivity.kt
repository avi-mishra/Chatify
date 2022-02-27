package android.example.chatify.Chat

import android.content.Context
import android.content.SharedPreferences
import android.example.chatify.Adapters.MessageAdapter
import android.example.chatify.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val etNewMessage=findViewById<EditText>(R.id.etNewMessage)
        val ivSendMessage=findViewById<ImageView>(R.id.ivSendMessage)
        val recycler_view=findViewById<RecyclerView>(R.id.recycler_view)

        val messages=ArrayList<android.example.chatify.Model.Message>()

        val friendName=intent.getStringExtra("name")
        val friendEmail=intent.getStringExtra("email")
        val friendPic=intent.getStringExtra("pic")

        val sharedPrefFile = "kotlinsharedpreference"
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val myName = sharedPreferences.getString("name","noName")
        val myEmail = sharedPreferences.getString("email","noEmail")
        val myPic=sharedPreferences.getString("pic","noPic")

        val myId = myEmail?.split(".")
        val friendId=friendEmail?.split(".")
        val firebaseRef=FirebaseDatabase.getInstance().getReference("Messages/").child(myId?.get(0) ?: "")
            .child(friendId?.get(0) ?: "")
        Log.d("messagess", "$myId $friendId")

        recycler_view.layoutManager= LinearLayoutManager(this)
        val adapter= friendPic?.let { MessageAdapter(messages,myId = myId?.get(0) ?: "", it) }
        recycler_view.adapter=adapter

        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messages.clear()
                for (messageSnapshot in dataSnapshot.children) {
                    val message = messageSnapshot.getValue<android.example.chatify.Model.Message>()
                    Log.d("messagess", messages.toString())
                    if (message != null) {
                        messages.add(message)
                        adapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })

        //send to databse
        val firebaseRef2=FirebaseDatabase.getInstance().getReference("Messages/").child(friendId?.get(0) ?: "")
            .child(myId?.get(0) ?: "")
        ivSendMessage.setOnClickListener {
            val text=etNewMessage.text.toString()
            val time=""+System.currentTimeMillis()
            firebaseRef.child(time).setValue(
                android.example.chatify.Model.Message(myEmail,text,"no",time,friendEmail)
            )
            firebaseRef2.child(time).setValue(
                android.example.chatify.Model.Message(myEmail,text,"no",time,friendEmail)
            ).addOnSuccessListener {
                recycler_view.scrollToPosition(adapter!!.itemCount-1)
                etNewMessage.text.clear()
            }
        }
    }
}