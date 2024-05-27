/**
 * Interface [Entity]
 * @property [name] is the name of the Entity.
 * @property [parent] parent of an entity, this parameter is not mandatory for every Entity.
 * @property [listAttribute] list of all the attributes of an Entity.
 */
sealed interface Entity {
    var name: String
    val parent:Tag?
    val listAttribute: MutableList<Attribute>
    fun accept(visitor: (Entity) -> Boolean) {
        visitor(this)
    }

    val depth: Int
        get() = if(parent==null)
            1
        else
            1+parent!!.depth

    /**
    * This function generates a pretty-printed string representation of the [Entity].
    *
    * @param [depth] The current depth of the Entity in the document tree.
    * @return The pretty-printed string representation.
    */
    fun prettyPrint(depth: Int): String

    /**
     *  This function checks if the path is accepted by the [Entity], considering the path and its ancestors.
     *
     * @param [path] The path to check.
     * @return True if the path is accepted, false otherwise.
     */
    fun pathAccepted(path: MutableList<String>): Boolean
}

