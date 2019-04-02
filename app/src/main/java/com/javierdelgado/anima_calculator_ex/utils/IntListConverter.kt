package com.javierdelgado.anima_calculator_ex.utils

import com.raizlabs.android.dbflow.converter.TypeConverter


class IntListConverter : TypeConverter<String, MutableList<Int>>() {
    private val separator = ","

    override fun getDBValue(model: MutableList<Int>?): String {
        return if (model==null || model.isEmpty())
            ""
        else
            model.joinToString (separator = separator) { it.toString() }
    }

    override fun getModelValue(data: String?): MutableList<Int> {
        return data?.split(separator)?.map { it.toInt() }?.toMutableList() ?: mutableListOf()
    }
}