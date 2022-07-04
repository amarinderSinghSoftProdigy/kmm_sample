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
        Category1("ayurvedic", "1"),
        Category2("allopathic", "2"),
        Category3("homeopathic", "3"),
        Category4("otc", "4"),
        Category5("Veterinary", "5"),
        Category6("cough_respiratory", "6"),
        Category7("diabetic_care", "7"),
        Category8("eye_care", "8"),
        Category9("pain_relief", "9"),
        Category10("skin_care", "10"),
        Category11("vitamins_and_aupplements", "11"),
        Category12("metal_wellness", "12"),
        Category13("dental_care", "13"),
        Category14("liver_care", "14"),
        Category15("pediatrics", "15"),
        Category16("cardiac_care", "16"),
        Category17("kidney_care", "17"),
        Category18("ortho_care", "18"),
        Category19("antibiotics", "19"),
        Category20("sexual_wellness", "20"),
        Category21("ent", "21"),
        Category22("cold_Immunity", "22"),
        Category23("piles_care", "23"),
        Category24("personal_care", "24"),
        Category25("health_devices", "25")
    }

}