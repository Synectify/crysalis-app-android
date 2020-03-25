package com.tangem.tangemtest._arch.structure.abstraction

import com.tangem.tangemtest._arch.structure.DataHolder
import com.tangem.tangemtest._arch.structure.Id
import com.tangem.tangemtest._arch.structure.Payload

/**
 * Created by Anton Zhilenkov on 22/03/2020.
 */
interface Item : Payload {
    val id: Id
    var parent: Item?
}

abstract class BaseItem<D>(
        override var viewModel: ItemViewModel<D>
) : Item, DataHolder<ItemViewModel<D>> {
    override var parent: Item? = null
    override val payload: MutableMap<String, Any?> = mutableMapOf()
}