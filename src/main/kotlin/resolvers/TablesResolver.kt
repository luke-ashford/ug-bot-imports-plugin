package resolvers

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.jetbrains.python.codeInsight.PyCustomMember
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.resolve.PyResolveContext
import com.jetbrains.python.psi.types.PyClassMembersProvider
import com.jetbrains.python.psi.types.PyClassType
import com.jetbrains.python.psi.types.TypeEvalContext

class TablesResolver : PyClassMembersProvider {

    override fun getMembers(
        clazz: PyClassType?,
        location: PsiElement?,
        context: TypeEvalContext
    ): Collection<PyCustomMember?> {

        if (clazz?.name != "Cache" && clazz?.name != "Database") return emptyList()

        val dir = clazz.pyClass.parent.parent as? PsiDirectory ?: return emptyList()

        val memberClasses = getTablesClasses(dir)
            .map { PyCustomMember(convertToSnakeCase(convertToSnakeCase(it.name!!)), it) }

        return memberClasses

    }

    override fun resolveMember(
        type: PyClassType,
        name: String,
        location: PsiElement?,
        resolveContext: PyResolveContext
    ): PsiElement? {

        if (type.pyClass.name != "Cache" && type.pyClass.name != "Database") return null

        val dir = type.pyClass.parent.parent as? PsiDirectory ?: return null

        val clazz = getTablesClasses(dir)
            .firstOrNull { convertToSnakeCase(it.name!!) == name }

        return clazz

    }

    private fun getTablesClasses(dir: PsiDirectory): List<PyClass> {
        val tablesDir = dir.children
            .filterIsInstance<PsiDirectory>()
            .firstOrNull { it.name == "tables" }
            ?: return emptyList()

        return tablesDir.children
            .filterIsInstance<PyFile>()
            .flatMap { it.topLevelClasses.asSequence() }
    }

    private fun convertToSnakeCase(name: String): String {
        return name.replace(Regex("(?<!^)(?=[A-Z])"), "_").lowercase()
    }

}