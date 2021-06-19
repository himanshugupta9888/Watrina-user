package com.gox.app.ui.language_setting

import com.gox.basemodule.base.BaseViewModel
import com.gox.app.data.LocalConstant
import com.gox.app.data.repositary.remote.model.Language


class LanguageViewModel : BaseViewModel<LanguageNavigator>(){

    private lateinit var languages:List<Language>
    private lateinit var adapter: LanguageAdapter
    private lateinit var currentLanguage:String

    fun setLanguage(currentLanguage:String){
        this.currentLanguage = currentLanguage
        languages = LocalConstant.languages
        adapter = LanguageAdapter(this)
    }

    fun getAdapter():LanguageAdapter = adapter

    fun getLanguages():List<Language> = languages


    fun getLanguage(position:Int):Language = languages[position]

    fun getCurrentLanguage():String = currentLanguage

    fun checkedChangeListener(checked:Boolean,position:Int){
        val selectedLanguage = languages[position].key
        if (checked && selectedLanguage != currentLanguage) {
            currentLanguage = selectedLanguage
            adapter.setNewLocale(selectedLanguage)
        }
    }

    fun onSaveClicked(){
        navigator.onLanguageChanged()
    }

}