import kotlin.jvm.Throws
/**
 * Creates a [Tag] with the specified name and applies customizations using a build lambda.
 *
 * @param [name] The name of the tag.
 * @param [build] A lambda for customizing the tag.
 * @return The created tag.
 */
fun tag(name:String, build:Tag.() -> Unit)=
    Tag(name).apply{
        build(this)
    }

/**
 * Creates a nested [Tag] under the current [Tag] with the specified name and applies customizations using a build lambda.
 *
 * @param [name] The name of the nested tag.
 * @param [build] A lambda for customizing the nested tag.
 * @return The created nested tag.
 */
fun Tag.tag(name:String,build:Tag.() -> Unit)=
    Tag(name,this).apply{
        build(this)

}

/**
 * Creates a nested [Tag] under the current [Tag] with the specified name.
 *
 * @param [name] The name of the nested tag.
 * @return The created nested tag.
 */
fun Tag.tag(name:String)= Tag(name,this)

/**
 * Creates a [Tag_Leaf] under the current [Tag] with the specified name and optional text.
 *
 * @param [name] The name of the tag_leaf.
 * @param [txt] Optional text content for the tag_leaf.
 * @return The created tag_leaf.
 */
fun Tag.tagLeaf(name: String, txt: String? = "")=Tag_Leaf(name, this, txt)

/**
 * Creates a [Tag_Leaf] under the current [Tag] with the specified name, optional text content, and applies customizations using a build lambda.
 *
 * @param [name] The name of the tag_leaf.
 * @param [txt] Optional text content of the tag_leaf. Defaults to an empty string if not provided.
 * @param [build] A lambda for customizing the tag_leaf.
 * @return The created tag_leaf.
 */
fun Tag.tagLeaf(name:String,txt:String?="",build: Tag_Leaf.() -> Unit)
     =Tag_Leaf(name,this,txt).apply {
        build(this)
}

/**
 * This operator function is used to concatenate two strings into a MutableList, where the current string is the first element
 * followed by the specified string as the second element. It ensures that both strings are valid names.
 *
 * @param [str] The string to concatenate with the current string.
 * @return A MutableList containing the current string followed by the specified string.
 * @throws [InvalidNameException] If either the current string or the specified string is invalid as per naming conventions.
 */
operator fun String.div(str:String):MutableList<String> {
    isNameInvalid(this)
    isNameInvalid(str)
    return mutableListOf(this,str)
}

/**
 * This operator function is used to append a string to the end of the current MutableList of strings.
 * It ensures that the specified string is a valid name before appending it to the list.
 *
 * @param [str] The string to append to the current MutableList.
 * @return The updated MutableList with the specified string appended to it.
 * @throws [InvalidNameException] If the specified string is invalid as per naming conventions.
 */
operator fun MutableList<String>.div(str:String):MutableList<String> {
    isNameInvalid(str)
    this.add(str)
    return this
}

/**
 * This function is used to create a new [Attribute] instance with the provided name and value,
 * add it to the list of attributes of the current [Entity], and return the newly created [Attribute].
 * It ensures that the created attribute is properly associated with the current Entity.
 *
 * @param [name] The name of the attribute to create.
 * @param [value] The value of the attribute to create.
 * @throws [DuplicateAttributeException] if there's already an attribute with the same name as attribute in the parent's list
 * @return The newly created Attribute instance.
 */
fun Entity.attribute(name:String,value:String): Attribute{
    val newAttribute = Attribute(name,value)
    this.addAttribute(newAttribute)
    return newAttribute
}


/**
 * Retrieves a child [Entity] with the specified name and optional index from the current entity.
 *
 * This operator function is used to retrieve a child entity with the specified name from the current entity,
 * optionally specifying the index if there are multiple entities with the same name.
 * It ensures that the current entity is a [Tag] before attempting to retrieve the child entities.
 *
 * @param [name] The name of the child entity to retrieve.
 * @param [index] The index of the child entity to retrieve if there are multiple entities with the same name. Defaults to 0.
 * @return The child [Entity] with the specified name and index.
 * @throws [InvalidEntityException] If the current entity is not a tag.
 */
operator fun Entity.get(name:String,index:Int=0):Entity{
    isTag(this)
    val entityChild =(this as Tag).children.filter {
        it.name==name
    }.get(index)

    return entityChild
}


