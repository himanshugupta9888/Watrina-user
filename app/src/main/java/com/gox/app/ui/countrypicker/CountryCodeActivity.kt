package com.gox.app.ui.countrypicker

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.databinding.ViewDataBinding
import com.gox.app.R
import com.gox.app.adapter.CountryAdapter
import com.gox.app.data.repositary.remote.model.CountryModel
import com.gox.app.databinding.ActivityCountrypickerListBinding
import com.gox.app.utils.Country
import com.gox.basemodule.base.BaseActivity

class CountryCodeActivity : BaseActivity<ActivityCountrypickerListBinding>(), SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {


    private lateinit var ivBack: ImageView
    private lateinit var llPlaces: ListView
    private lateinit var svCountry: SearchView
    private lateinit var listCountry: List<CountryModel>
    private var countryAdapter: CountryAdapter? = null
    private var countryName: String? = ""
    private var countryDialCode: String? = ""
    private var countryCode: String? = ""
    private var countryFlag: Int? = -1


    override fun getLayoutId(): Int {
        return R.layout.activity_countrypicker_list
    }


    override fun initView(mViewDataBinding: ViewDataBinding?) {
        ivBack = findViewById(R.id.iv_back)
        svCountry = findViewById(R.id.sv_country)
        llPlaces = findViewById(R.id.ll_country)


        listCountry = Country.getAllCountries()
        countryAdapter = CountryAdapter(this@CountryCodeActivity, listCountry)
        llPlaces.adapter = countryAdapter
        svCountry.setOnQueryTextListener(this)

        llPlaces.onItemClickListener = this
        ivBack.setOnClickListener {
            finish()
        }
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        countryAdapter!!.filter.filter(query.toString())
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        countryAdapter!!.filter.filter(newText.toString())
        return true
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val countryModel = parent!!.adapter.getItem(position) as CountryModel

        if (countryModel != null) {
            countryName = countryModel.name
            countryDialCode = countryModel.dialCode
            countryFlag = countryModel.flag
            countryCode = countryModel.code

            val resultIntent = Intent()
            resultIntent.putExtra("countryName", countryModel.name)
            resultIntent.putExtra("countryCode", countryModel.dialCode)
            resultIntent.putExtra("countryFlag", countryModel.flag)
            resultIntent.putExtra("countryIsoCode", countryModel.code)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        }
    }

}
