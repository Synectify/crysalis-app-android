package com.tangem.tangemtest.card_use_cases.ui.personalize.widgets.impl.block

import android.view.ViewGroup
import com.tangem.tangemtest.R
import com.tangem.tangemtest._arch.structure.abstraction.BaseItem
import com.tangem.tangemtest._arch.structure.abstraction.ItemViewModel
import com.tangem.tangemtest._arch.structure.abstraction.ListItemBlock
import com.tangem.tangemtest.card_use_cases.ui.personalize.widgets.abstraction.BaseBlockWidget

/**
 * Created by Anton Zhilenkov on 24/03/2020.
 */
class LinearBlockWidget(
        parent: ViewGroup,
        private val children: ListItemBlock
) : BaseBlockWidget(parent) {
    override fun getLayoutId(): Int = R.layout.w_personilize_block

    override var viewModel: List<ItemViewModel<*>> = listOf()
        get() = children.itemList.map { it as BaseItem<*> }.map { it.viewModel }
}