package android.example.chatify.Story

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.example.chatify.Chat.ChatMainActivity
import android.example.chatify.Model.Story
import android.example.chatify.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import io.github.hyuwah.draggableviewlib.DraggableListener
import io.github.hyuwah.draggableviewlib.DraggableView
import io.github.hyuwah.draggableviewlib.setupDraggable
import kotlin.properties.Delegates


class AddStoryActivity : AppCompatActivity() {
    lateinit var tvStoryText:TextView
    var flag by Delegates.notNull<Boolean>()
    lateinit var ivPlus:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        var x=0.0f
        var y=0.0f
        val imageUri=intent.getStringExtra("uri")?.toUri()
        val ivNewStory=findViewById<ImageView>(R.id.ivNewStory)
        ivPlus=findViewById(R.id.ivPlus)
        val ivPost=findViewById<ImageView>(R.id.ivPost)
        tvStoryText=findViewById(R.id.tvStoryText)
        Picasso.get().load(imageUri).into(ivNewStory)
        var storageRef= FirebaseStorage.getInstance().reference
        var dbRef=FirebaseDatabase.getInstance().getReference("Story")
        flag=true

//        tvStoryText.setOnClickListener{
//            tvStoryText.text
//        }
        ivPlus.setOnClickListener {
            if(flag){
                alertDialogDemo()
//            tvStoryText.visibility= View.VISIBLE
                val someDraggableView: DraggableView<TextView> = tvStoryText.setupDraggable()
                    .setStickyMode(DraggableView.Mode.NON_STICKY)
                    .setAnimated(true)
                    .build() // can be other View or ViewGroup
                val someDraggableListener = object: DraggableListener {
                    override fun onPositionChanged(view: View) {
                        // Here you can access x & y of the view while moving
                        Log.d("TAG", "X: ${view.x.toString()}, Y: ${view.y.toString()}")
                    }
                }
                someDraggableView.listener = someDraggableListener
                flag=false
                ivPlus.setImageResource(R.drawable.minus)
            }
            else {
                tvStoryText.visibility= View.GONE
                ivPlus.setImageResource(R.drawable.plus)
                flag=true
            }
        }

        //get current user
        val sharedPrefFile = "kotlinsharedpreference"
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val myName = sharedPreferences.getString("name","noName")
        val myEmail = sharedPreferences.getString("email","noEmail")
        val myPic = sharedPreferences.getString("pic","noPic")
        val id=myEmail?.split(".")
        ivPost.setOnClickListener {
            x=tvStoryText.x
            y=tvStoryText.y
            if(imageUri!=null){
                val time=System.currentTimeMillis().toString()
                val photoRef=storageRef.child("images/$time-photo.jpg")
                photoRef.putFile(imageUri).continueWithTask { photoUploadTask->
                    photoRef.downloadUrl
                }.continueWith { downloadUrlTask->
                    Log.d("urlTask",downloadUrlTask.result.toString())
                    val story= Story(
                        downloadUrlTask.result.toString(),
                        time,
                        "no",
                        tvStoryText.text.toString(),
                        x.toString(),
                        y.toString(),
                        myEmail
                    )
                    dbRef.child(id?.get(0) ?: "").child(time).setValue(story)
                }.addOnCompleteListener { postCreationTask->
                    if(!postCreationTask.isSuccessful) {
                        Toast.makeText(this@AddStoryActivity,"${postCreationTask.exception}",Toast.LENGTH_SHORT).show()
                    }
                    val i= Intent(this@AddStoryActivity,ChatMainActivity::class.java)
                    i.putExtra("email",myEmail)
                    i.putExtra("name",myName)
                    i.putExtra("pic",myPic)
                    i.putExtra("status","online")
                    startActivity(i)
                }
            }
            else {
                Toast.makeText(this@AddStoryActivity,"Image cannot be empty",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun alertDialogDemo() {
        // get alert_dialog.xml view
        val li = LayoutInflater.from(this@AddStoryActivity)
        val promptsView: View = li.inflate(R.layout.dialog_box, null)
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
            this@AddStoryActivity
        )

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)
        val userInput = promptsView.findViewById<View>(R.id.etUserInput) as EditText

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, id -> // get user input and set it to result
                    // edit text
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Entered: " + userInput.text.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    tvStoryText.setText(userInput.text.toString())
                    tvStoryText.visibility= View.VISIBLE
                })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                    ivPlus.setImageResource(R.drawable.plus)
                flag=true})

        // create alert dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
    }
}
