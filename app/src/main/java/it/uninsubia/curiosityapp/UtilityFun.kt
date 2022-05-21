package it.uninsubia.curiosityapp

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import androidx.core.content.ContextCompat

class UtilityFun()
{
    fun setErrorOnSearchView(editText: EditText, errorMessage : String,context: Context)
    {
        val errorColor = ContextCompat.getColor(context,R.color.white)
        //val errorBackgroundColor = ContextCompat.getColor(this,R.color.white)
        val fgcspan = ForegroundColorSpan(errorColor)
        //val bgcspan = BackgroundColorSpan(errorBackgroundColor)
        val builder = SpannableStringBuilder(errorMessage)
        builder.setSpan(fgcspan, 0, errorMessage.length, 4)
        editText.error = builder
    }
}