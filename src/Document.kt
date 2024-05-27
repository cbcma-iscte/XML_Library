import org.w3c.dom.Attr
import javax.print.Doc
/**
 * Class [Document].
 * @property [version] is a double that defines the version of the Document.
 * @property [encoding] is the encoding of the Document.
 * @property [rootTag] is the root of the Document.
 * @property [name] is the name of the Document.
 */
data class Document(
    val version: Double = 1.0,
    val encoding: String = "UTF-8",
    val rootTag: Tag,
    val name: String
)



/**
 * Converts the document to a formatted XML string.
 *
 * This function generates a pretty-printed XML string representation of the Document,
 * including the XML declaration with the version and encoding, and the root Tag's content.
 *
 * @return A formatted XML string representation of the Document.
 */
fun Document.prettyPrint():String{
    val str= StringBuilder();
    str.append("<?xml version=${version} encoding=${encoding}?>\n")
    str.append(rootTag.prettyPrint(0))
    return str.toString()
}


/**
 * This function adds an [Attribute] to all tags named [nameTag] in the [Document].
 *
 * @param [valueAttribute] is the value of the Attribute.
 * @param [valueAttribute] is the name of the Attribute.
 * @param [nameTag] specifies the tags within the Document that should receive the Attribute.
 *  */
fun Document.addAttributeGlobally(nameTag : String, nameAttribute : String, valueAttribute: String){
    var theTagList : MutableList<Attribute> = mutableListOf()
    rootTag.accept {
        if(it.name.equals(nameTag)){
            Attribute(nameAttribute,valueAttribute,it)
            theTagList = it.listAttribute
        }
        true
    }

}

/**
 *Changes the name of an [Entity] globally within the [Document].
 *
 *This function traverses the Document and changes the name of all entities that match the specified [oldNameTag] to the new name [newNameTag].
 *
 * The [changeName] function is called for each matching tag, which includes validation to ensure the new name is acceptable.
 *
 * @param [oldName] is the current name of the entities to be changed.
 * @param [newName] is the new name to assign to the entities.
 *
 *  */
fun Document.changeTagNameGlobally(oldNameTag : String, newNameTag : String){
    rootTag.accept {
        if(it.name.equals(oldNameTag)){
            it.changeName(newNameTag)
        }
        true
    }

}
/**
 *Changes the name of an [Attribute] globally within all [Entity] in the [Document].
 *
 * This function traverses the document and changes the name of all attributes that match the specified [oldNameAttribute] to the new name [newNameAttribute] within entities that have the specified [nameTag].
 *
 * The [changeNameTo] function is called for each matching Attribute, which includes validation to ensure the new name is acceptable.
 *
 * @param nameTag The name of the entities to look for.
 * @param oldNameAttribute The current name of the attributes to be changed.
 * @param newNameAttribute The new name to assign to the attributes.
 *
 *  */
fun Document.changeAttributeNameGlobally(nameTag : String, oldNameAttribute : String, newNameAttribute: String) {
    var theTagList : MutableList<Attribute> = mutableListOf()
    rootTag.accept {
        if(it.name.equals(nameTag)){
            it.listAttribute.map{
                if(it.name.equals(oldNameAttribute))
                    it.changeNameTo(newNameAttribute)
            }
            theTagList = it.listAttribute
        }
        true
    }

}

fun Document.removeEntityGlobally(nameTag : String) {
    var experiencia : MutableList<Entity> = mutableListOf()
    rootTag.accept {
        if(it.name == nameTag && it.parent!=null){
            experiencia.add(it)
        }
        true
    }
    experiencia.forEach{
        it.remove()
    }


}
/**
 * Removes the specified [Attribute] globally.
 *
 * This function traverses the document and removes all the attributes with the given [nameAttribute] from all entities that match the specified [nameTag].
 *
 * @param nameTag the name of the entities to search for.
 * @param nameAttribute the name of the attributes to remove.
 */
fun Document.removeAttributeGlobally(nameTag: String, nameAttribute: String) {
    val listTagAttribute :MutableList<Pair<Entity,Attribute>> = mutableListOf()
    rootTag.accept { entity->
        if(entity.name == nameTag){
            entity.listAttribute.forEach{atribute ->
                if(atribute.name.equals(nameAttribute))
                    listTagAttribute.add(Pair(entity, atribute) as Pair<Entity, Attribute>)
            }
        }
        true
    }
    listTagAttribute.forEach { (entity, attribute) ->
        entity.removeAttribute(attribute)
    }

}
/**
 * Performs a XPath search within the [Document] based on the provided [path].
 *
 * This function finds nodes within the Document that match the provided [path] and returns them.
 *
 * @param [path] list representing the path to search for.
 * @return A MutableList<[Tag_Leaf]> nodes that match the provided path.
 * @throws IllegalArgumentException if any path component is invalid.
 */
fun Document.microXpath(path: MutableList<String>) : MutableList<Tag_Leaf>{
    path.forEach{(isNameInvalid(it))}
    val pathFinal:MutableList<Tag_Leaf> = mutableListOf()
    rootTag.accept {
        if (path.size>0 && it.name == path.last() && it is Tag_Leaf)
            pathFinal.add(it)
        true
    }
    pathFinal.filter {
        if(path.size>0)path.removeAt(path.lastIndex)
        it.pathAccepted(path)
    }
    //println(prettyPrintXpath(pathFinal))
    //println(pathFinal.size)
    return pathFinal
}


/**
 * Generates a string representation of a list of [Tag_Leaf] nodes.
 *
 * This function creates a formatted string containing the pretty-printed representation of each [Tag_Leaf] in the provided list.
 *
 * @param [listTagLeafs] the list of Tag_Leaf nodes to be pretty-printed.
 * @return A string representation of the list of Tag_Leaf nodes.
 */

fun prettyPrintXpath(listTagLeafs : MutableList<Tag_Leaf>) : String {
    val s = StringBuilder()

    listTagLeafs.forEach(){
        s.append(it.prettyPrintAll())
    }
    return s.toString()
}
