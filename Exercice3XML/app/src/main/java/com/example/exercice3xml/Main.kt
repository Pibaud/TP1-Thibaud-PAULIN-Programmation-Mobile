package com.example.exercice3xml

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


class Main : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_main)

        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10))
        val rootParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        rootLayout.layoutParams = rootParams

        val wrapContentParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val commonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        commonParams.setMargins(0, dpToPx(5), 0, dpToPx(5))

        val tvWelcome = TextView(this)
        tvWelcome.text = getString(R.string.main_welcome_message)
        tvWelcome.textSize = 24f
        val typeface = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.space_grotesk_bold)
        tvWelcome.typeface = typeface
        tvWelcome.layoutParams = wrapContentParams
        rootLayout.addView(tvWelcome)

        val tvName = createLabel(getString(R.string.name))
        rootLayout.addView(tvName)

        val etName = createStyledEditText(getString(R.string.name_ph), InputType.TYPE_CLASS_TEXT)
        rootLayout.addView(etName)

        val tvFirstname = createLabel(getString(R.string.firstname))
        rootLayout.addView(tvFirstname)

        val etFirstname = createStyledEditText(getString(R.string.firstname_ph), InputType.TYPE_CLASS_TEXT)
        rootLayout.addView(etFirstname)

        val tvAge = createLabel(getString(R.string.age))
        rootLayout.addView(tvAge)

        val etAge = createStyledEditText(getString(R.string.age_ph), InputType.TYPE_CLASS_NUMBER)
        rootLayout.addView(etAge)

        val tvExpertise = createLabel(getString(R.string.area_of_expertise))
        rootLayout.addView(tvExpertise)

        val spinnerExpertise = Spinner(this)
        spinnerExpertise.background = getDrawable(R.drawable.bg_edittext_rounded)
        spinnerExpertise.elevation = dpToPx(4).toFloat()

        val spinnerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        spinnerParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        rootLayout.addView(spinnerExpertise, spinnerParams)

        val domains = arrayOf("Ressources Humaines", "Design", "Marketing", "Physique", "Big Data / IA")
        val domainAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, domains)
        domainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExpertise.adapter = domainAdapter

        val tvPhone = createLabel(getString(R.string.phone_number))
        rootLayout.addView(tvPhone)

        val phoneContainer = LinearLayout(this)
        phoneContainer.orientation = LinearLayout.HORIZONTAL
        phoneContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val spinnerCountry = Spinner(this)
        spinnerCountry.background = getDrawable(R.drawable.bg_edittext_rounded)
        spinnerCountry.elevation = dpToPx(4).toFloat()
        val countryParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        countryParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        phoneContainer.addView(spinnerCountry, countryParams)

        val countries = listOf(
            CountryItem("France", "+33", R.drawable.france_flag),
            CountryItem("Belgique", "+32", R.drawable.belgium_flag),
            CountryItem("Suisse", "+41", R.drawable.switzerland_flag)
        )

        val countryAdapter = object : ArrayAdapter<CountryItem>(this, 0, countries) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createCountryView(position)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createCountryView(position)
            }

            private fun createCountryView(position: Int): View {
                val item = getItem(position)

                val itemLayout = LinearLayout(context)
                itemLayout.orientation = LinearLayout.HORIZONTAL
                itemLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))

                val ivFlag = ImageView(context)
                ivFlag.layoutParams = LinearLayout.LayoutParams(dpToPx(24), dpToPx(24)).apply {
                    marginEnd = dpToPx(8)
                }
                if (item != null) ivFlag.setImageResource(item.flag)

                val tvCode = TextView(context)
                tvCode.text = item?.code
                tvCode.setTypeface(null, Typeface.BOLD)
                tvCode.setTextColor(Color.BLACK)

                itemLayout.addView(ivFlag)
                itemLayout.addView(tvCode)
                return itemLayout
            }
        }
        spinnerCountry.adapter = countryAdapter

        val etPhone = createStyledEditText("", InputType.TYPE_CLASS_PHONE)
        val phoneEditParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        phoneContainer.addView(etPhone, phoneEditParams)

        rootLayout.addView(phoneContainer)

        val btnSubmit = Button(this)
        btnSubmit.text = getString(R.string.validate)
        btnSubmit.layoutParams = wrapContentParams
        rootLayout.addView(btnSubmit)

        val tvError = TextView(this)
        tvError.text = getString(R.string.missing_fields)
        tvError.setTextColor(Color.RED)
        tvError.visibility = View.GONE
        tvError.gravity = Gravity.CENTER
        val errorParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        errorParams.topMargin = dpToPx(8)
        rootLayout.addView(tvError, errorParams)

        setContentView(rootLayout)

        btnSubmit.setOnClickListener {
            val fields = listOf(etName, etFirstname, etAge, etPhone)
            var allFilled = true

            for (field in fields) {
                if (field.text.toString().trim().isEmpty()) {
                    field.setBackgroundResource(R.drawable.bg_edittext_error)
                    allFilled = false
                } else {
                    field.setBackgroundResource(R.drawable.bg_edittext_success)
                }
            }

            if (!allFilled) {
                tvError.visibility = View.VISIBLE
            } else {
                tvError.visibility = View.GONE

                val areaOfExpertise = spinnerExpertise.selectedItem.toString()
                val selectedCountry = spinnerCountry.selectedItem as CountryItem
                val fullPhoneNumber = selectedCountry.code + etPhone.text.toString()

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.confirm_dialog_title))
                builder.setPositiveButton(getString(R.string.validate_dialog)) { _, _ ->
                    val intent = Intent(this, Recap::class.java)
                    intent.putExtra("USER_NAME", etName.text.toString())
                    intent.putExtra("USER_FIRSTNAME", etFirstname.text.toString())
                    intent.putExtra("USER_AGE", etAge.text.toString())
                    intent.putExtra("USER_EXPERTISE", areaOfExpertise)
                    intent.putExtra("USER_PHONE", fullPhoneNumber)
                    startActivity(intent)
                }
                builder.setNegativeButton(getString(R.string.dismiss_dialog)) { d, _ -> d.dismiss() }
                builder.create().show()
            }
        }
    }
    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_exercice3_xml)

        val domains = arrayOf("Ressources Humaines", "Design", "Marketing", "Physique", "Big Data / IA")
        val domainAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, domains)
        domainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val domainSpinner = findViewById<Spinner>(R.id.spinner_expertise)
        domainSpinner.adapter = domainAdapter

        val countries = listOf(
            CountryItem("France", "+33", R.drawable.france_flag),
            CountryItem("Belgique", "+32", R.drawable.belgium_flag),
            CountryItem("Suisse", "+41", R.drawable.switzerland_flag)
        )

        val adapter = object : ArrayAdapter<CountryItem>(this, R.layout.spinner_item, countries) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createViewFromResource(position, convertView, parent)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createViewFromResource(position, convertView, parent)
            }

            private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.spinner_item, parent, false)
                val item = getItem(position)

                view.findViewById<ImageView>(R.id.ivFlag).setImageResource(item!!.flag)
                view.findViewById<TextView>(R.id.tvCode).text = item.code

                return view
            }
        }

        val countryCodeSpinner = findViewById<Spinner>(R.id.country_phone_code)
        countryCodeSpinner.adapter = adapter

        val btnSubmit = findViewById<Button>(R.id.submit_btn)

        btnSubmit.setOnClickListener {
            val etName = findViewById<EditText>(R.id.edit_text_name)
            val etFirstname = findViewById<EditText>(R.id.edit_text_firstname)
            val etAge = findViewById<EditText>(R.id.edit_text_age)
            val etPhone = findViewById<EditText>(R.id.edit_text_phone_number)
            val tvError = findViewById<TextView>(R.id.tv_error_message)

            val fields = listOf(etName, etFirstname, etAge, etPhone)
            var allFilled = true

            for (field in fields) {
                if (field.text.toString().trim().isEmpty()) {
                    field.setBackgroundResource(R.drawable.bg_edittext_error)
                    allFilled = false
                } else {
                    field.setBackgroundResource(R.drawable.bg_edittext_success)
                }
            }

            if (!allFilled) {
                tvError.visibility = View.VISIBLE
            } else {
                tvError.visibility = View.GONE

                val areaOfExpertise = domainSpinner.selectedItem.toString()
                val selectedCountry = countryCodeSpinner.selectedItem as CountryItem
                val fullPhoneNumber = selectedCountry.code + etPhone.text.toString()

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.confirm_dialog_title))
                builder.setPositiveButton(getString(R.string.validate_dialog)) { _, _ ->
                    val intent = Intent(this, Recap::class.java)
                    intent.putExtra("USER_NAME", etName.text.toString())
                    intent.putExtra("USER_FIRSTNAME", etFirstname.text.toString())
                    intent.putExtra("USER_AGE", etAge.text.toString())
                    intent.putExtra("USER_EXPERTISE", areaOfExpertise)
                    intent.putExtra("USER_PHONE", fullPhoneNumber)
                    startActivity(intent)
                }
                builder.setNegativeButton(getString(R.string.dismiss_dialog)) { d, _ -> d.dismiss() }
                builder.create().show()
            }
        }
    }
    */

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun createLabel(text: String): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        return tv
    }

    private fun createStyledEditText(hint: String, inputType: Int): EditText {
        val et = EditText(this)
        et.hint = hint
        et.inputType = inputType
        et.setBackgroundResource(R.drawable.bg_edittext_rounded) // Utilise ton drawable existant

        val padding = dpToPx(12)
        et.setPadding(padding, padding, padding, padding)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, dpToPx(5), 0, dpToPx(15)) // Marge en bas
        et.layoutParams = params

        return et
    }
}