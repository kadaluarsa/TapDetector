package co.id.kadaluarsa.tapdetector.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val titles = arrayOf("Touch","History")
        viewPager.adapter = FragmentAdapter(supportFragmentManager,lifecycle)
        TabLayoutMediator(
            tabs, viewPager
        ) { tab, position -> tab.text = titles[position] }.attach()

    }
}