package com.example.citizen_science

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ShowResultsActivity : AppCompatActivity() {

    private lateinit var mRespondantInfo: TextView
    private lateinit var mResultReadout: TextView

    private lateinit var mFinishButton: Button
    private lateinit var mCopyButton: Button

    private lateinit var mResultString: String


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_results)

        mResultString = this.intent.getStringExtra("RESULTS")!!
        Log.i("CHECKER","Passed to ShowResultsActivity. Prinout is: $mResultString")
        mRespondantInfo = findViewById(R.id.infoLabel)
        mRespondantInfo.text = this.intent.getStringExtra("RESPONDANT")!!
        mFinishButton = findViewById(R.id.finishButton)
        mCopyButton = findViewById(R.id.copyButton)

        if(!mRespondantInfo.text.equals("Compiled Summary"))
        {
            mRespondantInfo.text = "From User: " + mRespondantInfo.text
        }

        mResultReadout = findViewById(R.id.resultPrintout)
        mResultReadout.movementMethod = ScrollingMovementMethod()
        mResultReadout.text = mResultString

        mCopyButton.setOnClickListener{
            copyToClipBoard()
        }

        mFinishButton.setOnClickListener{
            finish()
        }
    }


    // Copies the compiled result to the clipboard
    fun copyToClipBoard() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", mResultString)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this, "Copied the Results to clipboard", Toast.LENGTH_LONG).show()
    }
}