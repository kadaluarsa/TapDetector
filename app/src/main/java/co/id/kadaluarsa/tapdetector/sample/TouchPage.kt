package co.id.kadaluarsa.tapdetector.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.id.kadaluarsa.tapdetector.sample.R

class TouchPage private constructor() : Fragment() {

    companion object {
        fun getInstance(bundle: Bundle): TouchPage {
            val touchArea = TouchPage()
            touchArea.arguments = bundle
            return touchArea
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.touch_area, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener(SampleApp.getAppContext().getTap().touchListener())

    }
}