package android.example.chatify.Story

import android.example.chatify.Adapters.ClassAdapter
import android.example.chatify.Adapters.ViewPagerAdapter
import android.example.chatify.Model.Story
import android.example.chatify.R
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import pt.tornelas.segmentedprogressbar.SegmentedProgressBar
import pt.tornelas.segmentedprogressbar.SegmentedProgressBarListener

class ViewStoryActivity : AppCompatActivity() {

    lateinit var storyId:String
    lateinit var databaseReference:DatabaseReference
    lateinit var mStories :ArrayList<Story>
    var pressTime:Long=0L
    var limit :Long=500L
    var counter:Int = 0
    lateinit var adapter: ClassAdapter
    lateinit var item:ArrayList<Int>
    lateinit var storiesProgressView:SegmentedProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_story)

        storyId=intent.getStringExtra("storyId").toString()
        databaseReference=FirebaseDatabase.getInstance().getReference("Story")
        mStories=ArrayList()
        getStory()

        storiesProgressView=findViewById(R.id.storiesProgressView)

    }
    private fun getStory() {
        databaseReference.child(storyId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mStories.clear()
                for (storySnapshot in dataSnapshot.children) {
                    val story = storySnapshot.getValue<Story>()
                    Log.d("messagess", story.toString())
                    if (story != null) {
                        mStories.add(story)
                    }
                }
                Log.d("storieess",mStories.toString())
                item=ArrayList(mStories.size)
                for(i in 1..mStories.size){
                    item.add(i)
                }
                var viewPager=findViewById<ViewPager>(R.id.view_pager)
                adapter=ClassAdapter(supportFragmentManager,mStories,item)
                viewPager.adapter=adapter
                storiesProgressView.viewPager=viewPager
                storiesProgressView.segmentCount=mStories.size
                storiesProgressView.listener=object : SegmentedProgressBarListener {
                    override fun onPage(oldPageIndex: Int, newPageIndex: Int) {
                        // New page started animating
                    }

                    override fun onFinished() {
                        finish()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }
}