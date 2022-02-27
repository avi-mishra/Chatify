package android.example.chatify.Adapters

import android.example.chatify.Model.Story
import android.example.chatify.Pager.PageFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ClassAdapter(fragmentManager: FragmentManager, private val items: List<Story>,private val index:List<Int>): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = PageFragment.newInstance(index[position],items[position])

    override fun getCount() = index.size
}