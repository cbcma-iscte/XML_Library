import com.sun.source.doctree.AttributeTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import javax.print.Doc

class Testing{

        val plano : Tag = tag("plano"){

            tagLeaf("curso","Mestrado em Engenharia Informatica")

            tag("fuc"){
                attribute("codigo", "M0000")
                tagLeaf("ects","6.0")
                tag("avaliacao"){
                    tagLeaf("componente"){
                        attribute("nome", "teste")
                        attribute("peso","40%")
                    }
                }
            }

            tag("fuc"){
                attribute("codigo", "M1111")
                tag("avaliacao"){
                    tagLeaf("componente"){
                        attribute("nome","Dissertação")
                        attribute("peso","60%")
                    }
                }
            }
        }
    @Test
    fun verifyDSL(){
        val ano = Tag("AnoCurricular")
        val disciplina = Tag_Leaf("disciplina",ano,"Programação Avançada")
        val attribute_ano = Attribute("Data","2023-24",ano)
        val attribute_disciplina = Attribute("Ects","6",disciplina)

        val anocurricular : Tag = tag("AnoCurricular") {
            attribute("Data", "2023-24")
            tagLeaf("disciplina", "Programação Avançada"){
                attribute("Ects","6")
            }
        }

        assertEquals(ano,anocurricular)


        val path : MutableList<String> = mutableListOf("fuc","avaliacao","componente")
        val path_DSL: MutableList<String> = "fuc"/"avaliacao"/"componente"

        assertEquals(path,path_DSL)

    }
        @Test
    fun testAddTag(){
        val ano = Tag("AnoCurricular")
        val disciplina = Tag_Leaf("disciplina",plano)
        plano.addEntity(ano)

        assertEquals(ano,plano["AnoCurricular"])
        assertEquals(listOf(plano["curso"],plano["fuc"],plano["fuc",1],disciplina,ano),plano.children)
        assertThrows<InvalidNameException> { val curriculo = Tag("") }
        assertThrows<InvalidNameException> { val unidadeCurricular = Tag_Leaf("Unidade Curricular",ano) }

    }

    @Test
    fun testRemoveTag(){
        plano["fuc",1]["avaliacao"].remove()

        assertEquals(emptyList<Entity>(),(plano["fuc",1]as Tag).children)
    }

    @Test
    fun testAddAttribute(){

        val projeto = Attribute("projeto","80%")
        plano.addAttribute(projeto)

        val teste = plano.attribute("teste","20%")

        assertNotEquals(listOf(projeto),plano.listAttribute)
        assertEquals(listOf(projeto,teste),plano.listAttribute)
        assertThrows<DuplicateAttributeException> { plano.attribute("teste","20%") }

        //fuc.addAttribute(tese) //-> Error
    }

    @Test
    fun testRemoveAttribute(){

        val codigo = Attribute("codigo","20234",plano)
        val avaliacao = Attribute("avaliacao","23412",plano)

        plano.removeAttribute(avaliacao)

        assertEquals(listOf(codigo),plano.listAttribute)
        assertNotEquals(emptyList<Entity>(),plano.listAttribute)
    }

    @Test
    fun testChangeAttribute(){

        val previousName =  plano["fuc"].listAttribute[0].name

        plano["fuc"].listAttribute[0].changeValueTo("Teste")
        plano["fuc"].listAttribute[0].changeNameTo("Percentagem")

        assertEquals("Teste",plano["fuc"].listAttribute[0].value)
        assertEquals("Percentagem",plano["fuc"].listAttribute[0].name)
        assertNotEquals(previousName,plano["fuc"].listAttribute[0].name)
    }

    @Test
    fun testAccessParentAndChildren(){
        assertEquals(listOf(plano["curso"],plano["fuc"],plano["fuc",1]),plano.children)
        assertEquals(plano,plano["curso"].parent)
        assertNotEquals(plano,plano["fuc"]["avaliacao"])
    }

    @Test
    fun testPrettyPrint(){
        val disciplina = Tag("disciplina")
        val avaliacao = Tag("avaliacao",parent=disciplina)
        val componente = Tag_Leaf("componente",parent=avaliacao, text = "Hello World")
        val componente1 = Tag_Leaf("fuc"    ,parent=avaliacao)
        val peso = Attribute("peso","20234",componente)


        val xmlString = """<disciplina>
	<avaliacao>
		<componente peso='20234'> Hello World </componente>
		<fuc/>
	</avaliacao>
</disciplina>
"""
        assertEquals(xmlString,disciplina.prettyPrint(0))
    }

    @Test
    fun testVisitor(){
        assertEquals(listOf(plano["fuc"],plano["fuc"]["ects"], plano["fuc"]["avaliacao"], plano["fuc"]["avaliacao"]["componente"]),plano["fuc"].visitorLook())
        assertNotEquals(listOf(plano["fuc",1],plano["fuc",1]["avaliacao"]),plano["fuc",1].visitorLook())
    }


    @Test
    fun testAddAttribute_Global(){ //(fornecendo nome da entidade, nome e valor do atributo)


        val doc = Document(rootTag = plano, name = "Document")
        val tagLeaf = Tag_Leaf("Teste",plano,"Esta unidade currícular somente apresenta um teste na sua avaliacao")
        doc.addAttributeGlobally("plano","disciplina","PA")

        assertEquals("disciplina",plano.listAttribute[0].name)
        assertNotEquals("curso",plano.listAttribute[0].name)
        assertThrows<DuplicateAttributeException> { plano["fuc"].attribute("codigo","123") }
        //print(doc.prettyPrint())
    }


    @Test
    fun testChangeNameTag_Global(){ //(fornecendo nome antigo e nome novo)

        val doc = Document(rootTag = plano, name = "Document")
        val tagLeaf = Tag_Leaf("Teste",plano,"Esta unidade currícular somente apresenta um teste na sua avaliacao")
        doc.changeTagNameGlobally("plano","root")


        assertNotEquals("plano",plano.name)
        assertEquals("root",plano.name)


    }

    @Test
    fun testChangeNameAttribute_Global(){

        val doc = Document(rootTag = plano,name="Teste")


        doc.changeAttributeNameGlobally("fuc","codigo","valor")
        assertEquals("valor",plano["fuc"].listAttribute[0].name)
        assertEquals("valor",plano["fuc",1].listAttribute[0].name)
        assertNotEquals("codigo",plano["fuc",1].listAttribute[0].name)
        //print(doc.prettyPrint())

    }


    @Test
    fun testRemoveTag_Global(){

        val doc = Document(rootTag = plano, name = "Teste")

        doc.removeEntityGlobally("fuc")
        assertEquals(mutableListOf(plano["curso"]),plano.children)
        assertNotEquals(emptyList<Entity>(),plano.children)
        //print(doc.prettyPrint())


    }

    @Test
    fun removeAttribute_Global(){

        val doc = Document(rootTag = plano, name = "Teste")

        doc.removeAttributeGlobally("componente","peso")
        doc.removeAttributeGlobally("componente","tese")
        assertEquals(mutableListOf( plano["fuc"]["avaliacao"]["componente"].listAttribute[0]),plano["fuc"]["avaliacao"]["componente"].listAttribute)
        //print(doc.prettyPrint())

    }


    @Test
    fun testWriteDocument(){


        val doc = Document(rootTag = plano, name = "Teste")
        val resultado : String = """<?xml version=1.0 encoding=UTF-8?>
<plano>
	<curso> Mestrado em Engenharia Informatica </curso>
	<fuc codigo='M0000'>
		<ects> 6.0 </ects>
		<avaliacao>
			<componente nome='teste',  peso='40%'/>
		</avaliacao>
	</fuc>
	<fuc codigo='M1111'>
		<avaliacao>
			<componente nome='Dissertação',  peso='60%'/>
		</avaliacao>
	</fuc>
</plano>
"""


        //println(doc.prettyPrint())
        assertEquals(resultado,doc.prettyPrint())
        assertNotEquals(null,doc.prettyPrint())

    }

    @Test
    fun testXPathGlobal(){
        val path: MutableList<String> = "fuc"/"avaliacao"/"componente"

        val doc = Document(rootTag = plano, name = "Teste")

        val Xpath : MutableList<Tag_Leaf> = doc.microXpath(path)
        assertEquals(mutableListOf(plano["fuc"]["avaliacao"]["componente"],plano["fuc",1]["avaliacao"]["componente"]),Xpath)
        assertNotEquals(emptyList<Tag_Leaf>(),Xpath)

    }
    @Test
    fun testNameDif(){ //verification if our exceptions for the names are working
        val root = Tag("root")
        val componente = Attribute("componente","123",root)

        val componente_repeat = Attribute("componente","555")

        assertThrows<DuplicateAttributeException> { root.addAttribute(componente_repeat) }
        assertThrows<InvalidNameException> { Tag("''?") }
        assertThrows<InvalidNameException> { root.changeName("1!!!!")}
    }

}