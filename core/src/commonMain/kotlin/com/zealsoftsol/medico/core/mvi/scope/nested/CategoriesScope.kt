package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.AutoComplete
class CategoriesScope : Scope.Child.TabBar() {

    var categoriesData: List<Category>
    val CELL_COUNT = 3

    init {
        categoriesData = listOf(
            Category.Category1,
            Category.Category2,
            Category.Category3,
            Category.Category4,
            Category.Category5,
            Category.Category6,
            Category.Category7,
            Category.Category8,
            Category.Category9,
            Category.Category10,
            Category.Category11,
            Category.Category12,
            Category.Category13,
            Category.Category14,
            Category.Category15,
            Category.Category16,
            Category.Category17,
            Category.Category18,
            Category.Category19,
            Category.Category20,
            Category.Category21,
            Category.Category22,
            Category.Category23,
            Category.Category24,
            Category.Category25
        )
    }

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader("Shop By Category")

    fun startBrandSearch(searchTerm: String, field: String) {
        val autoComplete = AutoComplete(
            query = field, details = "in Manufacturers",
            suggestion = searchTerm
        )
        EventCollector.sendEvent(Event.Transition.Search(autoComplete))
    }

    enum class Category(val title: String, val imgPath: String) {
        Category1("ayurvedic", ""),
        Category2("allopathic", ""),
        Category3("homeopathic", ""),
        Category4("otc", ""),
        Category5("Veterinary", ""),
        Category6("cough_respiratory", ""),
        Category7("diabetic_care", ""),
        Category8("eye_care", ""),
        Category9("pain_relief", ""),
        Category10("skin_care", ""),
        Category11("vitamins_and_aupplements", ""),
        Category12("metal_wellness", ""),
        Category13("dental_care", ""),
        Category14("liver_care", ""),
        Category15("pediatrics", ""),
        Category16("cardiac_care", ""),
        Category17("kidney_care", ""),
        Category18("ortho_care", ""),
        Category19("antibiotics", ""),
        Category20("sexual_wellness", ""),
        Category21("ent", ""),
        Category22("cold_Immunity", ""),
        Category23("piles_care", ""),
        Category24("personal_care", ""),
        Category25("health_devices", "")
    }

}