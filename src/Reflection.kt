import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

/**
 *The annotation [Name] can be applied to properties or classes to define their preferred name.
 */
@Target(AnnotationTarget.PROPERTY,AnnotationTarget.CLASS)
annotation class Name(
    val id: String
)


/**
 *The annotation [Attributes] can be applied to properties to mark them as an Attribute.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Attributes()

/**
 *The annotation [Tags] can be applied to both properties and classes to mark them as a Tag.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class Tags()

/**
 *The annotation [TagLeafs] can be applied to both properties and classes to mark them as a Tag_Leaf.
 */
@Target(AnnotationTarget.PROPERTY,AnnotationTarget.CLASS)
annotation class TagLeafs()

/**
 *  The annotation [Exclude] can be applied to properties to mark them for exclusion
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Exclude()

/**
 *The annotation [XmlString] can be applied to classes to change the toString function depending on the class given [str].
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlString(
    val str: KClass<out StringChange>
)

/**
 *The annotation [XmlAdapter] can be applied to classes to change the order of the objects depending on the class given [xmlAdapter].
 */
@Target(AnnotationTarget.CLASS)
annotation class XmlAdapter(
    val xmlAdapter:KClass<out AdapterXML>
)



/**
 *
 * This data class is annotated for XML serialization and deserialization, providing a structure for mapping XML elements and attributes to Kotlin properties.
 *
 * @property [codigo] The code of the FUC, represented as an XML attribute.
 * @property [nome] The name of the FUC, represented as a leaf tag in XML.
 * @property [ects] The ECTS credits for the FUC, represented as a leaf tag in XML.
 * @property [observacoes] Observations about the FUC, represented as a leaf tag in XML, but excluded from serialization and deserialization.
 * @property [avaliacao] The list of evaluation components for the FUC, represented as nested tags in XML.
 */
@Name("fuc")
@XmlAdapter(FUCAdapter::class)
data class FUC(
    @Attributes
    val codigo: String,
    @TagLeafs
    val nome: String,
    @TagLeafs
    val ects: Double,
    @TagLeafs
    @Exclude
    val observacoes: String,
    @Tags
    val avaliacao: List<ComponenteAvaliacao>
)
/**
 *
 * This data class is annotated for XML serialization and deserialization, providing a structure for mapping XML elements and attributes to Kotlin properties.
 *
 * @property [nome] The name of the evaluation component, represented as an XML attribute.
 * @property [peso] The weight of the evaluation component, represented as an XML attribute.
 *                It uses a custom string format, indicated by the `@XmlString` annotation.
 */
@Name("componente")
data class ComponenteAvaliacao(
    @Attributes
    val nome: String,
    @Attributes
    @XmlString(AddPercentage::class)
    val peso: Int
)

/**
 * Interface for transforming strings.
 */
interface StringChange {
    /**
     * Transforms the input string.
     *
     * @param [value] The input string to transform.
     * @return The transformed string.
     */
    fun transform(value: String): String
}
/**
 * Adds a percentage symbol to the end of a string.
 */
class AddPercentage : StringChange {
    /**
     * Adds a percentage symbol to the end of the input string.
     *
     * @param [value] The input string.
     * @return The input string with a percentage symbol added.
     */
    override fun transform(value: String): String {
        return "$value%"
    }
}

/**
 * Interface for XML adapters.
 */
interface AdapterXML{
    /**
     * Adapts a [Tag] entity.
     *
     * @param [tag] The entity to adapt.
     * @return The adapted entity.
     */
    fun adapter(tag: Tag) : Tag
}

/**
 * Adapter for processing FUC (Functional Unit of Content) entities in XML.
 *
 * This adapter is responsible for sorting the children of a tag entity based on a provided map of order values.
 */
class FUCAdapter : AdapterXML{

    /**
     * Adapts the [Tag] entity by sorting its children based on the provided map of order values.
     *
     * @param [tag] The Tag entity to be adapted.
     * @return The adapted Tag entity with its children sorted according to the order values.
     */
    override fun adapter(tag:Tag): Tag{
        val children = tag.children

        val map = mapOf(
            "nome" to 1,
            "ects" to 2,
            "avaliacao" to 3,
        )

        val sorted = children.sortedBy{
            getXMLOrder(it,map)
        }
        children.removeAll{true}
        children.addAll(sorted)
        return tag
    }

}

/**
 * Retrieves the order value for a given entity from the provided map.
 *
 * @param [entity] The entity for which to retrieve the order value.
 * @param [map] A map containing order values for entities.
 * @return The order value for the entity, or [Int.MAX_VALUE] if not found in the map.
 */
private fun getXMLOrder(entity: Entity,map:Map<String,Int>): Int {
    return map[entity.name] ?: Int.MAX_VALUE
}

/**
 * Retrieves the properties of a data class.
 *
 * @throws [IllegalArgumentException] if the class is not a data class.
 * @return The list of properties of the data class.
 */
val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }
/**
 * Retrieves the class name or the custom ID specified in the [Name] annotation.
 *
 * @param [clazz] The class for which to retrieve the name.
 * @return The class name or the custom ID.
 */
fun getClassName(clazz: KClass<*>) : String{
    return clazz.findAnnotation<Name>()?.id  ?: clazz.simpleName!!
}

/**
 * Retrieves the parameter name or the custom ID specified in the [Name] annotation.
 *
 * @param [prop] The property for which to retrieve the name.
 * @return The parameter name or the custom ID.
 */
fun getParameterName(prop: KProperty<*>) : String{
    return prop.findAnnotation<Name>()?.id  ?: prop.name
}

/**
 * Retrieves the value of a property from an object and applies transformations if specified by annotations.
 *
 * @param [prop] The property for which to retrieve the value.
 * @param [obj] The object from which to retrieve the property value.
 * @return The value of the property, possibly transformed based on annotations.
 */
fun getParameterValue(prop: KProperty<*>,obj:Any) : String{
    var parameterValue : String = prop.call(obj).toString()
    if (prop.hasAnnotation<XmlString>()) {
        val stringValue = prop.findAnnotation<XmlString>()!!.str
        val valueInstance = stringValue.objectInstance ?: stringValue.createInstance()
        parameterValue = valueInstance.transform(prop.call(obj).toString())
    }
    return parameterValue
}

/**
 * Creates an XML entity from an object, mapping properties to XML elements and attributes.
 * By default, the code without annotations will verify if an object is a Tag or a Tag_Leaf.
 * In order, to create an attribute it is mandatory an Annotation [Attribute]
 *
 * @param [obj] The object from which to create the XML entity.
 * @param [parent] The parent tag for the XML entity.
 * @return The XML entity representing the object.
 */
fun createXML(obj: Any, parent: Tag?) : Entity{
    val clazz = obj::class
    val root=Tag(getClassName(clazz))

    clazz.memberProperties.forEach() {
            when {
                it.hasAnnotation<Exclude>() -> return@forEach
                it.hasAnnotation<Attributes>() -> Attribute(getParameterName(it), getParameterValue(it,obj), root)
                it.hasAnnotation<TagLeafs>() -> Tag_Leaf(getParameterName(it), root, getParameterValue(it,obj))
                it.hasAnnotation<Tags>() -> {
                    val tag = Tag(getParameterName(it), root)
                    (it.call(obj) as? List<Any>)?.forEach() { componentes ->
                        val tags = createXML(componentes, tag)
                        tag.addEntity(tags) }
                }
                else -> {
                    if(it.call(obj) is Collection<*>){
                        val tag = Tag(getParameterName(it), root)
                        (it.call(obj) as? List<Any>)?.forEach() { componentes ->
                            val tags = createXML(componentes, tag)
                            tag.addEntity(tags) }
                    }else{
                        Tag_Leaf(getParameterName(it), root, getParameterValue(it,obj))
                    }
                }
            }
        }
    if(clazz.hasAnnotation<XmlAdapter>()) {
        val adapterOfTheXML = clazz.findAnnotation<XmlAdapter>()!!.xmlAdapter
        val adapterInstance = adapterOfTheXML.objectInstance ?: adapterOfTheXML.createInstance()
        return adapterInstance.adapter(root)
    }
    return root
}




fun main() {
    val fuc = FUC("M4310", "Programação Avançada", 6.0, "la la...",
        listOf(
            ComponenteAvaliacao("Quizzes", 20),
            ComponenteAvaliacao("Projeto", 80),
            ComponenteAvaliacao("Teste", 10)
        )
    )

    print(createXML(fuc,null).prettyPrint(0))
}