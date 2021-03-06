package code.challenge.moviesInfoApp.infrastructure.defaultComponents.views.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.databinding.ViewDataBinding
import code.challenge.moviesInfoApp.infrastructure.defaultComponents.views.interfaces.DefaultView

abstract class DefaultActivity<T : ViewDataBinding>: AppCompatActivity(), DefaultView {

    val activityBinding: T by lazy { setContentView<T>(this, activityLayout())  }

    abstract fun activityLayout(): Int

    override fun viewContext(): Context = this

}