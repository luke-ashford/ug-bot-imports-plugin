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

class DatastoreResolver : PyClassMembersProvider {

    override fun getMembers(
        clazz: PyClassType?,
        location: PsiElement?,
        context: TypeEvalContext
    ): Collection<PyCustomMember?> {

        val cwdir = clazz?.pyClass?.parent?.parent as? PsiDirectory ?: return emptyList()
        if (cwdir.name != "Universes") return emptyList()

        val memberClasses = getDatastoreClasses(clazz.name!!, cwdir)
             .map { PyCustomMember(it.name!!, it) }

        return memberClasses
    }

    override fun resolveMember(
        type: PyClassType,
        name: String,
        location: PsiElement?,
        resolveContext: PyResolveContext
    ): PsiElement? {

        val cwdir = type.pyClass.parent.parent as? PsiDirectory ?: return null
        if (cwdir.name != "Universes") return null

        val clazz = getDatastoreClasses(type.name!!, cwdir)
            .firstOrNull { it.name!! == name }

        return clazz

    }

    private fun getDatastoreClasses(universe: String, cwdir: PsiDirectory): List<PyClass> {
        return (cwdir.parent as PsiDirectory)
            .children
            .filterIsInstance<PsiDirectory>()
            .firstOrNull { it.name == "Datastores" }
            ?.children
            ?.filterIsInstance<PsiDirectory>()
            ?.firstOrNull() { it.name == universe }
            ?.children
            ?.filterIsInstance<PyFile>()
            ?.flatMap { it.topLevelClasses.asSequence() }
            ?: emptyList()
    }

}