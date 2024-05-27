import com.sun.source.doctree.AttributeTree
import org.w3c.dom.Attr

/**
 * Class [Attribute].
 * @property [name] is the name of the Attribute.
 * @property [value] is the value of the Attribute.
 * @property [parent] is not mandatory when the Attribute is created.
 * @throws [DuplicateAttributeException] if there's already an attribute with the same name as Attribute in the parent's list*/

data class Attribute(
    var name: String,
    var value: String,
    val parent: Entity? = null
){
    init {

        if(parent!=null)
            parent.addAttribute(this)
        isNameInvalid(name)
    }

}

/**
 * This function creates a formatted string containing the Attribute's name and value
 *  * in the format: ` name='value'`.
 *
 * @return the Attribute information (name and value) in the form of a String.
 */
fun Attribute.prettyPrint():String{
    return " " + this.name + "='" + this.value + "'"
}

/**
 * This function changes the current value of the Attribute to a new value.
 *
 * @param [newValue] The new value to be assigned to the Attribute.
 */
fun Attribute.changeValueTo(newValue: String){
    this.value = newValue
}

/**
 * This function changes the name of the Attribute to a new name.
 * Verifies if [newName] is a valid name by using the method [isNameInvalid].
 *
 * @param newName The new name to be assigned to the Attribute.
 * @throws [InvalidNameException] When [newName] is invalid.
 *
 */

fun Attribute.changeNameTo(newName: String){
    isNameInvalid(name)
    this.name = newName

}