/**
 * Class [Tag]
 *
 * @property [name] is the name of the Tag.
 * @property [parent] parent of an entity, this parameter is not mandatory for every Tag.
 * @property [listAttribute] list of all the attributes of a Tag.
 * @property  [children] list of all entities children of the Tag.
 */
data class Tag( //Entity com parent e com children
    override var name: String,
    override val parent: Tag? =null,
):Entity {
    override val listAttribute: MutableList<Attribute> = mutableListOf()

    val children: MutableList<Entity> = mutableListOf()

    init {
        parent?.children?.add(this)
        isNameInvalid(name)

    }
    /**
     * This function accepts a visitor function and applies it to this tag and its children recursively.
     *
     * @param [visitor] The visitor function to apply.
     */
    override fun accept(visitor: (Entity) -> Boolean) {
        if (visitor(this))
            children.forEach {
                it.accept(visitor)
            }
    }

    /**
     * This function checks if the path is accepted by this [Tag], considering the path and its ancestors.
     *
     * @param [path] The path to check.
     * @return True if the path is accepted, false otherwise.
     */
    override fun pathAccepted(path:MutableList<String>):Boolean{
        if(path.size==0)
            return true
        if(path.last()==name){
            path.removeAt(path.lastIndex)
            return parent!!.pathAccepted(path)
        }
        return false

    }

    /**
     * This function generates a pretty-printed string representation of this [Tag] and its children.
     *
     * @param [depth] The current depth of the Tag in the document tree.
     * @return The pretty-printed string representation.
     */
    override fun prettyPrint(depth: Int): String {
        val builder = StringBuilder()
        if(this.children.isEmpty()){
            return "${"\t".repeat(depth)}<" + this.name + this.listAttribute.joinToString{
                    it.prettyPrint() } + "\\>\n"
        }
        builder.append("${"\t".repeat(depth)}${this.prettyPrintBeginning()}\n")
        children.forEach { builder.append(it.prettyPrint(depth + 1)) }
        builder.append("${"\t".repeat(depth)}${this.prettyPrintEnding()}\n")
        return builder.toString()
    }

    /**
     * This function generates the opening Tag as a string.
     *
     * @return The opening Tag string.
     */
    fun prettyPrintBeginning():String{
       return "<" + this.name + this.listAttribute.joinToString{
           it.prettyPrint()
       } + ">"
    }

    /**
     * This function generates the closing Tag as a string.
     *
     * @return The closing Tag string.
     */
    fun prettyPrintEnding():String{
        return "</" + this.name + ">"
    }
}

/**
 * Class [Tag_Leaf]
 *
 * @property [name] is the name of the Tag_Leaf.
 * @property [parent] parent of an entity, this parameter is mandatory for every Tag_Leaf.
 * @property [text] The text content of the Tag_Leaf, which can be null or empty.
 * @property [listAttribute] list of all the attributes of a Tag_Leaf.
 */
data class Tag_Leaf( //entity com parent e sem children
    override var name: String,
    override val parent: Tag,
    var text: String?="",
):Entity {
    override val listAttribute: MutableList<Attribute> = mutableListOf()

    init {
        parent.children.add(this)
        isNameInvalid(name)
    }

    /**
     * This function checks if the path is accepted by this [Tag_Leaf], considering the path and its ancestors.
     *
     * @param [path] The path to check.
     * @return True if the path is accepted, false otherwise.
     */
    override fun pathAccepted(path: MutableList<String>): Boolean{
        if(path.size==0)
            return true
        path.removeAt(path.lastIndex)
        return this.parent.pathAccepted(path)
    }

    /**
     * This function generates a pretty-printed string representation of this [Tag_Leaf].
     *
     * @param [depth] The current depth of the Tag_Leaf in the document tree.
     * @return The pretty-printed string representation.
     */
    override fun prettyPrint(depth: Int ): String {
        return "${"\t".repeat(depth)}${this.prettyPrintAll()}"
    }

    /**
     * This function generates the full pretty-printed string representation of this [Tag_Leaf].
     *
     * @return The pretty-printed string representation of the Tag_Leaf.
     */
    fun prettyPrintAll():String{
        return "<" + this.name + this.listAttribute.joinToString{
            it.prettyPrint()
        } + if(this.text.equals("")){
            "/>\n"
        }else{
            "> " + this.text + " </" + this.name + ">\n"
        }
    }
}

/**
 * This function adds an Entity [entity] to a [Tag].
 */
fun Tag.addEntity(entity: Entity){
    this.children.add(entity)
}

/**
 * This function removes the [Entity] from its parent's children list and all the entities children.
 * If the Entity has no parent, an exception is thrown indicating that the root Entity cannot be removed.
 *
 * @throws RemovalNotAllowedException if the entity is a root Entity and has no parent.
 */
fun Entity.remove() {
    if(parent==null)
        throw RemovalNotAllowedException("The root entity cannot be removed.")
    this.parent!!.children.remove(this)
}
/**
 * Removes the specified [attribute] from the [Entity]'s list of attributes.
 *
 * If the Attribute is present in the list, it will be removed. If the Attribute is not found,
 * no action is taken.
 *
 * @param attribute The Attribute to be removed.
 */
fun Entity.removeAttribute(attribute: Attribute){
    if(this.listAttribute.contains(attribute))
        this.listAttribute.remove(attribute)
}

/**
 * Gathers all entities in the document tree starting from this [Entity].
 *
 * This function uses the visitor pattern to traverse the Document tree and collect all entities.
 *
 * @return A list of all entities in the Document tree starting from this entity.
 */
fun Entity.visitorLook(): MutableList<Entity>{
    val visitors: MutableList<Entity> = mutableListOf()
    this.accept(){
        visitors.add(it)
    true
    }
    return visitors
}

/**
 * This function adds an attribute [attribute] to the list of attributes of an [Entity]
 * Throws a [DuplicateAttributeException] if that Attribute's name already exists in the list
 */
fun Entity.addAttribute(attribute : Attribute){
    if(this.listAttribute.any { it.name == attribute.name })
        throw DuplicateAttributeException("Attribute with name ${attribute.name} already exists.")

    this.listAttribute.add(attribute)

}

/**
 * This function changes the current name of an Entity to a new name: [newName].
 * Uses the function [isNameInvalid] to verify if the name is accepted.
 */
fun Entity.changeName(newName:String){
    isNameInvalid(newName)
    this.name=newName

}


