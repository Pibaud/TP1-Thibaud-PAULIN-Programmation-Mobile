package com.example.exercice3xml

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent

class Profile : Activity() {

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

        val headerLayout = LinearLayout(this)
        headerLayout.orientation = LinearLayout.HORIZONTAL
        headerLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val tvWelcome = createTitleTextView(getString(R.string.profil_welcome_message))
        headerLayout.addView(tvWelcome)

        val tvFirstname = createTitleTextView(firstname ?: "")
        headerLayout.addView(tvFirstname)

        val spaceHeader = View(this)
        spaceHeader.layoutParams = LinearLayout.LayoutParams(dpToPx(10), ViewGroup.LayoutParams.MATCH_PARENT)
        headerLayout.addView(spaceHeader)

        val tvName = createTitleTextView(name ?: "")
        headerLayout.addView(tvName)

        rootLayout.addView(headerLayout)


        val flexbox = FlexboxLayout(this)
        val flexParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        flexbox.layoutParams = flexParams
        flexbox.flexDirection = FlexDirection.ROW
        flexbox.flexWrap = FlexWrap.WRAP
        flexbox.justifyContent = JustifyContent.FLEX_START

        val tvPresentation = createNormalTextView(getString(R.string.profile_presentation))
        flexbox.addView(tvPresentation)

        val tvAge = createNormalTextView(age ?: "")
        flexbox.addView(tvAge)

        val tvAgeExpertise = createNormalTextView(getString(R.string.profile_age_expertise))
        flexbox.addView(tvAgeExpertise)

        val tvExpertise = createNormalTextView(areaOfExpertise ?: "")
        flexbox.addView(tvExpertise)

        rootLayout.addView(flexbox)


        val tvContactTitle = createTitleTextView(getString(R.string.profile_contact))
        val contactTitleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        contactTitleParams.gravity = Gravity.CENTER_HORIZONTAL
        contactTitleParams.topMargin = dpToPx(30)
        tvContactTitle.layoutParams = contactTitleParams
        rootLayout.addView(tvContactTitle)

        val tvPhone = createNormalTextView(phoneNumber ?: "")
        val phoneParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        phoneParams.gravity = Gravity.CENTER_HORIZONTAL
        tvPhone.layoutParams = phoneParams
        rootLayout.addView(tvPhone)

        val btnCall = ImageButton(this)
        btnCall.setImageResource(R.drawable.baseline_local_phone_24)
        btnCall.setBackgroundResource(R.drawable.bg_call_button)
        val btnCallParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        btnCallParams.gravity = Gravity.CENTER_HORIZONTAL
        btnCall.layoutParams = btnCallParams

        btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }
        rootLayout.addView(btnCall)


        val spaceFooter = View(this)
        spaceFooter.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(150)
        )
        rootLayout.addView(spaceFooter)

        val tvInvalidInfos = createNormalTextView(getString(R.string.profile_invalid_infos))
        rootLayout.addView(tvInvalidInfos)


        val btnEdit = Button(this)
        btnEdit.text = getString(R.string.profile_edit_infos_btn)
        val btnEditParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        btnEdit.layoutParams = btnEditParams

        btnEdit.setOnClickListener {
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
        }
        rootLayout.addView(btnEdit)


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
        tv.textSize = 22f // Taille approximative d'un titre
        // Application de la police Space Grotesk Bold
        try {
            val typeface = ResourcesCompat.getFont(this, R.font.space_grotesk_bold)
            tv.typeface = typeface
        } catch (e: Exception) {
            tv.setTypeface(null, Typeface.BOLD) // Fallback
        }
        tv.setTextColor(Color.BLACK)
        return tv
    }

    private fun createNormalTextView(text: String): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = 16f
        tv.setTextColor(Color.BLACK)
        return tv
    }
}


/*
class Profile: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_profile)

        val name = intent.getStringExtra("USER_NAME")
        val firstname = intent.getStringExtra("USER_FIRSTNAME")
        val age = intent.getStringExtra("USER_AGE")
        val areaOfExpertise = intent.getStringExtra("USER_EXPERTISE")
        val phoneNumber = intent.getStringExtra("USER_PHONE")

        val nameTextView = findViewById<TextView>(R.id.profile_name)
        val firstnameTextView = findViewById<TextView>(R.id.profile_firstname)
        val ageTextView = findViewById<TextView>(R.id.profile_age)
        val expertiseTextView = findViewById<TextView>(R.id.profile_expertise)
        val phoneTextView = findViewById<TextView>(R.id.profile_phone_number)

        nameTextView.text = name
        firstnameTextView.text = firstname
        ageTextView.text = age
        expertiseTextView.text = areaOfExpertise
        phoneTextView.text = phoneNumber

        val callButton = findViewById<ImageButton>(R.id.call_button)

        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }


        val editInfosButton = findViewById<Button>(R.id.profile_edit_infos_btn)
        editInfosButton.setOnClickListener {
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
        }
    }
}
*/