/**Class [InvalidNameException].
 *
 * @throws InvalidNameException when the name is invalid.
 */
class InvalidNameException(message: String) : Exception(message)

/**Class [DuplicateAttributeException].
 *
 * @throws DuplicateAttributeException when the attribute already exists in the list of attributes of an Entity.
 */
class DuplicateAttributeException(message: String) : Exception(message)

/**Class [RemovalNotAllowedException].
 *
 * @throws RemovalNotAllowedException when the Entity the user wants to remove doesn't have a parent.
 */
class RemovalNotAllowedException(message: String) : Exception(message)

/**Class [InvalidEntityException].
 *
 * @throws InvalidEntityException when the Entity is not a Tag, and we want to access the Entity children.
 */
class InvalidEntityException(message: String) : Exception(message)

/**
 * This function verifies if a name of an [Entity] or an [Attribute] is valid.
 *
 * @param [name] String related to the name to be validated.
 * @throws [InvalidNameException] When [name] is invalid.
 */

fun isNameInvalid(name:String){
    val allowedCharactersRegex = Regex("^[:A-Za-z][-.:A-Za-z0-9]*$")
    if(!name.contains(allowedCharactersRegex) || name.isEmpty())
        throw InvalidNameException("Invalid name: $name")
}
/**
 * This function verifies if a specific [Entity] is a [Tag].
 *
 * @param [entity] is the Entity we want to verify.
 * @throws [InvalidEntityException] When the [entity] is not a Tag.
 */
fun isTag(entity: Entity){
    if(entity !is Tag)
        throw InvalidEntityException("Your entity is not a Tag, so it doesn't have children")
}


