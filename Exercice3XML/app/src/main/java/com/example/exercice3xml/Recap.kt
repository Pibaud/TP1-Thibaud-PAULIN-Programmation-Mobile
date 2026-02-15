package com.example.exercice3xml

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

class Recap : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("USER_NAME")
        val firstname = intent.getStringExtra("USER_FIRSTNAME")
        val age = intent.getStringExtra("USER_AGE")
        val areaOfExpertise = intent.getStringExtra("USER_EXPERTISE")
        val phoneNumber = intent.getStringExtra("USER_PHONE")

        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10))
        rootLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val tvWelcome = createTitleTextView(getString(R.string.recap_welcome_message))
        rootLayout.addView(tvWelcome)

        val innerLayout = LinearLayout(this)
        innerLayout.orientation = LinearLayout.VERTICAL
        innerLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        innerLayout.addView(createLabelTextView(getString(R.string.name)))
        val tvName = createValueTextView(name)
        innerLayout.addView(tvName)

        innerLayout.addView(createLabelTextView(getString(R.string.firstname)))
        val tvFirstname = createValueTextView(firstname)
        innerLayout.addView(tvFirstname)

        innerLayout.addView(createLabelTextView(getString(R.string.age)))
        val tvAge = createValueTextView(age)
        innerLayout.addView(tvAge)

        innerLayout.addView(createLabelTextView(getString(R.string.area_of_expertise)))
        val tvExpertise = createValueTextView(areaOfExpertise)
        innerLayout.addView(tvExpertise)

        innerLayout.addView(createLabelTextView(getString(R.string.phone_number)))
        val tvPhone = createValueTextView(phoneNumber)
        innerLayout.addView(tvPhone)

        val space = View(this)
        space.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(35)
        )
        innerLayout.addView(space)

        val buttonsLayout = LinearLayout(this)
        buttonsLayout.orientation = LinearLayout.HORIZONTAL
        buttonsLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val buttonParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        buttonParams.marginStart = dpToPx(30)
        buttonParams.marginEnd = dpToPx(30)

        val btnBack = Button(this)
        btnBack.text = getString(R.string.back_recap)
        btnBack.layoutParams = buttonParams
        btnBack.setOnClickListener {
            finish()
        }
        buttonsLayout.addView(btnBack)

        val btnOk = Button(this)
        btnOk.text = getString(R.string.validate_recap)
        btnOk.layoutParams = buttonParams
        btnOk.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("USER_NAME", name)
            intent.putExtra("USER_FIRSTNAME", firstname)
            intent.putExtra("USER_AGE", age)
            intent.putExtra("USER_EXPERTISE", areaOfExpertise)
            intent.putExtra("USER_PHONE", phoneNumber)
            startActivity(intent)
        }
        buttonsLayout.addView(btnOk)

        innerLayout.addView(buttonsLayout)
        rootLayout.addView(innerLayout)

        setContentView(rootLayout)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun createTitleTextView(text: String): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = 24f
        try {
            val typeface = ResourcesCompat.getFont(this, R.font.space_grotesk_bold)
            tv.typeface = typeface
        } catch (e: Exception) {
            tv.setTypeface(null, Typeface.BOLD)
        }
        tv.setTextColor(Color.BLACK)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = dpToPx(10)
        tv.layoutParams = params
        return tv
    }

    private fun createLabelTextView(text: String): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = 16f
        tv.setTextColor(Color.BLACK) // Style Text standard
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = dpToPx(5)
        tv.layoutParams = params
        return tv
    }

    private fun createValueTextView(text: String?): TextView {
        val tv = TextView(this)
        tv.text = text ?: ""
        tv.textSize = 18f
        tv.setTextColor(Color.BLACK)

        tv.setBackgroundResource(R.drawable.bg_edittext_rounded)

        val padding = dpToPx(12)
        tv.setPadding(padding, padding, padding, padding)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = dpToPx(10)
        tv.layoutParams = params
        return tv
    }
}

/*
class Recap: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_recap)

        val name = intent.getStringExtra("USER_NAME")
        val firstname = intent.getStringExtra("USER_FIRSTNAME")
        val age = intent.getStringExtra("USER_AGE")
        val areaOfExpertise = intent.getStringExtra("USER_EXPERTISE")
        val phoneNumber = intent.getStringExtra("USER_PHONE")

        val nameTextView = findViewById<TextView>(R.id.recap_name)
        val firstnameTextView = findViewById<TextView>(R.id.recap_firstname)
        val ageTextView = findViewById<TextView>(R.id.recap_age)
        val expertiseTextView = findViewById<TextView>(R.id.recap_area_of_expertise)
        val phoneTextView = findViewById<TextView>(R.id.recap_phone_number)

        nameTextView.text = name
        firstnameTextView.text = firstname
        ageTextView.text = age
        expertiseTextView.text = areaOfExpertise
        phoneTextView.text = phoneNumber

        val okButton = findViewById<Button>(R.id.ok_btn)
        okButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)

            intent.putExtra("USER_NAME", name)
            intent.putExtra("USER_FIRSTNAME", firstname)
            intent.putExtra("USER_AGE", age)
            intent.putExtra("USER_EXPERTISE", areaOfExpertise)
            intent.putExtra("USER_PHONE", phoneNumber)

            startActivity(intent)
        }

        val backButton = findViewById<Button>(R.id.recap_back_btn)
        backButton.setOnClickListener {
            finish()
        }
    }
}
*/